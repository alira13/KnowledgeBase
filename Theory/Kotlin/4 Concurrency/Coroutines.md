# Корутины

Одна заметка на всю тему: от `suspend` до отмены, контекста и синхронизации.

---

## 1. Зачем они нужны

Обычная функция, которая долго работает, **блокирует поток**:

```kotlin
Thread.sleep(1000)      // поток стоит и ничего не делает
```

Поток — дорогой ресурс: около мегабайта под стек, переключение выполняет ядро ОС. Заблокировать главный поток на секунду в Android — это застывший интерфейс.

Корутина вместо блокировки **приостанавливается**:

```kotlin
delay(1000)             // поток свободен и работает над другими задачами
```

Ментальная модель:

> Поток — это работник. Блокировка — работник стоит и ждёт. Приостановка — работник уходит делать другое дело и вернётся, когда всё будет готово.

Поэтому корутины называют «лёгкими потоками»: их можно запустить тысячи на пуле из нескольких потоков.

---

## 2. suspend

`suspend` — модификатор функции, которая умеет приостанавливаться.

```kotlin
suspend fun loadUser(): User {
    delay(1000)
    return api.getUser()
}
```

Правило вызова:

```text
suspend-функцию можно вызвать
    только из корутины
    или из другой suspend-функции
```

Причина этого правила — в разделе 3.

---

## 3. Как это работает под капотом

Компилятор убирает слово `suspend` и добавляет скрытый параметр `Continuation`:

```kotlin
suspend fun load(): User
        ↓ компилятор
fun load(cont: Continuation<User>): Any?
```

`Continuation` — это «что делать дальше»:

```kotlin
interface Continuation<in T> {
    val context: CoroutineContext
    fun resumeWith(result: Result<T>)
}
```

Тело функции превращается в **машину состояний**: каждая точка приостановки становится отдельным `label`.

```kotlin
when (label) {
    0 -> { label = 1; val r = fetchUser(cont); if (r == SUSPENDED) return SUSPENDED; user = r }
    1 -> { label = 2; val r = fetchPosts(user, cont); if (r == SUSPENDED) return SUSPENDED; posts = r }
    2 -> return Result(user, posts)
}
```

Что происходит в точке приостановки:

```text
результат не готов → возвращаем COROUTINE_SUSPENDED → поток освобождается
результат пришёл  → continuation.resumeWith(...) → продолжаем со следующего label
```

Это называется **CPS — Continuation-Passing Style**.

Теперь понятно правило из раздела 2: обычная функция не знает, откуда взять `Continuation`, поэтому вызвать `suspend` из неё нельзя.

---

## 4. Что нужно, чтобы запустить корутину

```text
1. CoroutineScope       — где живёт (кто отменит)
2. CoroutineContext     — в каких условиях (поток, имя, обработчик)
3. suspend-код          — что выполнять
4. Coroutine builder    — чем запустить
```

---

## 5. Билдеры

| Билдер | Возвращает | Создаёт корутину | Когда |
| --- | --- | --- | --- |
| `launch` | `Job` | да | запустить работу, результат не нужен |
| `async` | `Deferred<T>` | да | нужен результат, `await()` его отдаст |
| `runBlocking` | `T` | да | мост из обычного кода: `main()` и тесты |
| `withContext` | `T` | **нет** | сменить контекст без новой корутины |

```kotlin
val job = scope.launch { doWork() }              // Job: можно отменить, дождаться join()

val deferred = scope.async { computeValue() }    // Deferred: await() вернёт значение
val value = deferred.await()

val data = withContext(Dispatchers.IO) { api.load() }   // просто переключили поток
```

Полезное правило:

```text
число корутин = число билдеров в коде
(withContext не считается — он не создаёт корутину)
```

`runBlocking` **блокирует** вызывающий поток, пока не завершится всё внутри. В Android-коде это почти всегда ошибка.

