# System Design мобильного приложения

На senior-собесе дают задачу «спроектируйте приложение X» (лента, чат, фото-фид, приложение рецептов с оффлайном). Оценивают структуру мышления, а не единственно верный ответ.

## Каркас ответа (по шагам)
1. **Уточнить требования** — функциональные (что делает) и нефункциональные (оффлайн? real-time? объём данных? целевые API-уровни? push?).
2. **Слои архитектуры** — presentation / domain / data. См. [[Clean architecture]].
3. **Модель данных** и источники (API, БД, кэш).
4. **Потоки данных** (загрузка, кэширование, синхронизация, оффлайн).
5. **Кросс-функциональное**: ошибки, пагинация, конфликты, безопасность, производительность, тестирование.
6. **Trade-offs** — озвучить альтернативы и почему выбор такой.

## Слои (эталон)
- **Presentation** — UI (Compose) + ViewModel/MVI. State наверх, события вниз. См. [[Comparing MVC, MVP, MVVM, MVI]].
- **Domain** — use cases + доменные модели, чистый Kotlin (можно KMP). Независим от Android. См. [[Domain layer and technologies]].
- **Data** — репозитории (интерфейс в domain, реализация в data), data sources (remote/local), мапперы DTO↔domain. См. [[Repository pattern]].

## Ключевые решения и паттерны
- **Single Source of Truth** — UI читает из БД (`Flow`), сеть пишет в БД. Оффлайн бесплатно.
- **Offline-first**: локальный кэш (Room) + фоновая синхронизация (WorkManager). См. [[2 Services and WorkManager]].
- **Пагинация** — Paging 3 (RemoteMediator для сеть+БД).
- **Обработка ошибок** — типизированный `Result<Data, Error>`, маппинг в UI-текст. См. skill android-error-handling.
- **DI** — Hilt/Koin, модульность по фичам. См. [[Dependency injection]], [[Multi-module architecture]].
- **Навигация** — type-safe Navigation Compose, кросс-фичевые callbacks. См. [[Navigation. Types]].
- **Многопоточность** — корутины + Flow, `viewModelScope`. См. [[Coroutines]].

## Пример: приложение рецептов (частая задача)
- Экраны: список, поиск, детали, редактирование фото.
- `:feature:recipes` (ui+vm) → `:domain` (GetRecipes, RefreshRecipes) → `:data` (RecipeRepository: RecipeApi + RecipeDao).
- SSOT: `dao.observeRecipes(): Flow` — UI подписан; `refresh()` тянет сеть → пишет в БД.
- Оффлайн: показываем из БД, если сети нет; синк через WorkManager.
- Загрузка фото: домен `UploadRecipeImage` (получить из галереи/камеры → загрузить через API). Чтобы переиспользовать на двух экранах (детали и список) — вынести в **use case**, а не дублировать в двух ViewModel. (Это ровно senior-вопрос из списка собеседований.)

## Нефункциональные аспекты (не забыть проговорить)
- Производительность и старт. См. [[Performance. Profiling and UI optimization]], [[Baseline Profiles and App Startup]].
- Безопасность (токены, pinning). См. [[Security. SSL Pinning, KeyStore, secrets]].
- Тестируемость (слои за интерфейсами). См. [[Testing]].
- Масштабирование команды — многомодульность.
- Аналитика, крэш-репортинг, feature flags, A/B.

Связано: [[Clean architecture]], [[Repository pattern]], [[Multi-module architecture]], [[Comparing MVC, MVP, MVVM, MVI]]
