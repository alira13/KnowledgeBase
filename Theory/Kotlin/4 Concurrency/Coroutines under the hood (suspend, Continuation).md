# Корутины под капотом (suspend, Continuation)

Senior-вопрос РФ-бигтеха: «как работает `suspend`, что такое Continuation, почему корутины дешевле потоков».

## suspend — это не «магия», а трансформация компилятора (CPS)
Компилятор превращает `suspend`-функцию в **state machine** и добавляет скрытый параметр `Continuation` — это **CPS (Continuation-Passing Style)**.

```kotlin
suspend fun load(): User { ... }
// компилятор превращает примерно в:
fun load(cont: Continuation<User>): Any?   // возвращает User или COROUTINE_SUSPENDED
```

**Continuation** — «что делать дальше»: колбэк с методом `resumeWith(Result)`, хранящий, на каком шаге остановились.

```kotlin
interface Continuation<in T> {
    val context: CoroutineContext
    fun resumeWith(result: Result<T>)
}
```

## Как работает приостановка
Функция с несколькими suspend-точками компилируется в машину состояний: каждая точка — это `label`. При достижении suspend-точки:
- если результат ещё не готов → функция возвращает **`COROUTINE_SUSPENDED`**, поток **освобождается** (не блокируется!);
- когда результат придёт → вызывается `continuation.resumeWith(...)`, и функция «продолжается» со следующего `label` (switch по состоянию).

Упрощённо, две suspend-точки → примерно:
```kotlin
when (label) {
    0 -> { label = 1; val r = fetchUser(cont); if (r == SUSPENDED) return SUSPENDED; user = r }
    1 -> { label = 2; val r = fetchPosts(user, cont); if (r == SUSPENDED) return SUSPENDED; posts = r }
    2 -> return Result(user, posts)
}
```

## Почему дешевле потоков
- Приостановка **не блокирует поток ОС** — тысячи корутин крутятся на небольшом пуле потоков (например, `Dispatchers.Default` = число ядер). Поток дорог (память под стек ~1 МБ, переключение контекста ядром), корутина — это объект-состояние.
- Переключение между корутинами — это вызовы функций/колбэков, а не системные переключения потоков.

## Связанные механизмы
- **CoroutineContext** — набор элементов (Job, Dispatcher, CoroutineName, ExceptionHandler). Dispatcher решает, **на каком потоке** возобновить корутину. См. [[2 Coroutines 3 Context (dispatcher, job, exceptionHandler)]].
- **Structured concurrency** — родительский Job ждёт детей, отмена распространяется. См. [[2 Coroutines 0 Structured concurrency]].
- **Отмена — кооперативная**: проверяется на suspend-точках (`ensureActive`/`isActive`), бросается `CancellationException`. См. [[Coroutines. Cancellation]].

## Вопрос-ловушка
«Блокирует ли `delay(1000)` поток?» → нет: планирует возобновление через 1с и освобождает поток (в отличие от `Thread.sleep`, который блокирует поток).

Связано: [[2 Coroutines 1 Suspend functions]], [[2 Coroutines 0 Structured concurrency]], [[Coroutines]], [[Java Memory Model (happens-before)]]
