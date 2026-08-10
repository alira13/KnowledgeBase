# Hilt

**Hilt** — надстройка над [[Dagger2]] от Google, стандартный DI для Android. Основан на Dagger (кодогенерация, проверка графа на этапе компиляции), но проще: убирает boilerplate компонентов, даёт готовые скоупы, привязанные к жизненному циклу Android-компонентов.

## Ключевые аннотации
- `@HiltAndroidApp` — на `Application`, точка входа, генерирует корневой компонент.
- `@AndroidEntryPoint` — на `Activity`/`Fragment`/`Service`/`View`, включает инъекцию.
- `@Inject` — конструктор или поле для внедрения.
- `@Module` + `@InstallIn(...)` — модуль привязок, устанавливается в конкретный компонент.
- `@Provides` — предоставить объект (когда нет своего конструктора: сторонние классы, интерфейсы).
- `@Binds` — связать интерфейс с реализацией (эффективнее `@Provides`, абстрактный метод).
- `@HiltViewModel` — интеграция с `ViewModel`, инъекция в конструктор + `by viewModels()`.

## Компоненты и скоупы (жизненный цикл)
| Компонент | Скоуп | Живёт |
|---|---|---|
| SingletonComponent | `@Singleton` | всё приложение |
| ActivityRetainedComponent | `@ActivityRetainedScoped` | переживает пересоздание Activity |
| ViewModelComponent | `@ViewModelScoped` | жизнь ViewModel |
| ActivityComponent | `@ActivityScoped` | Activity |
| FragmentComponent | `@FragmentScoped` | Fragment |

Скоуп = один экземпляр на время жизни компонента (в противном случае — новый объект на каждый запрос).

## Пример
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds @Singleton
    abstract fun bindRepo(impl: UserRepositoryImpl): UserRepository
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel()
```

## Hilt vs Koin (частый вопрос)
- **Hilt** — кодогенерация (KSP/kapt), ошибки графа на **этапе компиляции**, но замедляет сборку; выбор для больших/командных проектов.
- **Koin** — Service Locator на Kotlin DSL, без кодогена, ошибки в **рантайме**, легче стартовать. См. [[Koin]].

Связано: [[Dagger2]], [[Dependency injection]], [[Koin]], [[2 ViewModelFactory]]
