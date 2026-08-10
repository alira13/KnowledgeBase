

**Activity** — по сути, это экран, который видит пользователь. Именно здесь происходит взаимодействие с пользователем: показываем ему информацию, обрабатываем ввод и т.п. В активити у нас располагаются всякие элементы: кнопки, фрагменты, изображения и другие различные View. Из-за всего этого, объем памяти, потребляемый активити, может существенно увеличиваться.

Когда пользователь перемещается между экранами, экземпляры активити образуют стек. Положение в стеке определяет состояние каждого экземпляра. Состояние может быть:
- Активный — активити на переднем плане, которая полностью видна.
- Приостановлена — активити отображается частично.
- Остановлена — активити не видна. Например, пользователь перешел на другой экран.
- Неактивна — активити удалена.

![](<images/Pasted image 20250328110926.png>)
Это состояние определяет системный приоритет приложения, а это влияет напрямую на возможность завершения приложения и на планирование выполнения потоков.

Жизненный цикл активити завершается, когда пользователь возвращается к предыдущему экрану или когда активити вызывает метод finish().

![](<images/android-lifecycle-diagram-03.png>)

В Android разработке на Kotlin связь между `Activity` и XML-файлом верстки устанавливается с помощью метода `setContentView()`. Этот метод задает конкретный XML-файл верстки, который будет использоваться для отображения пользовательского интерфейса в данной `Activity`. Вот как это работает:

### 1. XML-файл верстки

XML-файл содержит описание пользовательского интерфейса. Например, файл `activity_main.xml` может выглядеть так:

```xml
<!-- res/layout/activity_main.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello, World!" />

</LinearLayout>
```

### 2. Связывание XML с Activity

Класс `Activity` управляет этим интерфейсом. В методе `onCreate()` нужно указать, какой XML-файл использовать:

```kotlin
// MainActivity.kt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Установка XML-файла в качестве разметки Activity
        setContentView(R.layout.activity_main)
    }
}
```

### 3. Объяснение ключевых моментов

- `setContentView(R.layout.activity_main)` — связывает XML-файл `activity_main.xml` с текущей `Activity`. Этот файл находится в папке `res/layout`.
- `R.layout.activity_main` — это ссылка на ресурс XML, сгенерированная автоматически в классе `R` (ресурсы приложения).
- После вызова `setContentView()`, элементы интерфейса, определенные в XML, становятся доступными для взаимодействия с помощью их ID.

### 4. Доступ к элементам из XML

Чтобы работать с элементами, вы можете использовать их ID, который указан в XML:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Доступ к TextView из XML
    val textView = findViewById<TextView>(R.id.textView)
    textView.text = "Привет, Android!"
}
```

### 5. View Binding (альтернатива)

Современный способ связывать Activity и XML — использование View Binding. Это безопаснее и удобнее:

В `build.gradle` включите View Binding:

```gradle
android {
    viewBinding {
        enabled = true
    }
}
```

Теперь в Activity можно использовать автоматически сгенерированный класс:

```kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Доступ к элементам напрямую через binding
        binding.textView.text = "Привет, View Binding!"
    }
}
```

Это делает код более читаемым и уменьшает вероятность ошибок.

### Почему фрагменты не являются основными компонентами Android

Фрагменты не являются основными компонентами Android, потому что они не являются самостоятельными единицами, которые могут существовать вне контекста **активити**. Основными компонентами Android считаются:

- **Активити (Activity)**: Представляют собой отдельные экраны приложения.
- **Службы (Service)**: Выполняют фоновые задачи без пользовательского интерфейса.
- **Бродкаст-ресиверы (Broadcast Receiver)**: Обрабатывают широковещательные сообщения.
- **Провайдеры контента (Content Provider)**: Управляют доступом к данным между приложениями.

Фрагменты же являются **модульными компонентами**, которые могут быть использованы внутри активити для создания более гибкого и адаптивного интерфейса. Они имеют свой собственный жизненный цикл, но он тесно связан с жизненным циклом активити-хоста[2](https://metanit.com/java/android/8.1.php)[4](https://proglike.ru/directory/android/fragmenty)[5](https://developer.alexanderklimov.ru/android/fragment.php).

## Причины, по которым фрагменты не являются основными компонентами:

1. **Зависимость от активити**: Фрагменты не могут существовать самостоятельно и должны быть встроены в активити.
2. **Отсутствие регистрации в манифесте**: В отличие от основных компонентов, фрагменты не регистрируются в манифесте приложения.
3. **Модульность и повторное использование**: Фрагменты предназначены для повторного использования внутри разных активити, что делает их вспомогательными компонентами.

Таким образом, хотя фрагменты являются важной частью приложений Android, они не считаются основными компонентами из-за своей зависимости от активити и отсутствия самостоятельности.

Фазы ЖЗ при действиях пользователя
1. **Запуск Activity**:
    - **onCreate()**: Вызывается при первом создании Activity.
    - **onStart()**: Activity становится видимой для пользователя.
    - **onResume()**: Activity получает фокус и становится интерактивной.
2. **Поворот экрана**:
    - **onPause()**: Activity временно приостанавливается.
    - **onSaveInstanceState()**: Сохраняется текущее состояние Activity.
    - **onStop()**: Activity становится невидимой.
    - **onDestroy()**: Activity уничтожается.
        
    - **onCreate()**: Activity пересоздается с новой конфигурацией.
    - **onStart()**: Activity становится видимой.
    - **onResume()**: Activity снова становится интерактивной.
        
3. **Переход к другой Activity**:
    
    - **onPause()**: Текущая Activity приостанавливается.
    - **onStop()**: Текущая Activity становится невидимой.
        
    - **onCreate()**: Новая Activity создается.
    - **onStart()**: Новая Activity становится видимой.
    - **onResume()**: Новая Activity получает фокус.

4. **Нажатие кнопки "Назад"**:
    
    - **onPause()**: Activity приостанавливается.
    - **onStop()**: Activity становится невидимой.
    - **onDestroy()**: Activity уничтожается.
        
5. **Нажатие кнопки "Домой"**:
    - **onPause()**: Activity приостанавливается.
    - **onStop()**: Activity становится невидимой, но не уничтожается.
        
6. **Возвращение к Activity после нажатия кнопки "Домой"**:
    
    - **onStart()**: Activity становится видимой.
    - **onResume()**: Activity снова становится интерактивной.

### Сохранение состояния приложения
передача параметров между Activity и фрагментами. Bundle