Параллельная загрузка — это `async` + `await`:

```kotlin
suspend fun loadAll() = coroutineScope {
    val user = async { loadUser() }
    val feed = async { loadFeed() }
    user.await() to feed.await()      // обе загрузки идут одновременно
}
```

---

## 6. CoroutineScope

Scope отвечает на один вопрос:

> Когда эти корутины надо отменить?

Технически это просто держатель контекста:

```kotlin
interface CoroutineScope {
    val coroutineContext: CoroutineContext
}
```

`launch` и `async` объявлены как его расширения — поэтому запустить корутину «нигде» нельзя.

Свой scope:

```kotlin
class MyRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun load() = scope.launch { ... }

    fun clear() = scope.cancel()      // обязательно!
}
```

Готовые в Android:

```text
viewModelScope   → до onCleared(), Dispatchers.Main.immediate
lifecycleScope   → до onDestroy()
rememberCoroutineScope() → пока композабл в композиции
```

**GlobalScope использовать нельзя**: у него нет владельца и отмены, корутина переживёт экран и дотянется до уничтоженной Activity.

---

## 7. Scope-объект и scope-функции — не путать

| | `CoroutineScope(...)` | `coroutineScope { }` |
| --- | --- | --- |
| Что это | фабрика объекта-владельца | **suspend-функция** |
| Возвращает управление | сразу | когда завершатся все дети |
| Кто отменяет | вы, вручную | отменяется вместе с вызывающей корутиной |
| Зачем | привязать к жизненному циклу объекта | сгруппировать параллельные задачи |

Важная тонкость про вложенность:

```kotlin
scope.launch {
    launch { }        // ① дочерняя корутина
}

scope.launch {
    scope.launch { }  // ② НЕ дочерняя — родитель тот же scope
}
```

Поэтому внутри suspend-функции для запуска дочерних корутин оборачивают код в `coroutineScope { }` или `supervisorScope { }`.

---

## 8. CoroutineContext

Набор элементов, складывается оператором `+`:

```kotlin
CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineName("sync") + handler)
```

| Элемент | За что отвечает |
| --- | --- |
| `Job` | жизненный цикл и иерархия |
| `CoroutineDispatcher` | на каком потоке выполняться |
| `CoroutineName` | имя для отладки |
| `CoroutineExceptionHandler` | последний рубеж обработки ошибок |

Этот контекст **никак не связан** с `Context` из Android SDK.

### Диспетчеры

```text
Dispatchers.Main        → главный поток, только UI
Dispatchers.Default     → вычисления; потоков = числу ядер (минимум 2)
Dispatchers.IO          → сеть и диск; потоков много, они в основном ждут
Dispatchers.Unconfined  → без привязки к потоку, в обычном коде не нужен
```

Если диспетчер не указан, берётся тот, что в контексте scope. У `viewModelScope` это `Main.immediate`, поэтому корутины по умолчанию идут на главном потоке.

---

## 9. Job и жизненный цикл

`Job` управляет корутиной и связывает её с родителем.

```text
isActive     → работает
isCancelled  → отменена
isCompleted  → завершилась
```

Промежуточные состояния `Completing` и `Cancelling` означают «ждём детей».

---

## 10. Structured concurrency

Главный принцип библиотеки: у каждой корутины есть родитель, и никто не теряется.

**Правило 1.** Корутина запускается только внутри scope.

**Правило 2.** Job'ы образуют иерархию: родитель — дети.

**Правило 3.** Родитель не завершится, пока живы дети.

```kotlin
suspend fun fetchData() {
    coroutineScope {
        launch { loadUser() }
        launch { loadPosts() }
    }
    println("всё загружено")     // выполнится после обеих корутин
}
```

**Правило 4.** Отмена родителя отменяет всех детей. Отмена ребёнка родителя не трогает.

**Правило 5.** Необработанное исключение в ребёнке уходит наверх и отменяет остальных детей — если только это не supervisor.

