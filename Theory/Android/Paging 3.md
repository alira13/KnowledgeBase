# Paging 3

**Paging 3** — библиотека Jetpack для постраничной загрузки списков: подгружает данные кусками по мере скролла, кэширует их и сама отслеживает состояние загрузки.

Зачем это нужно: список из 10 000 элементов нельзя ни выкачать одним запросом (трафик, время, память), ни удержать в памяти целиком. Пагинация решает и то, и другое, но написанная руками обрастает состоянием: какая страница загружена, идёт ли запрос сейчас, что делать с ошибкой, как не запросить одну страницу дважды при быстром скролле. Paging 3 забирает это на себя.

## Три части
| Компонент | Роль |
| --- | --- |
| **`PagingSource`** | откуда брать страницу: ключ страницы → данные + ключи соседних страниц |
| **`Pager` / `PagingData`** | сборка потока страниц с настройками (`PagingConfig`) |
| **`PagingDataAdapter`** | адаптер RecyclerView, знающий про подгрузку; в Compose — `collectAsLazyPagingItems()` |

Поток данных: `PagingSource` → `Pager` → `Flow<PagingData<T>>` → UI.

## PagingSource
```kotlin
class UsersPagingSource(private val api: UsersApi) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val page = params.key ?: 1          // первая загрузка — ключа нет
        return try {
            val response = api.getUsers(page = page, size = params.loadSize)
            LoadResult.Page(
                data = response.items,
                prevKey = if (page == 1) null else page - 1,   // null = дальше некуда
                nextKey = if (response.items.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)              // библиотека покажет состояние ошибки и даст retry
        }
    }

    // с какого ключа продолжить после инвалидации (например, после поворота)
    override fun getRefreshKey(state: PagingState<Int, User>): Int? =
        state.anchorPosition?.let { state.closestPageToPosition(it)?.nextKey?.minus(1) }
}
```
Ключ (`Int` в примере) — это то, чем описывается страница: номер, offset или курсор из ответа сервера.

## Pager во ViewModel
```kotlin
val users: Flow<PagingData<User>> = Pager(
    config = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,        // за сколько элементов до конца начинать подгрузку
        enablePlaceholders = false
    ),
    pagingSourceFactory = { UsersPagingSource(api) }
).flow
    .map { pagingData -> pagingData.map { it.toUiModel() } }
    .cachedIn(viewModelScope)        // обязательно: кэш переживёт поворот экрана
```
`cachedIn` — самая частая забытая деталь: без него при каждом пересоздании подписки список загружается заново с первой страницы.

## В UI
```kotlin
// RecyclerView
class UserAdapter : PagingDataAdapter<User, Holder>(DiffCallback) { ... }
lifecycleScope.launch {
    viewModel.users.collectLatest { adapter.submitData(it) }
}

// Compose
val items = viewModel.users.collectAsLazyPagingItems()
LazyColumn {
    items(items.itemCount) { index -> items[index]?.let { UserRow(it) } }
}
```

## Состояния загрузки
```kotlin
adapter.loadStateFlow.collect { state ->
    progressBar.isVisible = state.refresh is LoadState.Loading   // первая загрузка
    errorView.isVisible = state.refresh is LoadState.Error
    footerProgress.isVisible = state.append is LoadState.Loading // подгрузка вниз
}
```
`refresh` — начальная загрузка или обновление, `append` — догрузка вперёд, `prepend` — назад. Для шапки и подвала со спиннером есть `withLoadStateHeaderAndFooter()`.

## Offline-first: RemoteMediator
Когда данные должны сохраняться в базу и показываться без сети, `PagingSource` берут из **Room** (DAO возвращает `PagingSource` автоматически), а сеть подключают через **`RemoteMediator`**: он срабатывает, когда локальных данных не хватает, загружает страницу и **пишет её в базу**. Единственный источник правды — база.

```kotlin
Pager(
    config = PagingConfig(pageSize = 20),
    remoteMediator = UsersRemoteMediator(db, api),
    pagingSourceFactory = { db.userDao().pagingSource() }
).flow
```

## Грабли
- **Забыт `cachedIn`** — перезагрузка списка при каждом повороте.
- **`getRefreshKey` возвращает null** — после `invalidate()` список прыгает в начало.
- **Нестабильные id в DiffUtil** — мигание и дубликаты при подгрузке. См. [[View. Lists. RecyclerView]].
- **`enablePlaceholders = true`** требует, чтобы источник знал общее число элементов; иначе в списке появятся `null`-элементы, которые UI обязан уметь отрисовать.
- **`pageSize` слишком мал** — частые запросы; слишком велик — долгая первая отрисовка. Ориентир: 2–3 экрана данных.
- Трансформации (`map`, `filter`) применяются **к каждой странице**, а не ко всему списку: отфильтровать «глобально» нельзя.

## Вопросы-ловушки
- Зачем `cachedIn(viewModelScope)`? → кэшировать `PagingData` в скоупе ViewModel, чтобы пережить пересоздание UI.
- Чем `PagingSource` отличается от `RemoteMediator`? → первый отдаёт страницу из одного источника, второй пополняет локальную базу из сети, оставляя её единственным источником правды.
- Что делает `getRefreshKey`? → определяет, с какой страницы продолжить после инвалидации, чтобы не потерять позицию.
- Можно ли отфильтровать весь список? → нет, трансформации работают постранично; фильтрация — задача источника или запроса.
- Как показать спиннер внизу при подгрузке? → `loadStateFlow.append` или `withLoadStateFooter`.

Связано: [[View. Lists. RecyclerView]], [[Databases]], [[Networking. Retrofit and OkHttp]], [[Flow]], [[Android app architecture]]
