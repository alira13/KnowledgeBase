1. Подключить Koin

```Kotlin
implementation ("io.insert-koin:koin-android:3.3.0")
```

1. Создать пакет di и в нем файлы для Koin DataDi, DomainDi, AppDi.kt
2. Создать модули для каждого объекта. Для data как правило делают singleton, для use cases factory

```Kotlin
val dataModule = module {

    single<StorageCLient> {
        SharedPrefStorageClientImpl(context = get())
    }

    single<DataRepository> {
        DataRepositoryImpl(storageCLient = get())
    }
}
```

1. Создать appModule и в нем viewModel через Koin viewModel

```Kotlin
val appModule = module {
    viewModel<MainViewModel>{
        MainViewModel(dataIterator = get())
    }
}
```

1. В Activity убрать viewModelFactory и заменить создание viewModel на

```Kotlin
    private val viewModel by viewModel<MainViewModel>()
```

1. Создать App.kt:Application - точка входа, откуда будет разворачиваться koin.
2. Запустить koin в onCreate(). Указать модули и AppContext

```Kotlin
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

1. Добавить App в манифест

```Kotlin
    <application
        android:name=".app.App"
        ...
```

1. Удалить Creator