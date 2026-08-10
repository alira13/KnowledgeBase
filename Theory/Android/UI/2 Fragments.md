## Зачем создали фрагменты
- **Модульность и переиспользование**: `Fragment` = самодостаточный кусок экрана (макет + ViewModel + логика), который можно вставить в разные экраны без дублирования кода.
- На одном экране можно разместить **несколько контейнеров** с разными фрагментами (например, список + деталь на планшете), а на телефоне — тот же фрагмент отдельным экраном. При повороте/на большом экране легко показать два макета там, где на телефоне был один.
- В одну `Activity` **нельзя вложить другую** `Activity`, но можно сколько угодно фрагментов → фрагменты стали основой подхода **Single Activity**. См. [[Navigation. BackStack]].

## Жизненный цикл (кратко)
ЖЗ
onAttach(onDetach) прикрепление фрагмента к activit. Передается контекст. С этого момента у фрагмента есть ссылка на активити. GetContext-GetActivity!=null
onCreate(onDestroy) - создается фрагмент. Передаются ему параметры
onCreateView(onDestroyView) - создается view из макета
onViewCreated() - с этого момента можно работать с view элементами
![](<../../images/Pasted image 20250328113120.png>)

ViewModel следует удалять на фазе `onDestroy()` фрагмента, когда фрагмент окончательно уничтожается. Однако, если вы используете `ViewModel` с фрагментом, важно понимать, что `ViewModel` не уничтожается автоматически при уничтожении фрагмента, если фрагмент не полностью уничтожен (например, при изменении конфигурации).

## Жизненный цикл Fragment

Жизненный цикл Fragment в Android включает в себя несколько фаз, которые меняются в зависимости от действий пользователя и состояния Activity, к которой он прикреплен. Вот основные фазы жизненного цикла Fragment:

1. **`onAttach()`**: Вызывается первым, когда Fragment прикрепляется к Activity. В этом методе Fragment получает ссылку на Activity, к которой он прикреплен.
    
2. **`onCreate()`**: Вызывается после `onAttach()`, когда Fragment создается. Здесь можно инициализировать данные и сохраненное состояние.
    
3. **`onCreateView()`**: Вызывается, когда Fragment должен создать свой интерфейс. Возвращает корневой View, который будет отображен в Activity.
    
4. **`onViewCreated()`**: Вызывается после `onCreateView()`, когда View уже создан. Используется для настройки View.
    
5. **`onActivityCreated()`**: Вызывается, когда Activity завершила создание своего интерфейса. Этот метод был удален в AndroidX Fragment 1.3.0 и больше не рекомендуется к использованию.
    
6. **`onStart()`**: Вызывается, когда Fragment становится видимым для пользователя.
    
7. **`onResume()`**: Вызывается, когда Fragment становится активным и пользователь может с ним взаимодействовать.
    
8. **`onPause()`**: Вызывается, когда Fragment больше не активен, но все еще видим.
    
9. **`onStop()`**: Вызывается, когда Fragment больше не видим.
    
10. **`onDestroyView()`**: Вызывается, когда View Fragment уничтожается.
    
11. **`onDestroy()`**: Вызывается, когда Fragment уничтожается и освобождает ресурсы.
    
12. **`onDetach()`**: Вызывается последним, когда Fragment отсоединяется от Activity.
    

## Изменения фаз в зависимости от действий пользователя

- **Запуск Activity с Fragment**: Fragment проходит через `onAttach()`, `onCreate()`, `onCreateView()`, `onViewCreated()`, `onStart()`, `onResume()`.
    
- **Поворот экрана**: Fragment уничтожается и пересоздается, проходя через все фазы снова.
    
- **Переход к другой Activity**: Fragment проходит через `onPause()`, `onStop()`, `onDestroyView()`, `onDestroy()`, `onDetach()`.
    
- **Возвращение к предыдущей Activity**: Fragment пересоздается и проходит через все фазы снова.
    

Эти фазы жизненного цикла позволяют Fragment корректно обрабатывать изменения состояния и взаимодействовать с пользователем.


`Fragment` — это, по сути, экран (набор разных `View`)  
  
Создание fragments

