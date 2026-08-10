# Обработка исключений в корутинах

Главное отличие от обычного кода: исключение в корутине — **не только твоя проблема**. Необработанное исключение поднимается по иерархии `Job` к родителю, и родитель отменяет **всех остальных детей**. Это цена structured concurrency: сломалась часть — незачем доделывать остальное.

## Правило №1: `try/catch` вокруг билдера не работает
```kotlin
try {
    scope.launch { throw RuntimeException("boom") }   // НЕ поймается
} catch (e: Exception) { }
```
`launch` возвращает управление сразу, исключение случится позже и в другом потоке. Оборачивать нужно **код внутри** корутины:
```kotlin
scope.launch {
    try {
        loadData()
    } catch (e: Exception) {
        showError(e)
    }
}
```
Либо `runCatching` — та же обёртка `try/catch`, но с `Result`:
```kotlin
scope.launch {
    runCatching { loadData() }
        .onSuccess { render(it) }
        .onFailure { showError(it) }
}
```

## Правило №2: launch и async ведут себя по-разному
- **`launch`** — исключение **пробрасывается сразу**, вверх по иерархии, до `CoroutineExceptionHandler` или до краха приложения.
- **`async`** — исключение **запоминается в `Deferred`** и выбрасывается в момент `await()`. Поэтому его ловят через `try/catch` вокруг `await()`, а `CoroutineExceptionHandler` для `async` бесполезен.

```kotlin
val deferred = scope.async { throw RuntimeException("boom") }   // тут тихо
try {
    deferred.await()      // а вот тут прилетит
} catch (e: Exception) { }
```
**Ловушка**: «отложенность» касается только доставки *тебе*. Если `async` запущен в обычном (не supervisor) scope, родитель будет отменён **сразу**, даже если `await()` никто не вызовет. Не отменяет родителя только `async` внутри `supervisorScope`.

## Правило №3: CancellationException — особое
Отмена корутины реализована через `CancellationException`, и корутинный механизм её **игнорирует**: она не считается ошибкой и не отменяет родителя. Отсюда самая частая ошибка:
```kotlin
try {
    doWork()
} catch (e: Exception) {   // ловит и CancellationException — отмена сломана!
    log(e)
}
```
Правильно — пробросить её дальше:
```kotlin
catch (e: CancellationException) { throw e }
catch (e: Exception) { log(e) }
```
Ещё поэтому `runCatching` в корутинах опасен: он ловит все `Throwable`, включая отмену. См. [[Coroutines. Cancellation]].

## CoroutineExceptionHandler
Последний рубеж: «глобальный catch» для корутины, чтобы приложение не упало. Ставится в контекст scope или корневого `launch`.
```kotlin
val handler = CoroutineExceptionHandler { context, throwable ->
    println("Поймали: ${throwable.message}")
}
val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + handler)

scope.launch { throw RuntimeException("boom") }   // handler сработает
```
Три ограничения, о которых спрашивают:
1. Работает **только у корневой** корутины scope. Передать handler в дочерний `launch` или через `withContext` — он будет **проигнорирован**, исключение всё равно уйдёт наверх.
2. Не работает с `async`: там исключение живёт в `Deferred`.
3. Он **не предотвращает отмену** — к моменту вызова handler корутина и её братья уже отменены. Это про логирование и показ ошибки, а не про восстановление.

## SupervisorJob и supervisorScope
Меняют направление распространения: ошибка ребёнка **не** отменяет родителя и братьев.
```kotlin
supervisorScope {
    launch { throw RuntimeException("упал только я") }
    launch { delay(1000); println("а я доработаю") }
}
```
- `SupervisorJob()` — элемент контекста, для долгоживущего scope (`viewModelScope` устроен так же).
- `supervisorScope { }` — suspend-функция, для локальной группы независимых задач.

Ловушка: supervisor изолирует **только прямых детей**. Внутренний `coroutineScope { }` со своим `Job` отменит своих детей как обычно. И ещё: в `supervisorScope` каждый ребёнок обязан обработать ошибку сам (`try/catch` или handler), иначе она просто улетит в handler/крэш.

## Сводная таблица

| Способ | `launch` | `async` | Отменяет соседей | Когда применять |
| --- | --- | --- | --- | --- |
| `try/catch` внутри корутины | ✅ | ✅ | нет (обработано на месте) | основной способ |
| `try/catch` вокруг `await()` | — | ✅ | зависит от scope | результат `async` |
| `CoroutineExceptionHandler` | ✅ (только корневой) | ❌ | да, к моменту вызова уже отменены | логирование, глобальный экран ошибки |
| `supervisorScope` / `SupervisorJob` | ✅ | ✅ | ❌ изолирует | независимые параллельные задачи |

## Как это выглядит на практике (Android)
```kotlin
class MyViewModel : ViewModel() {
    // viewModelScope уже содержит SupervisorJob
    fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        try {
            val data = withContext(Dispatchers.IO) { repo.load() }
            _state.update { it.copy(data = data, isLoading = false) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isLoading = false) }
        }
    }
}
```
В data-слое ошибки чаще заворачивают в `Result`/`Either` и вообще не дают им подниматься исключениями — тогда UI-слой просто разбирает результат.

Для `Flow` — отдельный оператор `catch { }`, `try/catch` вокруг `collect` ловит не всё. См. [[4 Flow 3 Exceptions (catch, retry)]].

![](<../../images/Pasted image 20250305094554.png>)

## Вопросы-ловушки
- Почему `try/catch` вокруг `launch` не ловит исключение? → билдер возвращает управление немедленно; ошибка возникает позже, вне этого стека.
- `async` бросил исключение, `await()` не вызвали — что будет? → в обычном scope родитель всё равно отменится; «проглотит» только `supervisorScope`.
- Сработает ли handler, переданный в дочерний `launch`? → нет, у нерутовых корутин он игнорируется.
- Почему нельзя `catch (e: Exception)` без проброса? → перехватишь `CancellationException` и сломаешь отмену: корутина продолжит работу после `cancel()`.
- Спасает ли `SupervisorJob` саму упавшую корутину? → нет, она умирает; выживают только соседи и scope.

Связано: [[Coroutines. Cancellation]], [[2 Coroutines 2 CoroutineScope]], [[2 Coroutines 3 Context (dispatcher, job, exceptionHandler)]], [[2 Coroutines 0 Structured concurrency]], [[4 Flow 3 Exceptions (catch, retry)]]
