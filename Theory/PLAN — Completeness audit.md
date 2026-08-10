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

## Этап 1 — P1: ядро (спрашивают почти всегда)
Довести до learning-grade. Уже сделано как образец — отмечено.
- [x] `Android/Garbage collector` — GC + **daemon-потоки** (была опечатка «Dymon», исправлено в `Kotlin/4 Concurrency/0 Threads 1 Basics (thread)`).
- [x] `Patterns/Clean architecture` — слои + правило зависимостей + Dependency Inversion.
- [x] `Algorithms/Algorithm complexity` — классы O(1..n!), как считать, best/avg/worst, память.
- [x] `Kotlin/3 Classes and interfaces/Generics. Basics` — типобезопасность, bounds, вариантность, стирание.
- [x] `Kotlin/3.../Classes. Inner and Nested` — nested vs inner, ссылка на Outer, утечки, код.
- [ ] `Kotlin/3.../Generics. Variance (in, out)` — проверить глубину, добавить PECS и примеры.
- [ ] `Kotlin/3.../Generics. Type erasure` — почему нельзя `is List<String>`, reified.
- [ ] `Kotlin/3.../Classes. toString, equals, hashCode, copy` — контракт equals/hashCode, зачем.
- [ ] `Kotlin/4 Concurrency/0 Threads 3 Synchronized`, `Thread safety` — гонки, volatile, атомарки, монитор.
- [ ] `Kotlin/4 Concurrency/2 Coroutines 5 Exception handling`, `2 CoroutineScope` — SupervisorJob, отмена.
- [ ] `Kotlin/Collections/Collections. Overview` — List/Set/Map, HashMap изнутри (бакеты, коллизии, O).
- [ ] `Android/App components/1 Activity` — ЖЦ подробно, config change, process death.
- [ ] `Android/App/Context` — типы контекста, где какой, утечки.
- [ ] `Android/UI/Bundle`, `Android/Data/0 Serialization...` — Parcelable vs Serializable, лимит Bundle.
- [ ] `Kotlin/2 Functions/Functions. Operator overloading`, `Functions. Generic (parameterized)` — сейчас заглушки.

## Добавленные темы (частые в РФ-бигтехе) — с примерами кода
- [x] `Android/App components/Handler, Looper, MessageQueue` — устройство UI-потока, HandlerThread, утечки.
- [x] `Kotlin/4 Concurrency/Java Memory Model (happens-before)` — видимость, volatile, атомарность.
- [x] `Kotlin/4 Concurrency/Coroutines under the hood (suspend, Continuation)` — CPS, state machine, почему дешевле потоков.
- [x] `Android/Networking. Retrofit and OkHttp` — интерцепторы, кэш, ошибки, Authenticator.
- [ ] Ещё кандидаты: Binder/AIDL (IPC подробно), Paging 3, Compose phases детально, KAPT vs KSP, Dagger component graph.

## Этап 2 — P2
- [ ] Kotlin: `Functions. Scope functions`, `Functions. Delegates`, `Types. Unit, Nothing, Any, null`.
- [ ] Android: `App components/2 Services and WorkManager`, `App components/4 Broadcast Receiver`, DI (`Dagger2`, `Hilt`, `Koin`).
- [ ] Patterns: GoF (проверить каждый на «зачем + пример в Android SDK»).
- [ ] UI: `View. Lists. RecyclerView` (DiffUtil), `2 Fragments`.

## Этап 3 — P3
- [ ] Data structures: деревья, очереди, связные списки — проверить, что объясняют, а не только определяют.
- [ ] Algorithms: поиск/сортировки — добавить разбор и сложность.

## Как продолжать
1. Взять следующий незакрытый пункт P1.
2. Открыть заметку, переписать под критерий learning-grade (кратко, но достаточно).
3. Отметить `[x]` здесь и закоммитить.
