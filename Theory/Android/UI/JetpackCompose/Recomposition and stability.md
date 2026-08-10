# Recomposition и stability

Ключевая тема Compose на senior-собесе: как работает перекомпозиция и как её не «сломать».

## Что такое recomposition
При изменении читаемого `State` Compose перевыполняет composable-функции, которые **читают** это состояние, и обновляет дерево UI. См. [[Compose Lifecycle]].

Свойства, из которых следуют все правила:
- Может выполняться **в любом порядке**;
- **параллельно** (на разных потоках);
- **пропускаться** (skip), если входные данные не изменились;
- **прерываться и перезапускаться**.

→ Поэтому composable должны быть **чистыми** (без side-effects в теле), **быстрыми**, **идемпотентными**, не зависеть от порядка и не иметь состояния вне `remember`.

## Skipping и stability (главное)
Compose пропускает рекомпозицию composable, если **все** его параметры **stable** и не изменились (по `equals`). Тип считается **stable**, если:
- он immutable (все публичные свойства `val` и immutable), **или**
- помечен `@Stable`/`@Immutable`, **или**
- примитив / `String` / функциональный тип.

**Unstable** (ломает skipping → лишние рекомпозиции):
- классы с `var`-полями;
- коллекции интерфейсов `List`/`Map`/`Set` — компилятор считает их unstable (реализация может быть mutable!). Решение: `kotlinx.collections.immutable` (`ImmutableList`) или `@Immutable`-обёртка;
- классы из других модулей без Compose-компилятора.

```kotlin
@Immutable
data class UiState(val items: ImmutableList<Item>, val loading: Boolean)
```

## Инструменты диагностики
- **Compiler metrics / stability reports** — какие composable restartable/skippable, какие классы stable.
- **Layout Inspector** — счётчик рекомпозиций и пропусков по каждому composable.

## Приёмы снижения рекомпозиций
- **State hoisting** — поднимать состояние вверх, вниз передавать значения + лямбды (stateless composable).
- **`derivedStateOf`** — производное значение пересчитывается только при смене результата (например, `isScrolled = firstVisibleItem > 0`).
- **Отложенное чтение state** — передавать лямбду `() -> State`, а не значение, чтобы чтение происходило в фазе layout/draw (например, `Modifier.offset { }` вместо `offset(x)`).
- **`key`** в `LazyColumn` для корректного сопоставления элементов.
- **`remember`** для дорогих вычислений и объектов.
- Стабилизировать параметры (immutable-модели, `ImmutableList`).

## Фазы кадра Compose
1. **Composition** — что рисовать (дерево).
2. **Layout** — где (измерение + размещение).
3. **Drawing** — как (отрисовка).

Чтение state в поздней фазе (layout/draw) через лямбды не триггерит recomposition → дешевле анимации. Подробно — [[Compose phases]].

Связано: [[Compose phases]], [[Compose Lifecycle]], [[Jetpack Compose]], [[Performance. Profiling and UI optimization]], [[4 Flow 2 Flow as screen state]]
