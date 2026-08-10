---
tags: [собеседование, senior, шпаргалка, moc]
---
# 00 Собес Senior Android — Шпаргалка

Единая точка входа для **быстрого повторения перед собеседованием**. Тезисно, со ссылками на подробные заметки. Читать сверху вниз, проваливаться по вики-ссылкам в подробные заметки при пробелах.

Смежные оглавления: [[Interview questions bank]] (банк вопросов junior→senior), [[0. Complexity summary tables]].

---
## 🟣 Kotlin

- **val/var/const/lateinit/by lazy** — `const` compile-time, `lateinit` для non-null без сразу-значения (var, только объекты), `by lazy` — потокобезопасная ленивая инициализация (val). См. [[Variables. val, var, const, lateinit, by lazy]].
- **Null-safety** — `?`, `?.`, `?:`, `!!`, `let`. Platform types (`String!`) из Java — источник NPE. См. [[Types. 0 Nullable, not-null]], [[Platform type (nullability)]].
- **data / sealed / enum / value class** — `data` даёт equals/hashCode/copy/toString/componentN; `sealed` — закрытая иерархия (исчерпывающий `when` без else); `value class` (@JvmInline) — обёртка без аллокации. sealed vs enum: enum — фикс. набор инстансов, sealed — набор типов с разным состоянием. См. [[Classes. final, enum, data, sealed, abstract, object, companion]], [[Classes. Value class (@JvmInline)]].
- **object / companion object** — синглтон; companion — «статика» класса. См. [[GoF. Creational. Singleton]].
- **Scope-функции** — `let`(non-null, преобразование), `run`(вычисление блока), `with`(без ext), `apply`(конфиг, возвращает receiver), `also`(побочка). См. [[Functions. Scope functions (let, run, with, apply, also)]].
- **inline/noinline/crossinline/reified** — inline убирает overhead лямбд + `reified` (доступ к типу в рантайме, обход стирания). См. [[Functions. Inline, noinline, crossinline, reified]], [[Generics]].
- **Generics variance** — `out` (ковариант, producer), `in` (контравариант, consumer), star `*`. PECS. См. [[Generics]].
- **Extension functions** — резолвятся **статически** по типу ссылки. См. [[Functions. Higher-order and extensions]].
- **Делегаты** — `by lazy`, `by Delegates.observable`, кастомные `getValue/setValue`, `by` для интерфейсов. См. [[Functions. Delegates]].
- **Kotlin vs Java** — null-safety, data/sealed, extensions, корутины, нет checked exceptions, default args, всё «final by default».

### Ловушки Kotlin
- `IntArray` vs `Array<Int>` → `int[]` vs `Integer[]` (боксинг). См. конец [[Interview questions bank]].
- `hashCode() = Random.nextInt()` → сломает HashMap (объект «потеряется»); `= 1` → все в один бакет, O(n).
- `data class` не может быть `open`/наследоваться (до ограничений), componentN по primary-конструктору.
- Extension не переопределяется полиморфно (статическая диспетчеризация).

---
## 🟣 Корутины и Flow

- **Корутина** — легковесная «задача», не поток; тысячи на пуле потоков. Suspend не блокирует поток. См. [[Coroutines]], [[2 Coroutines 1 Suspend functions]].
- **Structured concurrency** — дочерние корутины привязаны к scope родителя; отмена/ошибка распространяются. См. [[2 Coroutines 0 Structured concurrency]].
- **Scope / Job / Dispatcher** — scope хранит контекст; `Job` — управление жизненным циклом/отменой; `Dispatchers`: Main (UI), IO (сеть/диск), Default (CPU), Unconfined. См. [[2 Coroutines 2 CoroutineScope]], [[2 Coroutines 3 Context (dispatcher, job, exceptionHandler)]].
- **launch vs async** — launch → Job (fire-and-forget), async → Deferred (`await`). См. [[2 Coroutines 4 Coroutine builders]].
- **Обработка ошибок** — try/catch вокруг suspend; `CoroutineExceptionHandler`; `SupervisorJob` — падение одного ребёнка не рушит остальных. `async` бросает при `await`. См. [[2 Coroutines 5 Exception handling]].
- **Отмена** — кооперативная: проверять `isActive`/`ensureActive`/suspend-точки; `CancellationException` не глотать. См. [[Coroutines. Cancellation]].
- **Flow** — холодный асинхронный поток (пересоздаётся на каждого коллектора). См. [[Flow]].
- **Cold vs Hot** — cold: Flow (по подписке); hot: **StateFlow** (state, всегда 1 значение, conflated), **SharedFlow** (события, настраиваемый replay/buffer). См. [[Flow]], [[StateFlow]], [[LiveData vs StateFlow]].
- **StateFlow vs LiveData** — StateFlow: Kotlin/KMP, всегда есть значение, нужен `repeatOnLifecycle` для lifecycle-aware сбора; LiveData: Android, lifecycle-aware сам. См. [[LiveData vs StateFlow]].
- **flowOn / catch / retry** — переключение контекста upstream / обработка ошибок / повтор. См. [[Flow]].
- **Channels** — горячий producer-consumer, suspend-аналог очереди. См. [[5 Channels]].