Что это даёт: нет потерянных корутин, нет утечек, отмена экрана гарантированно останавливает всю его работу.

---

## 11. Отмена — кооперативная

`cancel()` **не останавливает** корутину. Он лишь выставляет флаг:

```text
cancel() → isActive = false → на ближайшей точке приостановки
           бросается CancellationException
```

Отсюда главное следствие: цикл без suspend-точек **не остановится**.

```kotlin
// не отменится
while (true) { heavyComputation() }

// отменится
while (isActive) { heavyComputation() }

// или так
ensureActive()      // бросит CancellationException, если корутина отменена
yield()             // отдаёт управление и проверяет отмену
```

Отменяемые из коробки: `delay()`, `yield()`, `withTimeout()`, `await()`.

Виды отмены:

| Вид | Чем вызвана |
| --- | --- |
| Штатная | `cancel()` / `cancelAndJoin()` |
| Внештатная | исключение в корутине |
| Кооперативная | сама проверяет `isActive` |
| По таймауту | `withTimeout()` |

### Освобождение ресурсов

```kotlin
try {
    downloadFiles()
} finally {
    withContext(NonCancellable) {     // иначе suspend-вызов в finally сразу упадёт
        closeConnection()
    }
}
```

`NonCancellable` нужен именно для `finally`: отменённая корутина не может выполнять обычные suspend-вызовы.

---

## 12. Исключения

**`try/catch` вокруг билдера не работает:**

```kotlin
try {
    scope.launch { throw RuntimeException() }   // НЕ поймается
} catch (e: Exception) { }
```

Билдер возвращает управление сразу, а ошибка случится позже. Оборачивать нужно код **внутри**.

**launch и async ведут себя по-разному:**

```text
launch → исключение улетает вверх сразу
async  → исключение хранится в Deferred и всплывёт на await()
```

Ловушка: `async` в обычном scope всё равно отменит родителя, даже если `await()` никто не вызовет. Изолирует только `supervisorScope`.

**CancellationException — особое.** Механизм отмены построен на нём, и корутины его игнорируют. Поэтому нельзя глотать его в общем `catch`:

```kotlin
try {
    doWork()
} catch (e: CancellationException) {
    throw e                      // обязательно пробросить!
} catch (e: Exception) {
    log(e)
}
```

По той же причине `runCatching` в корутинах опасен — он ловит всё подряд.

**CoroutineExceptionHandler** — последний рубеж:

```kotlin
val handler = CoroutineExceptionHandler { _, e -> log(e) }
val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + handler)
```

Три ограничения:

```text
1. работает только у корневой корутины scope
2. не работает с async
3. не предотвращает отмену — корутина уже мертва
```

**SupervisorJob и supervisorScope** меняют направление: ошибка ребёнка не отменяет родителя и братьев.

---

## 13. Синхронизация

Даже с корутинами общее изменяемое состояние остаётся общим.

**Mutex** — приостанавливает корутину, а не блокирует поток:

```kotlin
val mutex = Mutex()
mutex.withLock { counter++ }
```

**Атомарные типы** — без блокировок:

```kotlin
val counter = AtomicInteger(0)
counter.incrementAndGet()
```

**`synchronized` в корутинах использовать нельзя**: он блокирует поток диспетчера, а если внутри окажется suspend-вызов — корутина уснёт, удерживая монитор.

| Средство | Когда |
| --- | --- |
| `Mutex` | нужен явный эксклюзивный доступ |
| `Atomic*` | счётчики и флаги, быстрее мьютекса |
| `withContext(Dispatchers.Default)` | вынести вычисления, состояние не делится |
| `Channel` / `actor` | состоянием владеет одна корутина |

Лучший вариант — вообще не делить изменяемое состояние. См. [[Multithreading]].

---

## 14. Корутины против потоков и ExecutorService

