# PLAN — Аудит полноты конспектов

Живой план: довести конспекты Theory до уровня «единственной книги для изучения», а не только
для напоминания. Продолжать можно с любого компьютера — файл в git. Отмечай `[x]` по мере готовности.

## Критерий «learning-grade» (для каждой заметки)
1. **Определение** термина простыми словами (не предполагать, что уже знаешь).
2. **Зачем / как** — смысл и механизм, а не только факт.
3. **Пример** (код/таблица), если уместно.
4. **Грабли / нюансы** и **вопрос-ловушка** с собеседования.
5. Никаких «объяснений только в картинке» — суть должна быть в тексте.

---
## Этап 0 — Миграция (СДЕЛАНО)
- [x] Перенос в `Theory/`, английские имена папок и файлов, содержимое на русском.
- [x] Картинки собраны в `images/`, ссылки переведены в `![](<images/...>)`.
- [x] Внутренние `[[ссылки]]` перепривязаны на новые английские имена.

## Этап 1 — P1: ядро (спрашивают почти всегда) — СДЕЛАНО
Все заметки доведены до learning-grade. Следующий незакрытый пункт — в Этапе 2.
- [x] `Android/Garbage collector` — GC + **daemon-потоки** (была опечатка «Dymon», исправлено в `Kotlin/4 Concurrency/0 Threads 1 Basics (thread)`).
- [x] `Patterns/Clean architecture` — слои + правило зависимостей + Dependency Inversion.
- [x] `Algorithms/Algorithm complexity` — классы O(1..n!), как считать, best/avg/worst, память.
- [x] `Kotlin/3 Classes and interfaces/Generics. Basics` — типобезопасность, bounds, вариантность, стирание.
- [x] `Kotlin/3.../Classes. Inner and Nested` — nested vs inner, ссылка на Outer, утечки, код.
- [x] `Kotlin/3.../Generics. Variance (in, out)` — PECS, use-site vs declaration-site, `@UnsafeVariance`, примеры.
- [x] `Kotlin/3.../Generics. Type erasure` — почему нельзя `is List<String>`, reified, обходы, метаданные.
- [x] `Kotlin/3.../Classes. toString, equals, hashCode, copy` — контракты equals/hashCode, «почему 31», copy (shallow, только первичный конструктор).
- [x] `Kotlin/4 Concurrency/0 Threads 3 Synchronized`, `Thread safety` — монитор, гонки, выбор объекта-замка, DCL, стратегии потокобезопасности.
- [x] `Kotlin/4 Concurrency/2 Coroutines 5 Exception handling`, `2 CoroutineScope` — scope vs coroutineScope, SupervisorJob, CancellationException, ограничения handler.
- [x] `Kotlin/Collections/Collections. Overview` — List/Set/Map, read-only vs immutable, HashMap изнутри (бакеты, load factor, treeify), ArrayList vs LinkedList.
- [x] `Android/App components/1 Activity` — ЖЦ подробно, последовательности колбэков, config change, process death vs поворот.
- [x] `Android/App/Context` — иерархия (ContextWrapper/ContextThemeWrapper), типы и время жизни, обе стороны ошибки (утечка / нет темы).
- [x] `Android/UI/Bundle`, `Android/Data/0 Serialization...` — Parcelable vs Serializable, почему Parcel нельзя хранить, лимит Binder (+ почищена `Intent and Bundle data size limits`).
- [x] `Kotlin/2 Functions/Functions. Operator overloading`, `Functions. Generic (parameterized)` — переписаны с кодом.
- [x] `Kotlin/3.../IPC. How two apps communicate` — Binder, AIDL, ContentProvider, Messenger, Intent.

## Добавленные темы (частые в РФ-бигтехе) — с примерами кода
- [x] `Android/App components/Handler, Looper, MessageQueue` — устройство UI-потока, HandlerThread, утечки.
- [x] `Kotlin/4 Concurrency/Java Memory Model (happens-before)` — видимость, volatile, атомарность.
- [x] `Kotlin/4 Concurrency/Coroutines under the hood (suspend, Continuation)` — CPS, state machine, почему дешевле потоков.
- [x] `Android/Networking. Retrofit and OkHttp` — интерцепторы, кэш, ошибки, Authenticator.
- [x] `Paging 3` — PagingSource/Pager/адаптер, cachedIn, loadState, RemoteMediator, грабли.
- [x] `Compose phases` — три фазы, один проход измерения, чтение state в поздней фазе.
- [x] `KAPT vs KSP` — почему KAPT медленный (стабы + javac), что даёт KSP, миграция.
- [x] Dagger component graph — раскрыт внутри [[Dagger2]] (Component, скоупы, subcomponent vs dependencies).
- [x] `Binder and AIDL` — зачем свой IPC, одно копирование через mmap, идентификация вызывающего, binder thread pool, Stub/Proxy, oneway, направления параметров, грабли.

## Этап 2 — P2 — СДЕЛАНО
- [x] Kotlin: `Functions. Scope functions` (два признака вместо зубрёжки, takeIf, грабли), `Functions. Delegates` (механика getValue/setValue, встроенные, Android, делегирование интерфейса), `Types. Unit, Nothing, Any, null` (свёрнуты два черновика, добавлено `Nothing?` и роль в выводе типов).
- [x] Android: `App components/2 Services and WorkManager` (ограничения по версиям, WorkManager раскрыт), `App components/4 Broadcast Receiver` (API 26/33, замена LocalBroadcastManager), DI: `Dagger2` (граф, скоупы, квалификаторы, kapt vs KSP), `Koin` (Service Locator, checkModules), `Hilt` был готов, `Dagger` сведён к указателю.
- [x] Patterns: GoF — индекс переписан, все 23 паттерна с «зачем» и примером из Android SDK в трёх таблицах; в 6 существующих заметок (Singleton, Builder, Decorator, Observer, Command, Iterator) добавлены разделы «В Android SDK», грабли и вопросы-ловушки.
- [x] UI: `View. Lists. RecyclerView` — код адаптера, DiffUtil/ListAdapter развёрнуто, payload, производительность, грабли; `2 Fragments` — два жизненных цикла, утечка binding, add vs replace, варианты commit, Fragment Result API (блок про три направления навигации перенесён в `Navigation. Types`).