### Многопоточность (база)
- Thread/Lock/Mutex/Deadlock (4 условия Коффмана). См. [[thread, lock, mutex, deadlock]].
- synchronized / volatile / атомарки / race condition / потокобезопасные коллекции. См. [[0 Threads 3 Synchronized]], [[Thread safety]].
- Mutex в корутинах — suspend, не блокирует поток. См. [[2 Coroutines. Synchronization]].

---
## 🟣 Android SDK

- **Компоненты**: Activity, Service, BroadcastReceiver, ContentProvider (+ Intent их связывает). См. [[0 App components. Intent]].
- **Жизненный цикл Activity** — onCreate→onStart→onResume→(onPause→onStop→onDestroy). Кнопка Back с корневой → onPause→onStop→onDestroy. См. [[1 Activity]].
- **Изменение конфигурации** (поворот) → Activity пересоздаётся; спасает **ViewModel** (переживает config) + `onSaveInstanceState`/`SavedStateHandle` (переживает **смерть процесса**). См. [[1 ViewModel, ViewModelProvider]], [[Bundle]].
- **Context** — типы: Application (долгоживущий, без UI), Activity (UI, тема). Не держать Activity context в долгоживущих объектах → утечка. См. [[Context]].
- **Fragment** — ЖЦ + отдельный ЖЦ View (`viewLifecycleOwner`); binding обнулять в `onDestroyView`. Single Activity. Общение фрагментов — общая ViewModel (`activityViewModels`)/Fragment Result API. См. [[2 Fragments]].
- **Intent** — explicit (по классу) / implicit (по action+фильтру); **PendingIntent** (`FLAG_IMMUTABLE`), Sticky (deprecated). См. [[Intent]].
- **Service vs WorkManager** — foreground service для длительной видимой работы (музыка, трек); WorkManager — отложенная **гарантированная** работа с ограничениями (сеть/зарядка), переживает перезапуск. Есть лимиты/Doze. См. [[2 Services and WorkManager]].
- **BroadcastReceiver** — статик (манифест, ограничен с 8.0) / динамик (register в коде). LocalBroadcastManager **deprecated** → Flow/LiveData. См. [[4 Broadcast Receiver]].
- **Serializable vs Parcelable** — Parcelable быстрее (без рефлексии), `@Parcelize`; в Bundle кладут Parcelable. Bundle ограничен (~1 МБ TransactionTooLarge). См. [[0 Serialization. Serializable vs Parcelable]], [[Bundle]].
- **Хранение данных** — SharedPreferences/DataStore (ключ-значение), Room (БД/SQLite), файлы (internal/external), сеть. Секреты — EncryptedSharedPreferences/KeyStore. См. [[Data storage]], [[Databases]].
- **Разрешения** — normal/dangerous/signature; runtime-запрос для dangerous. См. [[Permissions]].
- **ANR** — main thread >5с. См. [[ANR. Application Not Responding]].

---
## 🟣 UI: View + Compose