| | Потоки / ExecutorService | Корутины |
| --- | --- | --- |
| Подход | ориентация на потоки | ориентация на задачи |
| Задержка | `Thread.sleep` блокирует | `delay` приостанавливает |
| Управление жизнью | `shutdown()` вручную | scope и structured concurrency |
| Результат | `Future` | `Job`, `Deferred` |
| Отмена | `future.cancel(true)`, может не сработать | `job.cancel()`, кооперативная |
| Ошибки | `try/catch` вокруг `Future.get()` | handler, supervisor |
| Читаемость | вложенные колбэки | последовательный код |

---

## 15. Частые грабли

- **`GlobalScope`** — корутина переживает экран, утечка.
- **Scope без `SupervisorJob`** — первое исключение убивает scope навсегда, новые `launch` молча не запускаются.
- **Цикл без suspend-точек** не отменяется.
- **Проглоченный `CancellationException`** ломает отмену.
- **`runBlocking` на главном потоке** — ANR.
- **`withContext(Dispatchers.IO)` вокруг всего подряд** — переключение контекста не бесплатно; оборачивать нужно конкретную операцию.
- **`Dispatchers.IO` для вычислений** — он рассчитан на ожидание, для CPU нужен `Default`.
- **Ручной `scope.cancel()` во ViewModel** — `viewModelScope` делает это сам.

---

## 16. Что знать на каком уровне

**Middle**

```text
suspend, launch/async/runBlocking/withContext
Job и Deferred, await
viewModelScope, lifecycleScope
Dispatchers: Main / IO / Default
```

**Middle+**

```text
structured concurrency и иерархия Job
coroutineScope vs CoroutineScope
кооперативная отмена, ensureActive
try/catch вокруг билдера не работает
SupervisorJob и supervisorScope
```

**Senior**

```text
Continuation, CPS, машина состояний
почему корутины дешевле потоков
CancellationException и NonCancellable
ограничения CoroutineExceptionHandler
Mutex вместо synchronized
тестирование: runTest, TestDispatcher
```

---

## 17. Вопросы-ловушки

- **Блокирует ли `delay` поток?** Нет: планирует возобновление и освобождает поток. `Thread.sleep` — блокирует.
- **Почему `suspend` нельзя вызвать из обычной функции?** Ей неоткуда взять `Continuation`.
- **Сколько корутин создаёт `withContext`?** Ни одной, он только переключает контекст.
- **Почему `try/catch` вокруг `launch` не ловит исключение?** Билдер возвращает управление сразу, ошибка возникает позже.
- **Остановит ли `cancel()` бесконечный цикл?** Нет, отмена кооперативная — нужна проверка `isActive` или suspend-точка.
- **Что будет, если поймать `CancellationException` и не пробросить?** Корутина продолжит работу после `cancel()`.
- **Чем `coroutineScope` отличается от `CoroutineScope`?** Первый — suspend-функция, ждущая детей; второй — объект-владелец.
- **Переживёт ли корутина в `viewModelScope` поворот экрана?** Да, ViewModel не пересоздаётся. В `lifecycleScope` активити — нет.
- **Зачем `SupervisorJob` в своём scope?** Чтобы одно исключение не переводило scope в отменённое состояние навсегда.
- **Почему `synchronized` не годится для корутин?** Блокирует поток диспетчера и не умеет работать с приостановкой.

---

## Коротко

```text
1. Корутина не блокирует поток, а приостанавливается (Continuation + машина состояний).

2. Всё живёт в scope: нет scope — нет корутины. GlobalScope не использовать.

3. launch → Job, async → Deferred, withContext → просто смена контекста.

4. Отмена кооперативная: cancel() ставит флаг, проверка на suspend-точках.

5. CancellationException всегда пробрасывать, иначе отмена не работает.
```

Связано: [[Flow]], [[5 Channels]], [[Multithreading]], [[Test. Coroutines and Flow]], [[3 Sequence]]
