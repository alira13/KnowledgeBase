In software development, architectural patterns are crucial for structuring applications for better code management, user interface (UI) updates, and data flow. This essay will delve into understanding four such patterns: Model-View-Controller (MVC), Model-View-Presenter (MVP), Model-View-ViewModel (MVVM), and Model-View-Intent (MVI).

## Model-View-Controller (MVC)

The MVC pattern is one of the oldest architectural patterns. It divides an application into three interconnected components. The 'Model' manages the data, logic, and rules of the application. The 'View' displays the data that the 'Model' components hold, and the 'Controller' accepts inputs and converts them to commands for the 'Model' or 'View'. Although MVC provides a clean separation of concerns, it often leads to controllers having too much logic and becoming difficult to manage.

## Model-View-Presenter (MVP)

MVP is a derivative of the MVC pattern where the 'Controller' is replaced with a 'Presenter'. In this pattern, the 'Presenter' acts as a middleman between 'Model' and 'View'. The 'View' is passive and displays whatever the 'Presenter' tells it to, making testing easier. However, the Presenter can end up with too much logic, similar to the Controller in MVC.

## Model-View-ViewModel (MVVM)

MVVM is another variation of MVC where the 'Controller' is replaced with a 'ViewModel'. The 'ViewModel' abstracts the 'View' and exposes public properties and commands. Instead of a two-way communication, as seen in MVP, MVVM has a one-way data flow, making it easier to manage. The drawback here is that complex scenarios can lead to a bloated 'ViewModel'.

## Model-View-Intent (MVI)

MVI is the latest pattern where every 'View' state is modeled as a state object. The 'View' then renders itself based on this state object. The 'Intent' represents an intention or desire to perform an action, either by the user or the app itself. MVI supports multiple event sources and immutability, making it more predictable. However, it could be overkill for simple applications due to its complexity and overhead.

## Conclusion

In conclusion, choosing between MVC, MVP, MVVM, and MVI depends on the specific requirements of the project. While MVC and MVP provide a simple and clear separation of concerns, they can lead to bloated controllers or presenters. On the other hand, MVVM simplifies data flow but can lead to bloated 'ViewModels'. Lastly, MVI offers predictability and supports multiple event sources, but might be too complex for straightforward applications. Thus, understanding these patterns and their trade-offs is crucial in creating efficient and manageable applications.

## Further considerations

While choosing the right architectural pattern is crucial, it's also important to consider the skills and experience of the development team. Implementing these patterns requires a certain degree of familiarity and expertise.

In the case of MVC, its longevity means that it is widely understood and there are abundant resources available for learning and troubleshooting. This could be beneficial for teams with less experienced developers or if quick development is required.

MVP is particularly well-suited to applications with complex user interfaces as it allows for a high degree of separation between the 'View' and the 'Model'. This can make it easier to manage and test the UI.

MVVM, with its one-way data flow, is excellent for projects where data-binding is a significant concern. It decouples the 'View' and the 'Model' to a greater extent than MVP, allowing developers to work on either component independently.

MVI is the most modern of the four patterns discussed here. Its focus on immutability and managing 'View' states as objects makes it an excellent choice for complex applications with multiple event sources. However, it is also the most complex of the patterns and requires a high degree of understanding to implement effectively.

Ultimately, the decision between MVC, MVP, MVVM, and MVI will depend not just on the specific requirements of the project, but also the expertise and preference of the development team.

# **🔹 MVVM (Model-View-ViewModel) в Android (Kotlin)**

**MVVM (Model-View-ViewModel)** – это паттерн, который **разделяет логику и UI**, позволяя сделать код **чистым, удобным для тестирования и поддерживаемым**.

Google рекомендует **MVVM** для разработки Android-приложений, особенно с использованием **Jetpack (ViewModel, LiveData, StateFlow)**.

---
# **🔹 MVC (Model-View-Controller) в Android (Kotlin)**

**MVC (Model-View-Controller)** – один из самых старых архитектурных паттернов, который разделяет код на три части:

- **Model (Модель)** → отвечает за данные и бизнес-логику.
- **View (Представление)** → отвечает за отображение данных на экране.
- **Controller (Контроллер)** → управляет связью между Model и View.

📌 **MVC не рекомендуется Google для Android**, но его можно встретить в старых проектах. В Android **Activity/Fragment часто играют роль и Controller, и View**, что делает код сложнее.

---

## **1️⃣ Компоненты MVC**
Старейший паттерн. По сути это просто хоть какое-то отделение бизнес-логики от UI. В андроид за controller - activity, а xml-layout - view. Вот когда мы быстро набрасываем примерчик это мы реализуем паттерн MVC когда пихаем модель в activity

🔽 **Принцип работы MVC:**

```
User нажал кнопку
      ↓
[View] → передает действие → [Controller]
      ↓
[Controller] → запрашивает данные → [Model]
      ↓
[Model] → возвращает данные → [Controller]
      ↓
[Controller] → обновляет View
```

### **🔹 Model (Модель)**

- Управляет бизнес-логикой приложения.
- Получает данные из **БД, API, SharedPreferences**.
- Не зависит от View и Controller.

### **🔹 View (Представление)**

- Отображает данные на экране.
- Не содержит бизнес-логики.
- В Android – это **XML-разметка + Activity/Fragment**.

### **🔹 Controller (Контроллер)**

- Обрабатывает действия пользователя.
- Вызывает Model для получения данных.
- Обновляет View при изменении Model.
- В Android чаще всего **Activity** или **Fragment** выполняют роль Controller.

---

## **2️⃣ Реализация MVC в Kotlin (Android)**

📌 **Пример: загружаем имя пользователя и отображаем его в `TextView`**.

### **🔹 1. Model (Бизнес-логика)**

```kotlin
class UserModel {
    fun getUserName(): String {
        return "John Doe" // Здесь может быть API-запрос
    }
}
```

### **🔹 2. View (activity_main.xml)**

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Имя пользователя"
        android:textSize="18sp"/>

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Загрузить имя"/>
</LinearLayout>
```

### **🔹 3. Controller (Activity)**

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var model: UserModel
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = UserModel() // Создаем модель
        textView = findViewById(R.id.textView)
        val button = findViewById<Button>(R.id.button)

        // Нажатие кнопки → загрузка данных
        button.setOnClickListener {
            val userName = model.getUserName()
            textView.text = "Имя: $userName"
        }
    }
}
```

---

## **3️⃣ Разбор кода**

✔ **Model (`UserModel`)** – хранит данные (эмуляция запроса к API).  
✔ **View (`activity_main.xml`)** – отображает данные (TextView + Button).  
✔ **Controller (`MainActivity`)** – управляет логикой, загружает данные и обновляет View.

🚨 **Проблема**:  
В Android **Activity совмещает и Controller, и View**, что делает код запутанным. Это приводит к **"God Activity"**, где весь код в одном файле.

---

## **4️⃣ Минусы MVC в Android**

❌ **Activity перегружена** – сложность в поддержке.  
❌ **Трудно тестировать** – нельзя легко заменить View/Model.  
❌ **Сильная связность** – изменения в UI могут затронуть Controller.  
❌ **Нарушение SRP (Single Responsibility Principle)** – Activity делает слишком много.

### **🆚 MVVM vs MVC**

|**Фактор**|**MVC**|**MVVM**|
|---|---|---|
|Разделение логики|❌ Слабое|✅ Чёткое|
|Тестируемость|❌ Плохая|✅ Легкая|
|Перегруженность Activity|❌ Да|✅ Нет|
|Поддержка Google|❌ Нет|✅ Да|
|Использование LiveData/Flow|❌ Нет|✅ Да|

📌 **MVC устарел для Android**. Лучше использовать **MVVM**, особенно с `ViewModel` и `LiveData`.

---

## **5️⃣ Когда использовать MVC?**

