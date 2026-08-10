---
tags: [reference, android, библиотеки]
---
# Библиотеки Android (шпаргалка-справочник)

Набор де-факто стандартных библиотек экосистемы. Полезно назвать на собесе «какой стек используете».

## Сеть
- **Retrofit** + **OkHttp** — REST-клиент (View-мир, Android).
- **Ktor Client** — мультиплатформенный HTTP-клиент (KMP). См. [[KMP. Kotlin Multiplatform]].
- **kotlinx.serialization** / **Moshi** / **Gson** — JSON (kotlinx — современный выбор).

## Хранение
- **Room** — ORM над SQLite. См. [[Databases]].
- **DataStore** — замена SharedPreferences (Preferences/Proto). См. [[DataStore]].
- **SQLDelight** — типобезопасный SQL, KMP.

## DI
- **Hilt** (кодоген, compile-time). См. [[Hilt]].
- **Koin** (Service Locator, DSL). См. [[Koin]].

## Асинхронность
- **kotlinx.coroutines** + **Flow**. См. [[Coroutines]].
- **RxJava/RxKotlin** — legacy-реактивщина. См. [[RxJava and RxKotlin]].

## UI
- **Jetpack Compose** + **Material 3**. См. [[Jetpack Compose]].
- **Coil** (Kotlin-first) / **Glide** / **Picasso** — загрузка изображений.
- **Accompanist** — доп. утилиты для Compose (permissions, pager и т.п.).
- **Lottie** — анимации JSON.

## Навигация / архитектура
- **Navigation Compose** — навигация. См. [[Navigation. Types]].
- **Paging 3** — пагинация списков.
- **WorkManager** — фоновые задачи. См. [[2 Services and WorkManager]].

## Тестирование
- **JUnit**, **MockK**, **Turbine** (Flow), **AssertK**, **Espresso**, **Compose UI Test**. См. [[Testing]].

## Диагностика
- **LeakCanary** — утечки. См. [[Memory leaks. Detection]].
- **Timber** — логирование.
- **Firebase Crashlytics** — краши в проде.

## Графики (нишевые)
- **YCharts**, **MPAndroidChart** — диаграммы/графики.

Связано: [[Jetpack library]], [[Dependencies via .toml]]
