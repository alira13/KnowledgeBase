# Архитектура Android-приложения

> ⚠️ Ранее здесь был конспект старой статьи (Habr, ~2012), где Activity трактовалась как «ViewModel в MVVM», а ContentProvider — как «Model». Это **устаревшая и неверная** трактовка. Ниже — актуальное понимание.

## Из чего состоит приложение
- **Компоненты** (точки входа для системы): [[1 Activity|Activity]], [[2 Services and WorkManager|Service]], [[4 Broadcast Receiver|BroadcastReceiver]], [[3 Content Provider|ContentProvider]]. Каждый объявляется в **манифесте**.
- **AndroidManifest.xml** — декларирует компоненты, разрешения, min/target SDK, `application`, intent-фильтры.
- **Ресурсы** (`res/`) — layout, строки, изображения, темы; доступ через сгенерированный класс `R`. См. [[Resources]].
- **Собственные классы** — вся ваша логика (ViewModel, репозитории, use cases и т.д.).

## Компоненты — кратко
- **Activity** — экран/точка входа UI, имеет [[1 Activity|жизненный цикл]]. Современный тренд — **Single Activity** + composable/фрагменты как экраны.
- **Service** — фоновая работа без UI (foreground service для длительных видимых задач). Для отложенной/гарантированной работы — **WorkManager**.
- **BroadcastReceiver** — подписчик на системные/кастомные события (publish/subscribe).
- **ContentProvider** — предоставление данных **другим приложениям** (IPC), а не «модель».

## Рекомендованная архитектура (Google App Architecture Guide)
Не путать «компоненты Android» с «архитектурными слоями». Слои:

```
UI Layer          →  Composable/View + ViewModel (state holder)
   (State вниз, события вверх; UDF — однонаправленный поток данных)
Domain Layer      →  Use cases (опционально), доменные модели  [[Domain layer and technologies]]
Data Layer        →  Repository (SSOT) → data sources (remote/local)  [[Repository pattern]]
```

Принципы:
- **Separation of concerns** — UI тонкий, логика вне Activity/Fragment.
- **Drive UI from data models** — состояние в ViewModel, UI — функция состояния.
- **Single Source of Truth** — данные из одного авторитетного источника (обычно БД).
- **Unidirectional Data Flow (UDF)** — события идут вверх, состояние — вниз. См. [[Comparing MVC, MVP, MVVM, MVI]].
- **Dependency Inversion** — верхние слои зависят от абстракций нижних. См. [[Clean architecture]].

## Роль ViewModel (корректно)
`ViewModel` — держатель состояния UI, переживает изменение конфигурации, не знает про View/Context. Это и есть «VM» в MVVM/MVI — **не Activity**. См. [[1 ViewModel, ViewModelProvider]].

## Современный стек (senior-ориентир)
Kotlin • Coroutines/Flow • Jetpack Compose • ViewModel • Navigation • Room • Retrofit/Ktor • Hilt/Koin • многомодульность • Clean Architecture + MVI.

Связано: [[Clean architecture]], [[Repository pattern]], [[1 ViewModel, ViewModelProvider]], [[Mobile app system design]], [[0 App components. Intent]]