✔ **В очень простых приложениях**, где нет сложной логики.  
✔ **Если работаешь с кодом старых проектов**, где уже используется MVC.

💡 **Но для современных Android-приложений лучше использовать MVVM или MVI.** 🚀

💬 **Какой вариант архитектуры тебе нужен?** 😊
# **🔹 MVP (Model-View-Presenter) в Android (Kotlin)**

**MVP (Model-View-Presenter)** – это архитектурный паттерн, который улучшает **MVC**, делая код **более модульным, тестируемым и читаемым**. В отличие от MVC, где **Activity играет роль контроллера и View**, в **MVP View не содержит логики** – вся бизнес-логика передается в **Presenter**.

Активити имеет ссылку на презентер а презентер на активити. На самом деле они оба имеют ссылки на интерфейсы которые обязаны реализовать активити и презентер
## **1️⃣ Компоненты MVP**

### **🔹 Model (Модель)**

- Отвечает за **данные и бизнес-логику**.
    
- Получает данные из **API, БД или локального хранилища**.
    
- Не содержит информации о UI.
    

### **🔹 View (Представление)**

- Отвечает только за **отображение данных**.
    
- Передает **события пользователя** в **Presenter**.
    
- В Android — это **Activity, Fragment или View**.
    

### **🔹 Presenter (Презентер)**

- Связывает `View` и `Model`.
    
- **Не содержит ссылок на Android API (Activity, Context)**, что делает его **легко тестируемым**.
    
- Получает события от `View`, запрашивает данные у `Model`, обновляет `View`.
    

---

## **2️⃣ Как работает MVP?**

🔽 **Принцип работы:**

```
User нажал кнопку
      ↓
[View] → передает событие → [Presenter]
      ↓
[Presenter] → запрашивает данные → [Model]
      ↓
[Model] → возвращает данные → [Presenter]
      ↓
[Presenter] → обновляет View
```

🔥 **Отличие от MVC** → **View "глупая"**, вся логика вынесена в `Presenter`.

---

## **3️⃣ MVP на практике (Kotlin Android)**

📌 **Пример: загружаем имя пользователя и показываем в `TextView`**.

### **🔹 1. Model (Данные)**

```kotlin
class UserRepository {
    fun getUserName(): String {
        return "John Doe" // Здесь может быть запрос в API или БД
    }
}
```

---

### **🔹 2. View (Интерфейс)**

Создадим интерфейс `UserView`, который будет реализован в `MainActivity`.

```kotlin
interface UserView {
    fun showLoading()
    fun hideLoading()
    fun showUserName(name: String)
    fun showError(message: String)
}
```

---

### **🔹 3. Presenter (Презентер)**

```kotlin
class UserPresenter(private val view: UserView) {
    private val repository = UserRepository()

    fun loadUser() {
        view.showLoading()
        try {
            val userName = repository.getUserName()
            view.showUserName(userName)
        } catch (e: Exception) {
            view.showError("Ошибка загрузки")
        } finally {
            view.hideLoading()
        }
    }
}
```

📌 **Объяснение**:  
✔ **Presenter не зависит от Android API** (не использует Context, Activity).  
✔ Логика вынесена из View → `MainActivity` останется **максимально простой**.  
✔ `Presenter` вызывает методы View через интерфейс `UserView`.

---

### **🔹 4. View (Activity)**

```kotlin
class MainActivity : AppCompatActivity(), UserView {
    private lateinit var presenter: UserPresenter
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        progressBar = findViewById(R.id.progressBar)
        val button = findViewById<Button>(R.id.button)

        presenter = UserPresenter(this) // Передаем View в Presenter

        button.setOnClickListener {
            presenter.loadUser()
        }
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun showUserName(name: String) {
        textView.text = "Имя пользователя: $name"
    }

    override fun showError(message: String) {
        textView.text = "Ошибка: $message"
    }
}
```

---

## **4️⃣ Разбор кода**

