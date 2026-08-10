# ViewModel и ViewModelProvider

## ViewModel
Компонент Jetpack, который **хранит и управляет состоянием UI**, переживая изменения конфигурации (поворот экрана). Отделяет логику от View. См. [[ViewModel]], [[Android app architecture]].

Ключевые свойства:
- Живёт дольше, чем Activity/Fragment при пересоздании из-за конфигурации: при повороте Activity уничтожается и создаётся заново, а **тот же** экземпляр ViewModel сохраняется.
- Очищается (`onCleared()`), когда владелец уничтожается окончательно (Activity finish, Fragment снят без back stack).
- **Не должна** держать ссылку на View/Context/Activity → иначе утечка памяти. Для контекста приложения — `AndroidViewModel(application)`.
- Имеет свой `viewModelScope` (CoroutineScope, отменяется в `onCleared()`).

## Почему ViewModel нельзя создавать через конструктор
Если написать `MyViewModel()` вручную, она привяжется к текущему экземпляру Activity и **не переживёт пересоздание** — потеряется весь смысл. Экземпляр должен создавать и кэшировать фреймворк.

## Как ViewModel переживает поворот (частый senior-вопрос)
Хранилище ViewModel (`ViewModelStore`) удерживается **`ViewModelStoreOwner`** (Activity/Fragment). При изменении конфигурации Activity передаёт `ViewModelStore` через механизм `NonConfigurationInstances` в новый экземпляр — сама ViewModel в куче не пересоздаётся. При окончательном уничтожении store очищается.

> Важно: ViewModel переживает **изменение конфигурации**, но **НЕ смерть процесса**. От process death спасает `SavedStateHandle` / `onSaveInstanceState`. См. [[Bundle]].

## ViewModelProvider
Фабрика-посредник, которая либо возвращает уже существующую ViewModel из `ViewModelStore` владельца, либо создаёт новую через `Factory`:

```kotlin
val vm = ViewModelProvider(this, factory)[MyViewModel::class.java]
// либо KTX-делегаты:
val vm by viewModels { factory }           // Activity
val vm by activityViewModels()             // общая ViewModel Activity для фрагментов
```

- `ViewModelStoreOwner` — владелец store (Activity/Fragment/NavBackStackEntry).
- `Factory` — как создать ViewModel с параметрами конструктора. См. [[2 ViewModelFactory]].
- Общая ViewModel через `activityViewModels()` — способ связать два фрагмента (кнопка в одном меняет TextView в другом).

## SavedStateHandle
Map-хранилище внутри ViewModel, которое переживает и конфигурацию, и **смерть процесса** (пишется в сохранённое состояние). Сюда кладут минимально необходимое для восстановления экрана (id, введённый текст).

Связано: [[2 ViewModelFactory]], [[ViewModel]], [[StateFlow]], [[LiveData vs StateFlow]]
