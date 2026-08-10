# Performance. Профилирование и оптимизация UI

Senior обязан уметь диагностировать «тормозит скролл / фризы / долгий старт» и знать инструменты. Классический вопрос: «список товаров дёргается при скролле — что делать?».

## Бюджет кадра (jank)
- Экран 60 Гц → на кадр **16.6 мс** (120 Гц → 8.3 мс). Не уложился = пропущенный кадр = **jank** (дёрганье).
- **ANR** — главный поток заблокирован >5 сек (или broadcast >10 сек). См. [[ANR. Application Not Responding]].
- Всё тяжёлое (I/O, парсинг, БД, сеть) — вне main thread. См. [[Coroutines]].

## Инструменты профилирования
- **Android Studio Profiler** — CPU, Memory, Energy, Network в реальном времени.
- **Systrace / Perfetto** — системная трассировка, поиск долгих кадров и их причин.
- **Layout Inspector** — глубина иерархии View, overdraw, рекомпозиции Compose.
- **GPU Overdraw** (Developer Options) — сколько раз пиксель перерисован (красный = плохо).
- **Profile GPU Rendering** — столбики кадров относительно линии 16 мс.
- **Macrobenchmark / Microbenchmark** — измеримые метрики старта, скролла, jank в CI.
- **StrictMode** — ловит дисковые/сетевые операции на main thread в дебаге.
- **LeakCanary** — утечки. См. [[Memory leaks. Detection]].

## Оптимизация списков (RecyclerView)
- **ViewHolder** + переиспользование View; не раздувать layout в `onBindViewHolder`.
- **DiffUtil** / `ListAdapter` — точечные обновления вместо `notifyDataSetChanged()`. См. [[View. Lists. RecyclerView]].
- `setHasStableIds(true)`, `setHasFixedSize(true)`.
- Уменьшить вложенность layout (плоский `ConstraintLayout` вместо вложенных `LinearLayout`), убрать overdraw.
- Изображения — через Coil/Glide с ресайзом и кэшем; не декодировать big bitmap в UI-потоке.
- Пагинация (Paging 3) вместо загрузки всего списка.

## Оптимизация Compose
- Минимизировать recomposition: **stable** типы, `key` в списках, `derivedStateOf`, вынос состояния. См. [[Recomposition and stability]].
- `LazyColumn` с `key`; избегать чтения часто меняющегося state высоко в дереве.
- Не создавать объекты/лямбды в hot path без `remember`.

## Старт приложения
- Холодный старт: минимизировать работу в `Application.onCreate` и первом кадре.
- **App Startup** библиотека, ленивая инициализация, **Baseline Profiles**. См. [[Baseline Profiles and App Startup]].

## Память
- Избегать утечек (см. [[Memory leaks. Detection]]), крупных bitmap, лишних аллокаций в `onDraw`/рекомпозиции → меньше GC-пауз. См. [[Garbage collector]].

## Алгоритм ответа на собесе
1. Измерить (Profiler/Perfetto), а не гадать.
2. Найти узкое место (main thread? overdraw? аллокации? layout?).
3. Устранить (вынести в фон / DiffUtil / упростить иерархию / кэш).
4. Подтвердить измерением (benchmark).

Связано: [[ANR. Application Not Responding]], [[Memory leaks. Detection]], [[Recomposition and stability]], [[View. UI rendering stages]]