✔ **View (`MainActivity`)** реализует `UserView`, но **не содержит бизнес-логики**.  
✔ **Presenter (`UserPresenter`)** обрабатывает логику, взаимодействует с `Model` и обновляет `View`.  
✔ **Model (`UserRepository`)** отвечает за данные.

🔥 **Вся логика вынесена в `Presenter`, что делает код легче тестируемым и более читаемым!**

---

## **5️⃣ Плюсы MVP**

✅ **View остаётся "глупой"** – только отображает данные.  
✅ **Presenter легко тестировать** – не зависит от Android API.  
✅ **Activity/Fragment не перегружены логикой**.  
✅ **Чистый и модульный код**.

---

## **6️⃣ Минусы MVP**

❌ **Много "кода-обвязки"** – интерфейсы для View, вызовы методов.  
❌ **В крупных проектах Presenter может разрастаться** → MVP требует **разбиения Presenter на слои** (например, `UseCase`).  
❌ **Может быть сложно поддерживать** при большом количестве View.

---

## **7️⃣ Когда использовать MVP?**

✔ **Если нужно легко тестировать Presenter.**  
✔ **Если View должна быть максимально простой.**  
✔ **В больших проектах, если нет Jetpack ViewModel (MVVM более удобен).**

### **🆚 MVP vs MVVM**

|**Паттерн**|**Плюсы**|**Минусы**|
|---|---|---|
|**MVP**|✅ Хорошая тестируемость|❌ Много кода, Presenter разрастается|
|**MVVM**|✅ Использует Jetpack (ViewModel, LiveData)|❌ Немного сложнее в освоении|
|**MVC**|✅ Простая реализация|❌ Activity перегружена логикой|

📌 **MVP – это хорошая альтернатива MVVM, но сейчас Google рекомендует использовать MVVM.**

💬 **Какой паттерн ты хочешь использовать? 😊**
## **1️⃣ Компоненты MVVM**

MVVM делит приложение на три части:

### **🔹 Model (Модель)**

- Отвечает за **данные и бизнес-логику**.
    
- Получает данные из **БД, API или других источников**.
    
- Не содержит информации о UI.
    

### **🔹 View (Представление)**

- Отвечает за **пользовательский интерфейс**.
    
- Подписывается на **ViewModel** и обновляет UI при изменении данных.
    
- `Activity` или `Fragment` выступает в роли View.
    

### **🔹 ViewModel**

- **Не содержит ссылки на View!**
    
- Хранит и управляет состоянием UI.
    
- Использует **LiveData/StateFlow** для передачи данных в View.
    
- Переживает **смену конфигурации (поворот экрана)**.
    

---

## **2️⃣ Как работает MVVM?**

1️⃣ `View` отправляет **событие (Intent)** → например, пользователь нажал кнопку.  
2️⃣ `ViewModel` получает событие и запрашивает данные из `Model`.  
3️⃣ `Model` возвращает данные.  
4️⃣ `ViewModel` обновляет `LiveData/StateFlow`, и `View` автоматически обновляет UI.

🔽 **Пример схемы работы MVVM:**

```
User нажал кнопку
      ↓
[View] → передает событие → [ViewModel]
      ↓
[ViewModel] → запрашивает данные → [Model]
      ↓
[Model] → возвращает данные → [ViewModel]
      ↓
[ViewModel] → обновляет LiveData/StateFlow
      ↓
[View] автоматически обновляет UI
```

---

## **3️⃣ Реализация MVVM в Kotlin (Android)**

📌 **Пример: загружаем имя пользователя и показываем в `TextView`**.

### **🔹 1. Model (данные)**

```kotlin
class UserRepository {
    fun getUserName(): String {
        return "John Doe" // Здесь может быть запрос в API или БД
    }
}
```

### **🔹 2. ViewModel (логика, обработка данных)**

```kotlin
class UserViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    fun loadUser() {
        _userName.value = repository.getUserName()
    }
}
```

