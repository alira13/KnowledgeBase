Раньше findeViewById
Теперь ViewBinding от Google
Смысл в том, что для каждого макета создается собственный класс, где будут храниться ссылки на view элементы.

**View Binding** — это механизм в Android, который упрощает работу с элементами пользовательского интерфейса, минимизируя ошибки, связанные с неверным использованием идентификаторов представлений. Он генерирует классы, связывающие XML-элементы с соответствующими объектами `View`, что позволяет избежать использования метода `findViewById()`.
## Основные преимущества:

1. **Null Safety и Type Safety**: View Binding гарантирует, что ссылки на элементы интерфейса будут корректными и безопасными, что снижает риск ошибок во время выполнения.
2. **Упрощение кода**: Нет необходимости вручную искать элементы по идентификаторам, что делает код более лаконичным и читаемым.
3. **Отсутствие необходимости в ButterKnife или Kotlin Android Extensions**: View Binding является рекомендуемым подходом для доступа к элементам интерфейса вместо устаревших методов
4. **Type Safety**: View Binding гарантирует, что типы элементов интерфейса будут соответствовать их XML-описанию.
- **Null Safety**: Если элемента нет в разметке, поле в binding-классе будет помечено как nullable.
- **Лучшая производительность**: Поиск элементов происходит только один раз при вызове `inflate()`, что делает его более эффективным, чем `findViewById()`.

## Использование View Binding:

1. В файле `build.gradle` модуля добавить:
`android {buildFeatures {viewBinding = true}}`
 После включения, для каждого файла разметки (например, `activity_main.xml`) будет сгенерирован соответствующий класс (например, `ActivityMainBinding`)
2. В MainActivity создать ссылку на объект binding
3. В onCreate проинициализировать
4. Вернуть binding.root   
5. Для фрагментов: создать _binding и присвоить null в onDestroyView
```kotlin
package com.example.jetpackcompose.game.presentation  
  
class WelcomeFragment : Fragment() {  
    //1 Cоздать ссылку на объект binding null
    private var _binding: FragmentWelcomeBinding? = null 
    //1 И чтобы  каждый раз не работать с null-объектом
    private val binding: FragmentWelcomeBinding  
        get() = _binding ?: throw RuntimeException("FragmentWelcomeBinding == null")  
  
    override fun onCreateView(  
        inflater: LayoutInflater,  
        container: ViewGroup?,  
        savedInstanceState: Bundle?  
    ): View {  
        // 2 Инициализация bimding создает класс на основе макета и создает там ссылки на все view  
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)  
        // 3 теперь нужно вернуть view  
        //return inflater.inflate(R.layout.fragment_welcome, container, false)        return binding.root  
    }  
  
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {  
        super.onViewCreated(view, savedInstanceState)  
        // 4 Использовать Binding  
        val button = binding.btnAccept  
    }  
  
    // Удаляем ссылку на view чтобы если мы в каких-то методах обратились к view где она недоступна, сразу была ошибка  
    override fun onDestroyView() {  
        super.onDestroyView()  
        _binding = null  
    }  
}
```