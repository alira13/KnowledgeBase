# Context

**Context** — абстрактный класс, дающий доступ ко всему, что принадлежит не тебе, а системе: ресурсам (строки, drawable, темы), системным сервисам, файлам приложения, БД и `SharedPreferences`, запуску компонентов (`startActivity`, `bindService`, `sendBroadcast`). Проще говоря — это **ссылка на окружение, в котором работает приложение**.

Почти любой Android-API просит `Context` именно поэтому: без него нельзя ни достать строку по `R.string.*`, ни открыть файл, ни узнать текущую тему.

![](<../../images/Pasted image 20250327173802.png>)

## Иерархия классов
```
Context (абстрактный)
└── ContextWrapper                     ← хранит ссылку mBase на «настоящий» контекст
    ├── Application
    ├── Service
    └── ContextThemeWrapper            ← добавляет тему
        └── Activity
```
`ContextWrapper` — обёртка-делегат: все вызовы он переадресует внутреннему **base context** (`mBase`), который создаёт система. Такая конструкция позволяет подменить или дополнить поведение контекста, ничего не меняя в системном. На этом построены, например, `createConfigurationContext()` для смены локали и обёртки Hilt.

Отсюда следствие: у созданной Activity в наличии сразу **три** контекста —
- `applicationContext` — один на всё приложение;
- `baseContext` (`getBaseContext()`) — системный, создаётся заново для каждой Activity;
- сама Activity (`this`) — она же `ContextThemeWrapper`, то есть **контекст с темой**.

У `Service` — то же самое, только вместо Activity-контекста контекст сервиса (темы у него нет).

## Типы и время жизни
| Контекст | Как получить | Живёт | Для чего |
| --- | --- | --- | --- |
| Application | `applicationContext`, `getApplication()` | весь процесс | долгоживущие объекты: Room, DataStore, DI-граф, WorkManager |
| Activity | `this` в Activity | до `onDestroy()` активити | UI: inflate, диалоги, `startActivity`, темы, `Toast` |
| Service | `this` в Service | до `onDestroy()` сервиса | работа внутри сервиса |
| Fragment | `requireContext()` | между `onAttach` и `onDetach` | UI фрагмента |
| BroadcastReceiver | параметр `onReceive` | только время `onReceive` | ограниченный: нельзя показывать диалог, нельзя `bindService` |

Главный критерий выбора — **время жизни**. Контекст, который живёт дольше объекта, безопасен; наоборот — нет.

## Два симметричных способа выстрелить себе в ногу

**1. Activity-контекст в долгоживущем объекте → утечка памяти.**
```kotlin
object Repo {
    lateinit var context: Context   // передали активити — она никогда не соберётся GC
}
```
Синглтон живёт весь процесс, а Activity пересоздаётся при каждом повороте — старые экземпляры со всей иерархией View остаются в памяти. Правильно — `context.applicationContext`. См. [[Memory leaks. Detection]].

**2. Application-контекст там, где нужен UI → неправильный вид или краш.**
`Application` наследует `ContextWrapper`, а **не** `ContextThemeWrapper` — у него нет темы приложения. Последствия:
- `AlertDialog(applicationContext)` — падает (`WindowManager$BadTokenException`) или игнорирует стили;
- `LayoutInflater.from(applicationContext)` — вёрстка без атрибутов темы;
- `startActivity(intent)` с application-контекстом требует флага `FLAG_ACTIVITY_NEW_TASK`, иначе исключение.

Практическое правило: **всё, что рисует на экране, — Activity-контекст; всё, что живёт дольше экрана, — application-контекст.**

## Класс Application
Создаётся системой **первым**, до любой Activity или Service, и живёт до конца процесса. Точка инициализации глобального: DI, логирование, аналитика.
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin { androidContext(this@MyApplication) }
    }
}
```
```xml
<application android:name=".MyApplication" ...>
```
Порядок старта: процесс → `Application` + `onCreate()` → первая Activity и её контекст. См. [[App startup process]].

Важно: `Application` — **не место для хранения состояния**. При process death процесс убивают, поля обнуляются, а пользователь возвращается на восстановленный экран — и получает `null` там, где ждал данные. Состояние — в БД/DataStore, экранное — в `ViewModel`/`SavedStateHandle`.

Тонкость терминологии: `getApplication()` возвращает `Application`, `getApplicationContext()` — `Context`; фактически это один и тот же объект.

## Полезное на практике
- `ContextCompat.getColor(context, R.color.x)`, `ContextCompat.getSystemService(...)` — совместимые обёртки вместо устаревших методов.
- В Compose контекст берут через `LocalContext.current` — это контекст Activity.
- Во фрагменте `requireContext()` бросит понятное исключение, если фрагмент не прикреплён, — лучше, чем `context!!`.
- Утечки контекста ловит **LeakCanary** — это её самый частый улов.

## Вопросы-ловушки
- Можно ли передавать Activity-контекст в другие классы? → можно, но только если объект живёт не дольше активити; иначе — утечка. Для долгоживущих — `applicationContext`.
- Почему `AlertDialog` с `applicationContext` падает? → у application-контекста нет темы и нет window token; диалогу нужен `ContextThemeWrapper`, то есть Activity.
- Чем `this` в Activity отличается от `baseContext`? → `this` — `ContextThemeWrapper` с темой, `baseContext` — системный контекст внутри обёртки без неё.
- `getApplication()` и `getApplicationContext()` — одно и то же? → возвращают один объект, различаются типом.
- Что делать, если ссылку на Activity надо сохранить надолго? → не сохранять; в крайнем случае `WeakReference` и проверка на `isFinishing`, но обычно это сигнал о неверной архитектуре.

Источник: [Android Context 101 with class diagram](https://dev.to/vtsen/android-context-101-with-class-diagram-207n)

Связано: [[1 Activity]], [[App startup process]], [[Memory leaks. Detection]], [[2 Services and WorkManager]], [[4 Broadcast Receiver]]
