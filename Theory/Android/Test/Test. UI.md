# UI-тесты (Espresso и Compose)

UI-тесты проверяют приложение глазами пользователя: нажал — увидел. Живут в `src/androidTest/`, требуют эмулятора или устройства, поэтому идут секунды и десятки секунд. Их держат мало и только на критичных сценариях — почему именно так, см. [[Testing]].

## Espresso (View-система)
Три составляющие любой проверки: **найти** view, **сделать** действие, **проверить** результат.
```kotlin
@RunWith(AndroidJUnit4::class)
class LoginTest {
    @get:Rule val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun showsErrorOnEmptyPassword() {
        onView(withId(R.id.email)).perform(typeText("a@b.c"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        onView(withId(R.id.error)).check(matches(isDisplayed()))
    }
}
```
- `onView(matcher)` — поиск: `withId`, `withText`, `withContentDescription`, комбинации через `allOf`.
- `perform(action)` — `click()`, `typeText()`, `swipeUp()`, `scrollTo()`.
- `check(assertion)` — `matches(isDisplayed())`, `matches(withText("..."))`, `doesNotExist()`.

Для списков — отдельный API, потому что элементы переиспользуются и могут быть не созданы:
```kotlin
onView(withId(R.id.recycler))
    .perform(RecyclerViewActions.actionOnItemAtPosition<Holder>(2, click()))
```

**Espresso синхронизируется автоматически**: перед каждым действием он ждёт, пока главный поток освободится и очередь сообщений опустеет. Именно поэтому `Thread.sleep` обычно не нужен.

## Compose
Другой принцип: не иерархия View, а **дерево семантики (semantics)** — то же, что читает TalkBack. Отсюда бонус: тестируемость и доступность улучшаются вместе.
```kotlin
class LoginScreenTest {
    @get:Rule val composeRule = createComposeRule()      // без Activity

    @Test
    fun showsErrorOnEmptyPassword() {
        composeRule.setContent { LoginScreen(state = LoginState(), onAction = {}) }

        composeRule.onNodeWithText("Войти").performClick()

        composeRule.onNodeWithText("Введите пароль").assertIsDisplayed()
    }
}
```
- `createComposeRule()` — тестирует композабл изолированно, без запуска экрана целиком.
- `createAndroidComposeRule<MainActivity>()` — когда нужна настоящая Activity.
- Поиск: `onNodeWithText`, `onNodeWithTag("...")` (для `Modifier.testTag`), `onNodeWithContentDescription`.
- Действия: `performClick()`, `performTextInput()`, `performScrollTo()`.
- Проверки: `assertIsDisplayed()`, `assertTextEquals()`, `assertIsEnabled()`, `assertDoesNotExist()`.

Отладка дерева: `composeRule.onRoot().printToLog("TAG")` — печатает семантику целиком, быстрее, чем гадать, почему узел не находится.

### Синхронизация в Compose
Правило само ждёт, пока композиция «успокоится» (idle). Если экран зависит от анимации или внешнего источника:
```kotlin
composeRule.mainClock.autoAdvance = false     // ручной контроль времени анимации
composeRule.mainClock.advanceTimeBy(500)
composeRule.waitUntil { composeRule.onAllNodesWithTag("item").fetchSemanticsNodes().isNotEmpty() }
```

## Почему UI-тесты флакают и что делать
| Причина | Лечение |
| --- | --- |
| Бесконечная анимация (прогресс-бар) не даёт достичь idle | отключить анимации в тестовой сборке, `mainClock` вручную |
| Реальная сеть — разное время и ответы | MockWebServer или фейковый репозиторий через DI |
| Ожидание через `Thread.sleep` | `waitUntil { }` с условием |
| Общее состояние между тестами (БД, prefs) | чистить в `@Before`, in-memory база |
| Системные диалоги, разрешения | `GrantPermissionRule`, заранее выданные разрешения |
| Разные локали/размеры экрана | фиксировать в конфигурации теста |

Ключевой приём — **детерминированное окружение**: UI-тест должен проверять UI, а не сеть и не время. Зависимости подменяются через DI (`HiltAndroidRule`, тестовые модули Koin).

## Robolectric — середина между уровнями
Позволяет запускать Android-код (в том числе Espresso и Compose-тесты) **на JVM**, подменяя фреймворк. Быстрее эмулятора и работает в CI без устройства, но это симуляция: часть поведения отличается от настоящего Android. Разумный компромисс — Robolectric для большинства UI-проверок, реальный эмулятор для нескольких сквозных сценариев.

## Что стоит покрывать
- Критичные пути: авторизация, оплата, оформление заказа.
- Сценарии, где ломается интеграция слоёв: экран → ViewModel → репозиторий.
- Регрессии по найденным багам.

Не стоит: проверять каждый экран и каждый отступ — это дорого и хрупко; вёрстку надёжнее ловить скриншот-тестами (Paparazzi, Roborazzi).

## Вопросы-ловушки
- Чем поиск в Compose отличается от Espresso? → Compose ищет по дереву семантики, Espresso — по иерархии View.
- Почему UI-тест зависает на экране с прогресс-баром? → бесконечная анимация не даёт достичь состояния idle.
- Нужен ли `Thread.sleep` в Espresso? → нет, он синхронизируется сам; sleep — источник флака.
- Зачем `testTag`, если можно искать по тексту? → текст меняется и зависит от локали; тег стабилен.
- Что даёт Robolectric? → запуск Android-тестов на JVM: быстро и без устройства, ценой симуляции фреймворка.
- Где брать данные для UI-теста? → из подменённых через DI зависимостей, а не из реальной сети.

Связано: [[Testing]], [[Test. Unit tests]], [[Test. Test doubles]], [[View. Lists. RecyclerView]], [[Recomposition and stability]], [[CI-CD for Android]]
