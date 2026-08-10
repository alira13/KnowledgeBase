# KMP. Kotlin Multiplatform

Технология Kotlin для **шеринга кода между платформами** (Android, iOS, desktop, web, backend). Идея: общая **бизнес-логика** (domain, data, сеть, БД, валидация) на Kotlin, а **UI нативный** на каждой платформе (или общий через Compose Multiplatform).

## Что шарят и что нет
- **Шарят**: доменные модели, use cases, репозитории, сетевой слой (Ktor), БД (SQLDelight/Room KMP), валидацию, DI, ViewModel-логику.
- **Нативно (обычно)**: UI (Jetpack Compose на Android, SwiftUI на iOS), платформенные API. Либо **Compose Multiplatform** для общего UI.

## Структура исходников (source sets)
- **commonMain** — общий код, только мультиплатформенные API.
- **androidMain**, **iosMain**, ... — платформенные реализации.
- **expect / actual** — механизм платформенных зависимостей: в `commonMain` объявляют `expect` (контракт), в каждой платформе — `actual` (реализация).

```kotlin
// commonMain
expect fun platformName(): String
// androidMain
actual fun platformName() = "Android ${Build.VERSION.SDK_INT}"
// iosMain
actual fun platformName() = UIDevice.currentDevice.systemName()
```

## Ключевые библиотеки KMP
- **Ktor Client** — сеть.
- **kotlinx.serialization** — JSON.
- **SQLDelight** / Room KMP — БД.
- **kotlinx.coroutines**, **kotlinx.datetime** — общие.
- **Koin** — DI, кроссплатформенный.
- **Compose Multiplatform** — общий UI (опционально).

## Как работает на разных таргетах
- Android → компиляция в JVM-байткод (как обычно).
- iOS → **Kotlin/Native** компилирует в нативный бинарник, отдаётся как Objective-C/Swift-совместимый framework.

## Плюсы / минусы (на собесе)
- (+) Меньше дублирования логики, единый источник правды бизнес-логики, нативный UX.
- (+) Постепенное внедрение — можно шарить один модуль.
- (−) Порог входа, настройка тулчейна, iOS-интероп (memory model раньше был болью, сейчас лучше), меньше готовых библиотек, чем нативно.

## KMP vs Flutter/React Native
- KMP — **нативный UI и нативная производительность**, шарится логика, а не отрисовка.
- Flutter/RN — свой рендер-движок/мост, шарится и UI. KMP ближе к «нативу с общей логикой».

Связано: [[Multi-module architecture]], [[Domain layer and technologies]], [[Clean architecture]], [[Coroutines]]