1. Подключить библиотеку
```Kotlin
dependencies {
	def fragment_version = "1.5.5"
	implementation "androidx.fragment:fragment-ktx:$fragment_version"
}
```
2. Унаследовать `Activity` от `AppCompatActivity` для использования фрагментов в SingleActivity
3. `activity_main.xml` добавить контейнер для фрагмента. Мы должны помещать View в какую-то ViewGroup. Для этого нужно создать контейнер для Fragment в визуальной части (XML). В качестве контейнера для Fragment можно взять FrameLayout, но лучше использовать FragmentContainerView — наследник FrameLayout.  
    FragmentContainerView изначально адаптирован для работы с Fragment.  
    Провернём все эти действия в  
    `activity_main.xml`:

```Kotlin
<?xml version="1.0" encoding="utf-8"?>
<androidx.fragment.app.FragmentContainerView
xmlns:android="http://schemas.android.com/apk/res/android"
android:id="@+id/fragment_container_view"
android:layout_width="match_parent"
android:layout_height="match_parent"/>
```

1. Создаем сам фрагмент  
    1. Сначала создадим XML-файл c  
    `TextView`. Вот как выглядит вёрстка в layout `fragment_cities.xml`:

```Kotlin
<?xml version="1.0" encoding="utf-8"?>
<TextView
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:tools="http://schemas.android.com/tools"
android:id="@+id/textView"
android:layout_width="match_parent"
android:layout_height="wrap_content"
tools:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
```

1. Напишем класс `Fragment`, затем в нём зададим список наших городов в `TextView`. Работаем в файле `CitiesFragment.kt`:

_//наш класс должен наследоваться от класса Fragment_

```Kotlin
class CitiesFragment : Fragment() {

    private val cities = "Yurevichi,Gumist’a,Ptitsefabrika,Orekhovo,Birim,Priiskovyy"
    *// используем ViewBinding, мы можем использовать его так же как и в Activity*

		private var _binding: FragmentCitiesBinding? = null

*// создаём неизменяемую переменную, к которой можно будет обращаться без ?. Мы должны не забыть инициализировать _binding, до того как использовать*

		private val binding get() = _binding!!

*// в момент вызова onCreateView создаётся View для Fragment, поэтому именно в этот момент мы инициализируем binding и настраиваем View-элементы*

		override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitiesBinding.inflate(inflater, container, false)

        binding.textView.text = cities
        return binding.root
    }
}
```

1. И теперь самый интересный момент — показать наш `Fragment` в `Activity`.  
    Так как мы знаем, что навигацию между экранами удобнее выстраивать с использованием 'Fragment', нам нужно уметь динамично в коде задавать  
    `Fragment` для лёгкой смены `Fragment`. В этом нам помогут два класса: `FragmentManager` и `FragmentTransaction`. Рассмотрим каждый.

**FragmentManager**

В отличие от `Activity`, которой управляет система, для управления `Fragment` существует специальный класс — `FragmentManager`.

Класс `FragmentManager` отвечает за выполнение таких операций с фрагментами, как добавление, удаление и замена.

Важная функция `FragmentManager` — управление `Back Stack`. Вы уже знаете про `Back Stack` у `Activity`, вот и для `Fragment` есть такая сущность. Нужна она ровно для того же — запоминать пройденный путь пользователя и возвращать его обратно. Например, если пользователь захочет вернуться на предыдущий экран и нажмёт на аппаратную кнопку Back, `FragmentManager` вернёт к предыдущему фрагменту в этой последовательности.

**FragmentTransaction**

Класс `FragmentTransaction` и есть операция (добавление, удаление или замена). Также мы можем сгруппировать несколько операций в один `FragmentTransaction`. Например, если хотим добавить сразу несколько `Fragment` на экран. Можно поставить один `Fragment` отвечать за верхнюю часть экрана, а второй — за нижнюю.  
  

В базовом классе `AppCompatActivity` есть метод, возвращающий `FragmentManager`, — `getSupportFragmentManager()`.

```Kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
// в этот момент мы отображаем Fragment
            supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, CitiesFragment())
            .commit()
        }
    }

}
```

Получается, чтобы отобразить `Fragment`:

- Вызываем метод `beginTransaction()` у `FragmentManager`.
- Метод `beginTransaction()` возвращает `FragmentTransaction`.
- У экземпляра `FragmentTransaction` вызываем метод `add`, передаём туда контейнер и сам `Fragment`.
- Затем вызываем метод `commit()`, который и осуществляет переход на новый `Fragment`.

Можно также использовать лямбда-выражения для добавления `Fragment`. Нагляднее выглядит последовательность вызовов в примере выше, но на практике лучше смотрятся лямбда-выражения. Так выглядит изменённый код `MainActivity`:

```Kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
// в этот момент мы отображаем Fragment
            supportFragmentManager.commit {
                add<CitiesFragment>(R.id.fragment_container_view)
            }
        }
    }
}
```

Обратите внимание, что транзакция фрагмента создаётся только тогда, когда `savedInstanceState == null`. Это делается для того, чтобы фрагмент добавлялся только один раз, когда `Activity` создаётся впервые. Когда происходит изменение конфигурации и активность пересоздаётся, `savedInstanceState != null` и `Fragment` не нужно добавлять во второй раз, так как фрагмент автоматически восстанавливается из `savedInstanceState`.

**Переключение Fragment**

Мы можем создать экземпляр FragmentTransaction с помощью FragmentManager, а у экземпляра FragmentTransaction вызвать метод add(), чтобы поместить Fragment в контейнер.  
Помимо add(), у FragmentTransaction есть метод replace(), который удаляет существующий Fragment и добавляет новый в контейнер. Если просто вызвать replace() вместо add(), то при нажатии кнопки «Назад» мы не вернёмся к прошлому Fragment, а выйдем из приложения.  
Чтобы вернуться на предыдущий Fragment, нужно сохранить его в Back Stack нашего FragmentManager. Для этого у FragmentTransaction нужно вызвать метод addToBackStack().  

Итого `Back Stack` — это список `Fragment`, которым управляет `FragmentManager`. Помимо добавления в `Back Stack`, мы можем убрать из `Back Stack` последний `Fragment` — `popBackStack()`, и получится `supportFragmentManager.popBackStack()`.

![](<../../images/Pasted image 20241209173615.png>)Для доступа к родительской activity(к той, к которой прикреплен фрагмен) можно использовать методы:
 - getActivity(activity?) - возвращает null-объект и мы можем вставить проверку, что если не null, тогда выполни действие activity?.onBackPressed и приложение не упадет
 - requireActivity() возвращает либо activity либо исключение. Поэтому если activity=null, то наше приложение упадет с исключением. То есть если мы его не обработаем, приложение упадет
 Аналогичны методы
 getContext - requireContext
 getView - requireView
 Я везде в приложениях использовала require, чтобы на ручном тестинге увидеть падающее приложение и исключение, иначе я войду в проверку на null, она сработает, действие не совершится и я не пойму, почему это действие не сработало

Чтобы добавить фрагмент в activity:
1. Нужно создать фрагмент. Если есть параметры, то нужно создать статический фабричный метод для их передачи во фрагмент
```
companion object {  
    @JvmStatic  
    fun newInstance(param1: String, param2: String) =  
        PrepareFragment().apply {  
            arguments = Bundle().apply {  
                putString(ARG_APP_NAME, param1)  
                putString(ARG_ACTION_NAME, param2)  
            }  
        }}
```
2. вся работа с фрагментами идет через supportFragmentManager внутри транзакций,
 - открываем транзакцию
 - добавляем фрагмент(ссылка на родительский контейнер activity, ссылка на созданный п1. фрагмент)
 - стартуем транзакцию
```
val fragment =  
    PrepareFragment.newInstance("PrepareFragment", "launch")  
supportFragmentManager  
    .beginTransaction()
    .add(R.id.main_fragment_container, fragment)  
    .commit()
```
![](<../../images/Pasted image 20241212135855.png>)


По типу перемещений всю навигацию можно разделить на три большие группы:

- Перемещение из приложения **наружу** (в другие приложения).
Пример такой навигации — переход по ссылке из приложения в приложение браузера.
- Перемещения **снаружи** **внутрь** приложения.
- И перемещения **внутри** приложения.


Вопросы и ответы
![](<../../images/Pasted image 20241216121620.png>)![](<../../images/Pasted image 20241216121643.png>)![](<../../images/Pasted image 20241216121720.png>)![](<../../images/Pasted image 20241216121807.png>)![](<../../images/Pasted image 20241216121845.png>)![](<../../images/Pasted image 20241216123059.png>)![](<../../images/Pasted image 20241216123459.png>)![](<../../images/Pasted image 20241216123646.png>)![](<../../images/Pasted image 20241216123930.png>)![](<../../images/Pasted image 20241216124153.png>)![](<../../images/Pasted image 20241216124245.png>)https://swiftbook.org/pages/1450/