- **RecyclerView** — ViewHolder (переиспользование), Adapter, LayoutManager, DiffUtil (`areItemsTheSame`/`areContentsTheSame`), `ListAdapter`. Оптимизация: DiffUtil вместо notifyDataSetChanged, stable ids, плоский layout. См. [[View. Lists. RecyclerView]].
- **Этапы отрисовки View** — measure → layout → draw; `invalidate()` (перерисовать) vs `requestLayout()` (переизмерить). CustomView: `onMeasure/onLayout/onDraw`, не аллоцировать в onDraw. См. [[View. UI rendering stages]], [[CustomView. View lifecycle]].
- **Compose** — декларативный UI, `@Composable` описывает UI как функцию state. См. [[Jetpack Compose]].
- **Recomposition** — перезапуск composable, читающих изменённый state; может идти параллельно/в любом порядке/пропускаться → composable чистые. См. [[Recomposition and stability]].
- **Stability / skipping** — stable/immutable параметры → пропуск рекомпозиции; `List` unstable → `ImmutableList`. См. [[Recomposition and stability]].
- **State** — `remember`, `rememberSaveable`, `mutableStateOf`, `derivedStateOf`; **state hoisting** (state вверх, событие вниз). См. [[Compose Lifecycle]].
- **Side effects** — `LaunchedEffect`, `DisposableEffect`, `SideEffect`, `rememberUpdatedState`, `snapshotFlow`. См. [[Compose Lifecycle]].
- **Фазы кадра** — composition → layout → drawing; читать часто меняющийся state в поздней фазе (лямбды) дешевле.

---
## 🟣 Архитектура

- **Слои** — UI (Compose+ViewModel) → Domain (use cases) → Data (repository+sources). UDF, SSOT, DI. См. [[Android app architecture]].
- **Clean Architecture** — зависимости внутрь, домен ни от чего не зависит, Dependency Inversion (интерфейс репозитория в domain, реализация в data). См. [[Clean architecture]].
- **MVC / MVP / MVVM / MVI** — MVVM: ViewModel + observable state; MVI: единый immutable State + Intent/Action + однонаправленный поток + reducer. См. [[Comparing MVC, MVP, MVVM, MVI]].
- **ViewModel** — держатель состояния, переживает config, без View/Context, `viewModelScope`. Создаётся через Provider/Factory, не конструктором. См. [[1 ViewModel, ViewModelProvider]].
- **Repository** — абстракция над источниками, SSOT, мапперы, обычно синглтон. См. [[Repository pattern]].
- **DI** — Hilt (кодоген, ошибки в compile-time) vs Koin (Service Locator, рантайм). См. [[Hilt]], [[Koin]], [[Dependency injection]].
- **Многомодульность** — скорость сборки, инкапсуляция, границы; не для мелких проектов. api vs implementation. См. [[Multi-module architecture]].
- **Паттерны GoF** — Singleton, Builder, Factory, Observer, Decorator, Command, Iterator, Strategy. В Android SDK: Builder (AlertDialog/Notification), Observer (LiveData/Flow), Adapter (RecyclerView), Factory (ViewModelFactory). См. [[GoF patterns]].

---
## 🟣 Performance, Memory, Security (senior-фокус)

- **Бюджет кадра** 16.6 мс; jank; профилирование (Profiler, Perfetto, Layout Inspector, Macrobenchmark, StrictMode). См. [[Performance. Profiling and UI optimization]].
- **Memory leaks** — причины (static/Context/inner class/Handler/незакрытые ресурсы), поиск (LeakCanary, heap dump, leak trace). См. [[Memory leaks. Detection]].
- **GC** — mark-sweep-compact, поколения, ART Concurrent Copying, типы ссылок (strong/soft/weak/phantom). См. [[Garbage collector]].
- **Старт приложения** — cold/warm/hot, App Startup, **Baseline Profiles** (AOT, +20–30%). См. [[Baseline Profiles and App Startup]].
- **R8/ProGuard** — shrink/optimize/obfuscate, keep-правила, mapping.txt. См. [[R8 and ProGuard. Minification and obfuscation]].
- **Security** — KeyStore, EncryptedSharedPreferences, SSL/TLS pinning (Network Security Config), FLAG_IMMUTABLE, OWASP Mobile Top 10. См. [[Security. SSL Pinning, KeyStore, secrets]].

---
## 🟣 Тестирование

