# Dagger 2

**Dagger 2** — DI-фреймворк с **кодогенерацией**: на этапе компиляции он строит граф зависимостей и генерирует код, который их создаёт. Ошибки («не знаю, как создать `UserRepository`») ловятся **при сборке**, а не в рантайме — это его главное преимущество и причина, по которой он до сих пор стандарт в больших проектах.

Название — от **направленного ациклического графа** (Directed Acyclic Graph): зависимости образуют именно такой граф, и Dagger умеет находить в нём циклы.

![](<../../images/Pasted image 20241113161811.png>)

## Как Dagger узнаёт, что создавать

### @Inject на конструкторе
Простейший случай: помечаем конструктор — Dagger видит и сам класс, и его зависимости.
```kotlin
class MusicRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource
)

class RemoteDataSource @Inject constructor()
class LocalDataSource @Inject constructor()
```
Рекурсивно: чтобы собрать `MusicRepository`, Dagger должен уметь собрать и его аргументы — поэтому `@Inject` нужен и на них.

### @Module + @Provides
Когда конструктор недоступен (сторонняя библиотека) или нужен нетривиальный способ создания:
```kotlin
@Module
class DataModule {
    @Provides
    fun provideDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "db").build()
}
```

### @Binds
Когда нужно просто связать интерфейс с реализацией. Предпочтительнее `@Provides`: метод абстрактный, Dagger не генерирует лишний класс-фабрику, сборка чуть быстрее.
```kotlin
@Module
abstract class RepoModule {
    @Binds
    abstract fun bindRepo(impl: MusicRepositoryImpl): MusicRepository
}
```

## Component — держатель графа
Компонент — интерфейс, из которого Dagger генерирует реализацию (`DaggerAppComponent`). В нём объявляют, что можно достать наружу и куда внедрять.
```kotlin
@Singleton
@Component(modules = [DataModule::class, RepoModule::class])
interface AppComponent {
    fun repository(): MusicRepository        // provision-метод: достать зависимость
    fun inject(activity: MainActivity)       // field injection: внедрить в готовый объект

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
```
```kotlin
val appComponent = DaggerAppComponent.factory().create(applicationContext)
val repo = appComponent.repository()
```
Приложений-графов может быть несколько — Dagger должен понимать, в каком искать. Обычно корневой компонент создают в `Application` и хранят там.

## Scope — время жизни
Без скоупа **каждый запрос создаёт новый объект**. Аннотация-скоуп говорит: «один экземпляр на время жизни компонента».
```kotlin
@Singleton
class AppDatabase @Inject constructor()
```
`@Singleton` — не «один на приложение», а «один на компонент, помеченный `@Singleton`». Если создать два таких компонента, будет два объекта.

На практике `@Singleton` часто заменяют **своими** скоупами: по имени сразу видно время жизни, а `@Singleton` в коде класса ничего не говорит о том, где создаётся компонент.
```kotlin
@Scope @Retention(AnnotationRetention.RUNTIME) annotation class AppScope
@Scope @Retention(AnnotationRetention.RUNTIME) annotation class ScreenScope
```

## Subcomponent и зависимости компонентов
Экранным графам нужен доступ к приложенческому. Два способа:
- **`@Subcomponent`** — дочерний компонент видит **весь** граф родителя;
- **`dependencies = [AppComponent::class]`** — видит только то, что родитель явно объявил provision-методами (более строгая изоляция, удобно в многомодульных проектах).

## Qualifier — две зависимости одного типа
```kotlin
@Qualifier annotation class IoDispatcher
@Qualifier annotation class MainDispatcher

@Provides @IoDispatcher fun provideIo(): CoroutineDispatcher = Dispatchers.IO

class Repo @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher)
```
Есть готовый `@Named("io")`, но собственные квалификаторы безопаснее: опечатку в строке компилятор не поймает.

## Lazy и Provider
```kotlin
class Screen @Inject constructor(
    private val lazyRepo: dagger.Lazy<Repo>,   // создастся при первом get()
    private val provider: Provider<Repo>       // новый объект на каждый get()
)
```

## kapt vs KSP
Dagger исторически работал через **kapt** — он генерирует Java-стабы для всего кода и заметно замедляет сборку. Современная замена — **KSP**, который читает Kotlin напрямую и работает в разы быстрее. Dagger/Hilt поддерживают KSP — на собеседовании это частый вопрос про скорость сборки.

## Dagger vs Hilt vs Koin
| | Dagger 2 | Hilt | Koin |
| --- | --- | --- | --- |
| Механизм | кодогенерация | кодогенерация поверх Dagger | Service Locator, DSL на Kotlin |
| Ошибки графа | на компиляции | на компиляции | **в рантайме** |
| Boilerplate | много (компоненты, скоупы вручную) | мало, готовые скоупы | минимум |
| Скорость сборки | медленнее (kapt/KSP) | медленнее | не влияет |
| Когда брать | большой проект, нужен полный контроль | Android-проект по умолчанию | небольшой проект, быстрый старт, KMP |

Hilt — это тот же Dagger с готовыми компонентами под жизненный цикл Android, см. [[Hilt]].

## Вопросы-ловушки
- Чем `@Binds` лучше `@Provides`? → абстрактный метод без тела, меньше сгенерированного кода; годится только для «интерфейс → реализация».
- `@Singleton` гарантирует один объект на приложение? → нет, один на **экземпляр компонента** с этим скоупом.
- Что будет, если забыть `@Inject` на конструкторе зависимости? → ошибка компиляции вида «cannot be provided without an @Inject constructor» — в этом и ценность Dagger.
- Чем `Lazy<T>` отличается от `Provider<T>`? → `Lazy` кэширует первый созданный объект, `Provider` создаёт новый на каждый вызов.
- Почему Dagger замедляет сборку и что с этим делать? → kapt-обработка аннотаций; переход на KSP и разбиение на модули.

Связано: [[Hilt]], [[Koin]], [[Dependency injection]], [[2 ViewModelFactory]], [[Multi-module architecture]]
