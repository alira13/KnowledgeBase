# Тестирование корутин и Flow

Асинхронный код ломает обычный подход к тестам: тест завершается раньше, чем корутина успевает отработать, а `delay(5000)` заставил бы ждать пять секунд по-настоящему. Библиотека `kotlinx-coroutines-test` решает обе проблемы.

```kotlin
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
testImplementation("app.cash.turbine:turbine:1.1.0")
```

## runTest — виртуальное время
```kotlin
@Test
fun `loads users`() = runTest {          // тело — suspend, тест ждёт завершения корутин
    val viewModel = UsersViewModel(FakeUserRepository())

    viewModel.load()

    assertEquals(2, viewModel.state.value.users.size)
}
```
`runTest` делает две вещи:
1. Запускает тело как корутину и **дожидается** всех дочерних корутин перед завершением теста.
2. Подставляет планировщик с **виртуальным временем**: `delay(10_000)` внутри выполняется мгновенно, время просто «проматывается». Тест с таймаутами идёт миллисекунды.

Управление временем вручную:
```kotlin
advanceTimeBy(5_000)     // промотать на 5 секунд
advanceUntilIdle()       // выполнить всё запланированное
runCurrent()             // выполнить задачи, готовые прямо сейчас
```

## Диспетчеры: главное правило
Код под тестом не должен создавать диспетчеры сам. `Dispatchers.Main` на JVM не существует (нет Looper'а) — тест упадёт с `Module with the Main dispatcher had failed to initialize`.

Два решения, и на собеседовании ждут второе:

**1. Правило подмены Main** — когда `Dispatchers.Main` зашит в код (например, `viewModelScope`):
```kotlin
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

class MyViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
}
```

**2. Внедрять диспетчеры** — правильнее архитектурно: класс принимает диспетчер параметром, в тесте передаётся тестовый.
```kotlin
class UserRepository(private val io: CoroutineDispatcher = Dispatchers.IO) {
    suspend fun load() = withContext(io) { ... }
}
```

### Standard vs Unconfined
| | `StandardTestDispatcher` | `UnconfinedTestDispatcher` |
| --- | --- | --- |
| Запуск корутины | откладывается до `advanceUntilIdle()`/`runCurrent()` | выполняется **сразу**, до первой приостановки |
| Когда брать | нужно контролировать порядок и проверять промежуточные состояния | простые тесты, где важен только итог |

По умолчанию в `runTest` — `StandardTestDispatcher`. Отсюда классическое «тест не видит результата»: корутина запланирована, но не запущена — нужен `advanceUntilIdle()`.

## Flow: Turbine
Собирать `Flow` вручную в тесте неудобно — нужен отдельный scope, отмена, накопление значений. Turbine делает это декларативно:
```kotlin
@Test
fun `emits loading then content`() = runTest {
    viewModel.state.test {                       // Turbine
        assertEquals(UiState.Loading, awaitItem())
        assertEquals(UiState.Content(users), awaitItem())
        cancelAndIgnoreRemainingEvents()
    }
}
```
- `awaitItem()` — ждёт следующее значение (с таймаутом, а не вечно);
- `awaitError()`, `awaitComplete()` — для ошибок и завершения;
- `expectNoEvents()` — проверить, что лишнего не пришло;
- незабранные значения приводят к падению теста — это защита от «проглоченных» эмиссий.

Без Turbine то же пишется руками:
```kotlin
val values = mutableListOf<UiState>()
val job = launch(UnconfinedTestDispatcher()) { viewModel.state.toList(values) }
// ...
job.cancel()
```

## Особенность StateFlow
`StateFlow` **конфлейтит** значения: если состояние изменилось дважды быстрее, чем подписчик успел прочитать, промежуточное значение он не увидит. Поэтому тест вида «должно прийти Loading, потом Content» может оказаться нестабильным — и это не баг Turbine, а поведение `StateFlow`. Проверяй конечное состояние либо используй `SharedFlow`/`Channel` для событий. См. [[StateFlow]], [[Flow]].

## Грабли
- **`runBlocking` вместо `runTest`** — реальные задержки, тест висит секунды.
- **`GlobalScope` в коде** — `runTest` не дождётся такой корутины, тест закончится раньше. См. [[Coroutines]].
- **`Thread.sleep()` в тесте** — верный признак, что проблема не решена, а замаскирована; ещё и флак.
- **Забыт `Dispatchers.resetMain()`** — утечка подмены на следующие тесты.
- **Проверка сразу после `launch`** со `StandardTestDispatcher` — корутина ещё не стартовала, нужен `advanceUntilIdle()`.
- **`delay` для синхронизации** вместо ожидания события — источник флака.

## Вопросы-ловушки
- Зачем `runTest`, если есть `runBlocking`? → виртуальное время и ожидание дочерних корутин: тест с таймаутами идёт мгновенно.
- Почему падает `Dispatchers.Main` в JVM-тесте? → на JVM нет Android Looper'а; нужен `Dispatchers.setMain` или внедрение диспетчера.
- Чем `UnconfinedTestDispatcher` отличается от `StandardTestDispatcher`? → первый выполняет корутину сразу, второй откладывает до явной промотки.
- Почему тест не видит промежуточного `Loading` у `StateFlow`? → конфлейт: быстрые последовательные значения схлопываются.
- Зачем Turbine, если можно `toList()`? → таймауты, проверка «лишних» эмиссий и читаемость.

Связано: [[Testing]], [[Test. Unit tests]], [[Test. Test doubles]], [[Coroutines]], [[StateFlow]], [[Flow]]