## Этап 3 — P3 — СДЕЛАНО
- [x] Data structures: `3. Linked list` и `Trees. Binary search tree` переписаны (были определения без объяснений), в `5. Priority queue` добавлен разбор двоичной кучи. AVL, Red-black, Splay, Treap, B-tree, `Trees`, `4. Queue` проверены — уже learning-grade.
- [x] Algorithms: `Linear search`, `Selection sort`, `Binary search` (был некомпилирующийся код) переписаны с разбором и сложностью; `Sorting algorithms used` — добавлены устройство Timsort, таблица алгоритмов, нижняя граница O(n log n), зачем устойчивость.

## Этап 4 — Senior-темы: тестирование, CI/CD, KMP, архитектурный собес
Этапы 1–3 закрывали «знаю ли я платформу». Этот этап про то, что спрашивают
**отдельными секциями на senior-собесе** и чего в конспектах почти нет.
Состояние на старте этапа: `Android/Test/Testing` — 59 строк, где код-ревью
перемешан с JUnit; `Teamwork/CI-CD for Android` — 39 строк; `KMP` — 44 строки;
`Patterns/Mobile app system design` — есть каркас, но без разборов задач.

### 4.1 Тестирование (самый большой пробел) — СДЕЛАНО
- [x] `Android/Test/Testing` — переписан как вход в тему: пирамида тестов,
      что тестировать в каждом слое, признаки хорошего теста, given/when/then,
      где лежат тесты и почему, про покрытие. Код-ревью вынесен в
      `Teamwork/Code review`.
- [x] `Test. Unit tests` — JUnit4 vs JUnit5, структура и именование,
      assertEquals с delta и почему, параметризованные тесты, грабли.
- [x] `Test. Test doubles` — пять видов, почему fake обычно лучше мока,
      mockk и когда моки оправданы, in-memory Room и MockWebServer.
- [x] `Test. Coroutines and Flow` — `runTest` и виртуальное время,
      подмена Main vs внедрение диспетчеров, Standard vs Unconfined,
      Turbine, конфлейт `StateFlow` как источник флака.
- [x] `Test. UI` — Espresso и Compose semantics, синхронизация,
      таблица причин флака, Robolectric, что стоит покрывать.
- [x] Проверен `JUnit`-проект в корне. Найдено (код не правил — учебный
      полигон, решение за автором):
      1. нет `useJUnitPlatform()` в `testOptions` — тесты на `@ParameterizedTest`
         (JUnit 5) движком не подхватываются, хотя зависимости подключены;
      2. `CalculatorTest`: в тесте «50 + 100 = 150» стоит `expected = 151`;
      3. смешаны три поколения: `junit.framework.Assert` (JUnit 3),
         `org.junit.Test` (JUnit 4), `org.junit.jupiter.params` (JUnit 5);
      4. `*.jar` в `.gitignore` исключает `gradle-wrapper.jar` — ни один
         Gradle-проект репозитория не собирается из свежего клона.

### 4.2 CI/CD — СДЕЛАНО
- [x] `Teamwork/CI-CD for Android` — каркас был неплох, добавлена конкретика:
      таблица «что гонять на PR / на merge / ночью» с ориентиром 10–15 минут,
      рабочий пример GitHub Actions (проверки + релиз по тегу), signingConfig
      через переменные окружения.
- [x] Время сборки как метрика: build/configuration cache, многомодульность,
      KSP, `--scan`, время до обратной связи вместо общего времени джоб.
- [x] Безопасность: секреты не в логах, PR из форков без доступа к секретам,
      Play App Signing; версионирование (`versionCode` строго возрастает,
      тег как триггер релиза); публикация и **невозможность отката** в Play,
      отсюда staged rollout и feature flags; типичные проблемы (флак, рост
      времени сборки, «работает у меня»).

### 4.3 KMP
- [ ] `KMP. Kotlin Multiplatform` — углубить: альтернативы `expect/actual`
      (интерфейс + DI), иерархия source sets, статус Compose Multiplatform.
- [ ] Внутри или отдельно: интеграция с iOS (Kotlin/Native, XCFramework,
      как код выглядит из Swift), ограничения и подводные камни.
- [ ] Экосистема: Ktor, SQLDelight/Room KMP, koin, kotlinx-datetime —
      что уже мультиплатформенное.

### 4.4 Архитектурный собес (senior)
- [ ] `Patterns/Mobile app system design` — расширить каркас: чеклист
      уточняющих вопросов, как проговаривать trade-offs, типичные ошибки
      кандидата (сразу в код, без требований).
- [ ] `Architecture interview. Типовые задачи` — разборы: лента с оффлайном,
      чат с real-time, загрузка/шеринг файлов, приложение с картами.
- [ ] Проверить `IDE/Multi-module architecture` — модуляризация по фичам,
      как это защищать на собесе (границы, скорость сборки, команда).
- [ ] Смежное: масштабирование команды, feature flags, A/B, миграции
      (View → Compose, one module → multi-module).

## Как продолжать
1. Взять следующий незакрытый пункт (сейчас — Этап 4).
2. Открыть заметку, переписать под критерий learning-grade (кратко, но достаточно).
3. Отметить `[x]` здесь и закоммитить.