- **Пирамида** — unit (JUnit, много) → integration → UI/instrumented (Espresso/Compose test, мало). См. [[Testing]].
- **ViewModel-тесты** — JUnit5, Turbine (Flow), AssertK, `UnconfinedTestDispatcher`, `runTest`, фейковые репозитории, SavedStateHandle. См. skill android-testing.
- **Моки** — MockK/Mockito; тест БД (in-memory Room); мок network (MockWebServer). Fake vs Mock.
- **TDD**, тестируемость через DI и интерфейсы.

---
## 🟣 Алгоритмы и структуры данных

- **Big-O** — оценка роста; таблицы сложностей. См. [[Algorithm complexity]], [[0. Complexity summary tables]].
- **Коллекции** — ArrayList (O(1) индекс, O(n) вставка в середину) vs LinkedList (O(1) вставка, O(n) индекс); HashMap (O(1) средн., бакеты + red-black при коллизиях в Java 8+), LinkedHashMap (порядок), TreeMap (red-black, сортировка). См. [[ArrayList]], [[LinkedList]], [[Trees. Balanced. Red-black]].
- **hashCode/equals контракт** — equals true ⇒ hashCode равны; обратное не обязательно. См. [[Classes. toString, equals, hashCode, copy]].
- **Поиск/сортировка** — бинарный O(log n), линейный O(n), сортировки. См. [[Binary search O(log n)]], [[Selection sort O(n^2)]].
- **Деревья** — BST, AVL (строгий баланс, быстрый поиск), Red-Black (TreeMap/HashMap), B-tree (БД-индексы). См. [[Trees]].

---
## 🟣 Kotlin Multiplatform / кросс-платформа
- KMP — шаринг логики (domain/data/network), нативный UI; `expect/actual`, commonMain, Kotlin/Native для iOS. См. [[KMP. Kotlin Multiplatform]].

---
## 🟣 Инструменты / процессы
- **Git** — merge vs rebase, flow, cherry-pick, reset/revert. См. [[Git]].
- **CI/CD** — pipeline (lint→test→build AAB→sign→publish), fastlane, staged rollout. См. [[CI-CD for Android]].
- **Gradle** — build.gradle, version catalog (.toml), convention plugins, api/implementation. См. [[Build.gradle]], [[Dependencies via .toml]].
- **System Design** — каркас ответа на «спроектируйте приложение». См. [[Mobile app system design]].

---
## ⚡ Вопросы-ловушки (быстрый прогон)
1. Как ViewModel переживает поворот, но НЕ смерть процесса? → ViewModelStore через NonConfigurationInstances; process death → SavedStateHandle.
2. Почему `List` в Compose делает лишние рекомпозиции? → интерфейс unstable → ImmutableList.
3. Чем корутинный `Mutex` отличается от `synchronized`? → suspend, не блокирует поток.
4. `launch` vs `async` при исключении? → launch кидает сразу в handler; async — при `await`.
5. StateFlow vs LiveData для UI-state? → StateFlow + `repeatOnLifecycle(STARTED)`.
6. Где интерфейс репозитория — в domain или data? → в domain (Dependency Inversion).
7. Утечка через Handler — почему и как чинить? → отложенное сообщение держит Activity; static + WeakReference + removeCallbacks.
8. Что реально уменьшает время сборки в multi-module? → инкрементальность + параллелизм + build cache, `implementation` вместо `api`.
9. Обфускация = защита секретов? → нет, только усложнение реверса; секреты в KeyStore.
10. equals есть, hashCode не переопределён — что в HashMap? → объект «теряется».

## ✅ Чек-лист «за вечер до собеса»
- [ ] Корутины: scope/job/dispatcher, отмена, ошибки, SupervisorJob
- [ ] Flow: cold/hot, StateFlow vs SharedFlow vs LiveData
- [ ] ЖЦ Activity/Fragment + config change + process death
- [ ] Compose: recomposition, stability, state hoisting, side effects
- [ ] Clean Arch + MVI + слои + DI
- [ ] Repository + SSOT + offline-first
- [ ] Memory leaks + инструменты, GC
- [ ] Performance: как диагностировать jank/тормозящий список
- [ ] Многомодульность: зачем/когда не надо, api vs implementation
- [ ] Security: KeyStore, SSL pinning
- [ ] System Design: каркас ответа на «спроектируйте X»
- [ ] Пробежать банк вопросов: [[Interview questions bank]]
