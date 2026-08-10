# Compose Lifecycle (жизненный цикл composable)

![](<images/composableLifecycle-480x236.png>)

Composable проходит через три события жизненного цикла:
1. **Enter the Composition** — composable впервые вызван и попал в дерево UI.
2. **Recompose 0..N раз** — при изменении читаемого `State` Compose перевыполняет composable, чтобы обновить дерево.
3. **Leave the Composition** — composable убран из дерева (условие перестало выполняться, элемент ушёл из `LazyColumn` и т.п.).

## Recomposition (перекомпозиция)
- Compose перезапускает **только те** composable, которые читают изменившийся `State` (intelligent recomposition), а не всё дерево.
- Может выполняться **параллельно**, в **любом порядке**, и **пропускаться** — поэтому composable должны быть **без side-effects** и **идемпотентны**. См. [[Recomposition and stability]].
- Пропуск (skipping) возможен, если параметры **stable** и не изменились.

## Identity и `key`
Compose отслеживает инстансы по позиции вызова в дереве. В списках используйте `key`, чтобы Compose корректно сопоставлял элементы при перестановке/удалении:
```kotlin
LazyColumn {
    items(users, key = { it.id }) { user -> UserRow(user) }
}
```

## `remember` и состояние между рекомпозициями
- `remember { }` — кэширует значение между рекомпозициями (сбрасывается при выходе из композиции).
- `rememberSaveable { }` — переживает пересоздание Activity/процесса (пишет в `Bundle`).
- `remember(key)` — пересчитывается при смене `key`.

## Side effects (управляемые эффекты)
Побочные эффекты нельзя писать прямо в теле composable — только через API, привязанные к жизненному циклу композиции:
- `LaunchedEffect(key)` — запуск корутины при входе/смене ключа, отмена при выходе.
- `DisposableEffect(key)` — эффект с очисткой (`onDispose`) — подписки, листенеры.
- `SideEffect` — публикация значения в не-Compose код на каждую успешную рекомпозицию.
- `rememberUpdatedState` — «свежая» ссылка внутри долгоживущего эффекта.
- `produceState`, `derivedStateOf`, `snapshotFlow` — производные состояния.

Связано: [[Jetpack Compose]], [[Recomposition and stability]], [[4 Flow 2 Flow as screen state]]
