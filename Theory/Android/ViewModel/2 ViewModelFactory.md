Нельзя создавать просто через конструктор, потому что не переживет переворот экрана и уничтожится. Нужно через viewModelProvioder
По этой же причине нельзя передавать контекст в конструктор. Нужно передавать application
```kotlin
class GameViewModel(val application: Application) : AndroidViewModel(application)

private val viewModel by lazy {  
    ViewModelProvider(  
        this,  ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)  
    )[GameViewModel::class.java]  
}
```
Но как передать тогда параметры? Чтобы не вызывать  какой-то метод а сразу при создании viewModel в init делать какие-то действия? Через viewModel factory

```kotlin
//viewModel с параметром
class GameViewModel(val application: Application, private val level: Level) : AndroidViewModel(application)

// фабрика для создания viewModel с параметром
class GameViewModelFactory(private val application: Application, private val level: Level) :  
    ViewModelProvider.Factory {  
    override fun <T : ViewModel> create(modelClass: Class<T>): T { 
    // проверка, что мы именно класс GameViewModel создаем с помощью нашей фабрики, а не какой-то левый 
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {  
            return GameViewModel(application, level) as T  
        }  
        throw RuntimeException("Unknown ViewModel class $modelClass ")  
    }  
}

// создание viewModel во фрагменте
class GameFragment : Fragment() {
// параметр который передаем
	private lateinit var level: Level
// создание фабрики  
    private val viewModelFactory by lazy {  
        GameViewModelFactory(requireActivity().application, level)  
    }  
// создание модели с использованием нашей фабрики    
    private val viewModel by lazy {  
        ViewModelProvider(  
            this,  
            viewModelFactory  
        )[GameViewModel::class.java]  
    }

```