# Koin

**Koin** — лёгкий DI-фреймворк на чистом Kotlin. Никакой кодогенерации и аннотаций: зависимости описываются **DSL-ом**, а граф собирается в рантайме.

Строго говоря, классический Koin — это **Service Locator**, а не полноценный DI: объект часто сам достаёт зависимость из реестра (`get()`, `by inject()`), вместо того чтобы получать её извне. На собеседовании это любят уточнять. При инъекции через конструктор (`viewModel { MainViewModel(get()) }`) разница стирается.

## Подключение
```kotlin
implementation("io.insert-koin:koin-android:3.5.0")
```

## Модули
Модуль — набор описаний «как создать объект». Обычно раскладывают по слоям: `dataModule`, `domainModule`, `appModule`.
```kotlin
val dataModule = module {
    single<StorageClient> { SharedPrefStorageClientImpl(context = get()) }
    single<DataRepository> { DataRepositoryImpl(storageClient = get()) }
}

val appModule = module {
    viewModel { MainViewModel(repository = get()) }
}
```
`get()` внутри лямбды — «достань зависимость нужного типа из графа». Тип берётся из сигнатуры, поэтому явно указывать его обычно не нужно.

### Три способа объявления
| Функция | Что делает | Типичное применение |
| --- | --- | --- |
| `single` | один экземпляр на всё приложение | репозитории, БД, сетевой клиент |
| `factory` | новый объект на каждый запрос | use case, мапперы |
| `viewModel` | привязка к `ViewModelStore` | ViewModel экрана |

Указывать интерфейс в угловых скобках (`single<DataRepository> { ... }`) важно: иначе объект зарегистрируется под типом реализации, и запрос по интерфейсу не найдёт его.

## Старт
```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(appModule, domainModule, dataModule))
        }
    }
}
```
```xml
<application android:name=".app.App" ... >
```
`androidContext()` регистрирует `Context` в графе — после этого `get()` внутри модулей отдаёт application-контекст.

## Получение зависимостей
```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()   // делегат, ленивое получение
    private val repo: DataRepository by inject()
}
```
`by viewModel()` и `by inject()` — property-делегаты (см. [[Functions. Delegates]]): объект достаётся при первом обращении.

## Квалификаторы и параметры
Две реализации одного типа различают именем:
```kotlin
single<Storage>(named("local")) { LocalStorage() }
single<Storage>(named("remote")) { RemoteStorage() }

val local: Storage by inject(named("local"))
```
Параметры, известные только в момент вызова, передают через `parametersOf`:
```kotlin
viewModel { (userId: String) -> DetailsViewModel(userId, get()) }

private val viewModel: DetailsViewModel by viewModel { parametersOf(userId) }
```

## Скоупы
Кроме `single`/`factory` есть скоупы, привязанные к времени жизни объекта:
```kotlin
scope<DetailsActivity> {
    scoped { DetailsPresenter(get()) }    // живёт, пока жива Activity
}
```

## Плюсы и минусы
**За:** нет кодогенерации — сборка не замедляется; DSL читается как обычный Kotlin; порог входа низкий; работает в KMP (`koin-core` без Android).

**Против:** ошибки графа вылезают **в рантайме** — забытая зависимость роняет приложение при открытии экрана, а не при сборке. Частично лечится тестом:
```kotlin
@Test fun checkModules() = checkModules {          // koin-test
    modules(appModule, dataModule, domainModule)
}
```
Такой тест обязателен в проекте на Koin — он возвращает ту самую проверку графа, которую Dagger делает бесплатно.

Ещё минус — разрешение зависимостей идёт через поиск по типу в рантайме, что чуть медленнее сгенерированного кода (на практике заметно редко).

## Грабли
- **Забыл `startKoin`** или обратился к графу раньше — `KoinApplication has not been started`.
- **Забыл интерфейс в `single<T>`** — зависимость не найдётся по типу интерфейса.
- **`single` там, где нужен `factory`** — состояние утекает между экранами.
- **Ссылка на Activity в `single`** — утечка на всё время жизни приложения. См. [[Context]].
- Модули не подключены в `modules(...)` — описание есть, а в графе объекта нет.

## Вопросы-ловушки
- Koin — это DI или Service Locator? → по механике Service Locator; инъекция через конструктор делает его использование похожим на DI.
- Когда узнаешь об ошибке в графе? → в рантайме при первом запросе; спасает `checkModules()` в тестах.
- Чем `single` отличается от `factory`? → один экземпляр на приложение против нового объекта на каждый запрос.
- Почему Koin не замедляет сборку, а Dagger замедляет? → у Koin нет обработки аннотаций и кодогенерации.
- Что выбрать для KMP? → Koin: он работает на общем коде, Hilt — только Android.

Связано: [[Dependency injection]], [[Hilt]], [[Dagger2]], [[Functions. Delegates]], [[KMP. Kotlin Multiplatform]]
