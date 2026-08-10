# CoroutineScope

**CoroutineScope** — область, к которой привязан жизненный цикл корутин. Он отвечает на вопрос «когда эти корутины надо отменить», и в этом весь его смысл: корутина не должна пережить того, кто её запустил.

Технически это интерфейс с **единственным полем**:
```kotlin
interface CoroutineScope {
    val coroutineContext: CoroutineContext
}
```
То есть scope — просто «держатель контекста». Билдеры `launch`/`async` объявлены как его расширения, поэтому запустить корутину без scope нельзя — это и есть механизм **structured concurrency**. См. [[2 Coroutines 0 Structured concurrency]].

## Scope, Context, Job — кто есть кто
- **Context** — набор элементов (`Job` + `Dispatcher` + `CoroutineName` + `CoroutineExceptionHandler`), складывается оператором `+`. См. [[2 Coroutines 3 Context (dispatcher, job, exceptionHandler)]].
- **Job** — элемент контекста, отвечающий за жизненный цикл и иерархию: у каждой корутины есть свой `Job`, дочерний по отношению к `Job` scope.
- **Scope** — обёртка над контекстом, из которой запускают корутины.

Отсюда работает `scope.cancel()`: он отменяет `Job` из контекста, а тот каскадом отменяет всех детей.

## Как создать свой scope
```kotlin
class MyRepository {
    // SupervisorJob: падение одной корутины не убьёт scope и остальные
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun fetchData() {
        scope.launch { println(loadData()) }
    }

    fun clear() {
        scope.cancel()   // обязательно! иначе корутины переживут объект
    }
}
```
Два правила: **`SupervisorJob()`** (иначе первое же исключение переведёт scope в состояние Cancelled навсегда — новые `launch` просто не запустятся) и **обязательный `cancel()`** в точке смерти владельца.

Важная деталь: `CoroutineScope(context)` — это **функция**-фабрика, и если в контексте нет `Job`, она добавит его сама. А `MainScope()` — готовый вариант `SupervisorJob() + Dispatchers.Main`.

## Готовые scope в Android
| Scope | Живёт до | Диспетчер по умолчанию |
| --- | --- | --- |
| `viewModelScope` | `ViewModel.onCleared()` | `Dispatchers.Main.immediate` |
| `lifecycleScope` | `onDestroy()` владельца | `Dispatchers.Main.immediate` |
| `rememberCoroutineScope()` | пока композабл в композиции | контекст композиции |

```kotlin
class MyViewModel : ViewModel() {
    fun fetchData() = viewModelScope.launch { /* отменится в onCleared */ }
}
```
Внутри они устроены ровно как самописный scope выше: `SupervisorJob() + Dispatchers.Main.immediate` плюс `cancel()` в нужном колбэке.

Для UI-работы, зависящей от состояния экрана, обычно нужен не просто `lifecycleScope`, а `repeatOnLifecycle(Lifecycle.State.STARTED)` — иначе сбор Flow продолжится, когда экран не виден.

## GlobalScope — почему нельзя
```kotlin
GlobalScope.launch { /* живёт до конца процесса */ }
```
У `GlobalScope` нет владельца и нет отмены: корутина переживёт экран, дотянется до уничтоженной `Activity` (утечка) и попытается обновить мёртвый UI. Он помечен `@DelicateCoroutinesApi`. Легальные случаи наперечёт — фоновая работа уровня всего приложения, которая обязана дожить до конца процесса.

Замена: свой scope с явным `cancel()` или `viewModelScope`. Для работы, которая должна пережить экран, — `WorkManager`, а не корутина.

## runBlocking
**Блокирует** текущий поток, пока не завершатся все вложенные корутины. Это мост из обычного кода в корутинный: тесты и `main()`. В Android-коде — почти всегда ошибка: на main-потоке это ANR.

## Scope-объект vs scope-функции
Частая путаница. `coroutineScope { }` и `supervisorScope { }` — **suspend-функции**, а не объекты:

| | `CoroutineScope(...)` | `coroutineScope { }` |
| --- | --- | --- |
| Что это | фабрика объекта-владельца | suspend-функция |
| Когда возвращает управление | сразу | когда завершатся **все** дочерние корутины |
| Кто отменяет | ты, вручную `cancel()` | отменяется вместе с вызывающей корутиной |
| Зачем | привязать корутины к времени жизни объекта | сгруппировать параллельные задачи внутри одной suspend-функции |

```kotlin
suspend fun loadAll() = coroutineScope {          // ждёт обе, отменит вторую при падении первой
    val user = async { loadUser() }
    val feed = async { loadFeed() }
    user.await() to feed.await()
}
```
Разница `coroutineScope` и `supervisorScope` — в реакции на исключение ребёнка: первый отменяет всех, второй изолирует. См. [[2 Coroutines 5 Exception handling]].

## Грабли
- **Отменённый scope больше не работает**: после `cancel()` объект нельзя переиспользовать, `launch` в нём завершится мгновенно. Для «отменить текущее, запустить новое» храни `Job` конкретной корутины и отменяй его (`job?.cancel()`), а не scope.
- **`scope.cancel()` в `ViewModel` вручную** не нужен — `viewModelScope` делает это сам; лишний вызов сломает ViewModel.
- **Scope без `SupervisorJob`** умирает от первого же исключения — типичная причина «корутины молча перестали запускаться».
- **Наследование `CoroutineScope` классом** (`class Foo : CoroutineScope`) — устаревший приём: публикует `launch` наружу. Лучше приватное поле. См. [[2 Coroutines. Inheriting CoroutineScope]].
- Отмена **кооперативна**: цикл без suspend-точек не остановится. Нужен `ensureActive()`/`yield()`/`isActive`. См. [[Coroutines. Cancellation]].

## Вопросы-ловушки
- Чем `CoroutineScope(ctx)` отличается от `coroutineScope { }`? → первый создаёт объект-владелец и возвращает управление сразу, второй приостанавливает вызывающую корутину до завершения детей.
- Что произойдёт с корутинами после поворота экрана? → `viewModelScope` переживёт (ViewModel не пересоздаётся), `lifecycleScope` активити — отменится.
- Почему `viewModelScope` использует `Main.immediate`, а не `Main`? → чтобы не откладывать выполнение через `Handler`, если мы уже на main-потоке; меньше лишних кадров задержки.
- Можно ли запустить корутину без scope? → нет: `launch`/`async` — расширения `CoroutineScope`; исключение — `GlobalScope`/`runBlocking`, которые сами являются scope.

Связано: [[2 Coroutines 0 Structured concurrency]], [[2 Coroutines 3 Context (dispatcher, job, exceptionHandler)]], [[2 Coroutines 5 Exception handling]], [[Coroutines. Cancellation]], [[2 Coroutines 4 Coroutine builders]]