📌 **Объяснение**:  
✔ **MutableLiveData** – хранит изменяемые данные.  
✔ **LiveData** – поток данных, подписка на изменения.  
✔ **ViewModel не зависит от UI**, что делает её **удобной для тестирования**.

---

### **🔹 3. View (Activity)**

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.button)

        // Инициализируем ViewModel
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Подписываемся на изменения в LiveData
        viewModel.userName.observe(this) { name ->
            textView.text = "Имя пользователя: $name"
        }

        // Нажатие кнопки → загружаем пользователя
        button.setOnClickListener {
            viewModel.loadUser()
        }
    }
}
```

📌 **Объяснение**:  
✔ `ViewModelProvider` создаёт `ViewModel`, которая сохраняется при смене конфигурации.  
✔ `observe {}` подписывается на изменения **LiveData** – UI автоматически обновляется.  
✔ **ViewModel не содержит ссылок на View!**

---

## **4️⃣ Улучшенная версия с Coroutines + Flow**

📌 **Если данные загружаются асинхронно (например, из API), лучше использовать `StateFlow` и `Coroutines`**.

🔽 **Пример обновлённого `UserViewModel` с `StateFlow`**:

```kotlin
class UserViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userName = MutableStateFlow("Загрузка...")
    val userName: StateFlow<String> = _userName

    fun loadUser() {
        viewModelScope.launch {
            delay(1000) // Имитация загрузки
            _userName.value = repository.getUserName()
        }
    }
}
```

🔽 **View с `StateFlow` (Activity)**:

```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.button)

        // Подписываемся на StateFlow
        lifecycleScope.launch {
            viewModel.userName.collect { name ->
                textView.text = "Имя пользователя: $name"
            }
        }

        button.setOnClickListener {
            viewModel.loadUser()
        }
    }
}
```

📌 **Почему `StateFlow` лучше `LiveData`?**  
✅ У `StateFlow` **нет проблем с подпиской** (сразу получает последнее значение).  
✅ Он **эффективнее LiveData** в реактивных приложениях.  
✅ Отлично работает с `Coroutines`.

---

## **5️⃣ Итог**

|**Паттерн**|**Плюсы**|**Минусы**|
|---|---|---|
|**MVVM**|✅ Чистая архитектура, ViewModel не зависит от View|❌ Нужно разбираться с ViewModel, LiveData, Coroutines|
|**MVP**|✅ Хорошая тестируемость|❌ Presenter может быть перегружен|
|**MVC**|✅ Прост в реализации|❌ "God Activity", трудно тестировать|

🚀 **MVVM – это стандарт Google для Android-приложений.**

---

## **6️⃣ Когда использовать MVVM?**

✔ **Если приложение работает с API, БД, локальным кешем**.  
✔ **Если важна реактивность и автоматическое обновление UI**.  
✔ **Если нужно удобное тестирование бизнес-логики**.

💬 **Какой вариант MVVM тебе больше подходит?** 😊

MVVM
Model-View-ViewModel
 - Model - не зависит ни от кого
 - ViewModel имеет ссылку на Model и вызывает ее методы, но ничего не знает о View. Она Она имеет публичные методы, которые могут вызываться из View(Activity или Fragment) и предоставляет LiveData на изменения в которой подписывается View. Может быть несколько liveData и публичных методов
 - View имеет ссылку на ViewModel и подписывается на изменения LiveData+вызывает публичные методы ViewModel
 MVI
 Model View Intent - один вход и один выход. 1 публичный метод который принимает на вход тип Intent от View и 1 данные на которые мы подписываемся во view.
 Model - также независимая штука
 ViewModel содержит 1 публичный метод и работает со Intent. В зависимости от Inteе вызывает нужные методы model и после изменяет состояние State, на которое подписан View. Как правило state и intent это sealed интерфейсы
 View вызывает метод viewModel и перерисовыает весь экран в зависимости от state 
 State и intent - ключевая штука в MVI. Хорошо работают с Jetpackcompose

![](<images/Pasted image 20250328094700.png>)# **🔹 MVI (Model-View-Intent) в Android (Kotlin)**

### **1️⃣ Что такое MVI?**

**MVI (Model-View-Intent)** – это паттерн с **однонаправленным потоком данных** (Unidirectional Data Flow), где **View отображает состояние**, а **ViewModel принимает Intents и обновляет состояние Model**.

💡 **Главная идея** → **View = функция от состояния** (`State`). Вместо того, чтобы изменять UI "по кусочкам", мы обновляем **всё состояние сразу**.

---

### **2️⃣ Компоненты MVI**

- **Model** – управляет данными (например, `Repository`).
- **View** – отображает `State` и отправляет `Intent`.
- **Intent** – событие пользователя (например, "Нажал кнопку").
- **State** – отображает текущее состояние UI.
- **ViewModel** – обрабатывает `Intent`, обновляет `State`.


📌 **Принцип работы MVI**:

1. **View отправляет Intent** (например, "загрузить пользователя").
2. **ViewModel обрабатывает Intent**, запрашивает данные у **Model**.
3. **Model возвращает данные**, и **ViewModel обновляет State**.
4. **View подписывается на изменения State** и обновляет UI.

### **3️⃣ Реализация MVI в Android (Kotlin)**

Используем **StateFlow** для управления состоянием.

📌 **Пример: Загружаем имя пользователя с репозитория и отображаем в UI.**

#### **📌 1. Определяем `State`**

```kotlin
sealed class UserState {
    object Loading : UserState()
    data class Success(val name: String) : UserState()
    data class Error(val message: String) : UserState()
}
```

**📌 2. Определяем `Intent`**

```kotlin
sealed class UserIntent {
    object LoadUser : UserIntent()
}
```

**📌 3. Создаем `Repository` (Model)**

```kotlin
class UserRepository {
    fun getUserName(): String {
        return "John Doe" // Здесь мог бы быть API-запрос
    }
}
```

**📌 4. Создаем `ViewModel`**

```kotlin
class UserViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _state = MutableStateFlow<UserState>(UserState.Loading)
    val state: StateFlow<UserState> = _state

    fun processIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.LoadUser -> loadUser()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                _state.value = UserState.Loading
                delay(1000) // Имитация загрузки
                val userName = repository.getUserName()
                _state.value = UserState.Success(userName)
            } catch (e: Exception) {
                _state.value = UserState.Error("Ошибка загрузки данных")
            }
        }
    }
}
```

**📌 5. Реализуем `View` (Activity)**

```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.button)

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is UserState.Loading -> textView.text = "Загрузка..."
                    is UserState.Success -> textView.text = "Имя: ${state.name}"
                    is UserState.Error -> textView.text = "Ошибка: ${state.message}"
                }
            }
        }

        button.setOnClickListener {
            viewModel.processIntent(UserIntent.LoadUser)
        }
    }
}
```

---

### **4️⃣ Разбор кода**

✔ **View (`MainActivity`)** отправляет `Intent` через `processIntent(UserIntent.LoadUser)`.  
✔ **ViewModel** обрабатывает `Intent`, обновляет `StateFlow`.  
✔ **View подписана на `state.collect {}`**, обновляя UI при изменении `State`.

---

### **5️⃣ Плюсы MVI**

✅ **Чистая архитектура** – ViewModel не зависит от View.  
✅ **Реактивность** – UI автоматически обновляется при изменении состояния.  
✅ **Легкость тестирования** – можно тестировать ViewModel отдельно.  
✅ **Логика сосредоточена в одном месте** – меньше багов, проще поддержка.

---

### **6️⃣ Когда использовать MVI?**

✔ **Когда важна реактивность** – в сложных UI с множеством состояний.  
✔ **Если приложение активно меняет состояние** (например, загрузка данных, списки, фильтры).  
✔ **Если используешь Kotlin Coroutines/Flow**.

Если приложение простое (например, форма с вводом), **MVVM будет проще**.

