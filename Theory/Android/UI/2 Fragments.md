# Fragments

## Зачем создали фрагменты
- **Модульность и переиспользование**: `Fragment` = самодостаточный кусок экрана (макет + ViewModel + логика), который можно вставить в разные экраны без дублирования кода.
- На одном экране можно разместить **несколько контейнеров** с разными фрагментами (например, список + деталь на планшете), а на телефоне — тот же фрагмент отдельным экраном.
- В одну `Activity` **нельзя вложить другую** `Activity`, но можно сколько угодно фрагментов → фрагменты стали основой подхода **Single Activity**. См. [[Navigation. BackStack]].

Важно: фрагмент — **не** основной компонент Android. Он не регистрируется в манифесте, его не запускает система, он всегда живёт внутри Activity. См. [[1 Activity]].

## Жизненный цикл
- `onAttach` / `onDetach` — прикрепление к Activity. С этого момента у фрагмента есть контекст: `getContext()` и `getActivity()` не `null`.
- `onCreate` / `onDestroy` — создаётся сам фрагмент, доступны `arguments`.
- `onCreateView` / `onDestroyView` — создаётся и уничтожается **View** фрагмента.
- `onViewCreated` — с этого момента можно работать с элементами разметки.
- `onStart` / `onStop`, `onResume` / `onPause` — как у Activity, следуют за её состоянием.

`onActivityCreated()` **удалён** в AndroidX Fragment 1.3.0 — если встретишь в старом коде, замена: `onViewCreated` или `onCreate`.

![](<../../images/Pasted image 20250328113120.png>)

### Последовательности
- **Запуск**: `onAttach` → `onCreate` → `onCreateView` → `onViewCreated` → `onStart` → `onResume`.
- **Уход с экрана (replace с addToBackStack)**: `onPause` → `onStop` → `onDestroyView`. **`onDestroy` не вызывается** — фрагмент жив в стеке.
- **Возврат из стека**: `onCreateView` → `onViewCreated` → `onStart` → `onResume` — новая View у старого объекта фрагмента.
- **Поворот экрана**: полный цикл уничтожения и создания вместе с Activity.
- **Закрытие экрана**: `onDestroyView` → `onDestroy` → `onDetach`.

## Два жизненных цикла — главное о фрагментах
У фрагмента их **два, и они разной длины**:
1. Жизненный цикл **самого фрагмента**: `onCreate` → `onDestroy`.
2. Жизненный цикл его **View**: `onCreateView` → `onDestroyView`.

Фрагмент переживает свою View — и может пережить несколько раз подряд. Отсюда два следствия, которые спрашивают почти всегда:

**1. Обнулять binding в `onDestroyView`** — иначе фрагмент в стеке держит ссылку на уничтоженную иерархию View, это утечка памяти.

**2. Подписываться на `viewLifecycleOwner`, а не на `this`:**
```kotlin
viewModel.state.observe(viewLifecycleOwner) { render(it) }

viewLifecycleOwner.lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.state.collect { render(it) }
    }
}
```
С `this` подписка живёт столько же, сколько фрагмент: при каждом возврате из стека добавляется ещё одна, и обработчик срабатывает по нескольку раз на одно событие.

`ViewModel` через `by viewModels()` привязана к `ViewModelStore` фрагмента и очищается в `onDestroy` (не в `onDestroyView`), поэтому переживает и поворот, и возврат из стека. Общее с Activity состояние — `by activityViewModels()`. См. [[1 ViewModel, ViewModelProvider]].

## Создание фрагмента
```kotlin
dependencies {
    implementation("androidx.fragment:fragment-ktx:1.6.2")
}
```
Контейнер в разметке Activity — `FragmentContainerView` (наследник `FrameLayout`, адаптированный под фрагменты):
```xml
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

Сам фрагмент:
```kotlin
class CitiesFragment : Fragment() {

    private var _binding: FragmentCitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = cities        // работа с View — здесь
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null                       // обязательно! иначе утечка
    }

    companion object {
        fun newInstance(param: String) = CitiesFragment().apply {
            arguments = bundleOf(ARG_NAME to param)
        }
    }
}
```
Параметры передаются **только через `arguments`**, а не через конструктор: система пересоздаёт фрагмент пустым конструктором, и всё, что передали иначе, потеряется. См. [[Bundle]].

## FragmentManager и транзакции
Фрагментами управляет не система, а **`FragmentManager`**: добавление, удаление, замена и собственный **back stack**.

**`FragmentTransaction`** — одна операция или группа операций, применяемых атомарно.
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {                 // важно!
            supportFragmentManager.commit {
                add<CitiesFragment>(R.id.fragment_container_view)
            }
        }
    }
}
```
Проверка `savedInstanceState == null` обязательна: при пересоздании Activity фрагменты восстанавливаются автоматически, и без неё в контейнер добавится второй экземпляр.

### add vs replace
- **`add`** — новый фрагмент кладётся поверх, View предыдущего **остаётся** в дереве (его `onDestroyView` не вызывается). Удобно, когда нужно сохранить состояние экрана под ним.
- **`replace`** — предыдущий фрагмент удаляется из контейнера: `onPause` → `onStop` → `onDestroyView`.

Без `addToBackStack(null)` кнопка «Назад» после `replace` не вернёт на предыдущий фрагмент, а закроет приложение. С `addToBackStack` `FragmentManager` запомнит транзакцию и откатит её; вручную это делается через `popBackStack()`.

### Варианты commit
| Метод | Поведение |
| --- | --- |
| `commit()` | асинхронно, в ближайшем цикле главного потока |
| `commitNow()` | синхронно; несовместим с `addToBackStack` |
| `commitAllowingStateLoss()` | не падает после `onSaveInstanceState`, но изменение может потеряться |

Классическая ошибка — `IllegalStateException: Can not perform this action after onSaveInstanceState`: транзакция запущена после сохранения состояния (например, из колбэка сети, пришедшего когда экран уже свернули). Правильное решение — не выполнять транзакцию в этот момент (`lifecycleScope` + `repeatOnLifecycle`), а не глушить `commitAllowingStateLoss`.

## Доступ к Activity и контексту
- `getActivity()` / `getContext()` / `getView()` — возвращают `null`, если фрагмент не прикреплён.
- `requireActivity()` / `requireContext()` / `requireView()` — бросают исключение вместо `null`.

`require*` предпочтительнее: падение сразу показывает реальную ошибку, тогда как молчаливая проверка на `null` просто не выполнит действие, и причину придётся искать долго.

## Общение между фрагментами
Напрямую фрагменты друг о друге знать не должны.
| Задача | Решение |
| --- | --- |
| передать данные при открытии | `arguments` / Safe Args |
| вернуть результат назад | **Fragment Result API** (`setFragmentResult` / `setFragmentResultListener`) |
| общее состояние на экране | `by activityViewModels()` |
| события в реальном времени | `SharedFlow` в общей ViewModel |

## Грабли
- **Не обнулён `_binding`** в `onDestroyView` — утечка иерархии View.
- **Подписка на `this` вместо `viewLifecycleOwner`** — дублирующиеся колбэки.
- **Конструктор с параметрами** — после пересоздания фрагмент останется без данных (лечится `arguments` или `FragmentFactory`).
- **Вложенные фрагменты**: используй `childFragmentManager`, иначе иерархия сломается при пересоздании.
- **Транзакция после `onSaveInstanceState`** — `IllegalStateException`.
- **`FrameLayout` вместо `FragmentContainerView`** — теряется часть корректной обработки вставок и восстановления.

## Вопросы-ловушки
- Сколько жизненных циклов у фрагмента? → два: фрагмента и его View; View может пересоздаваться, пока фрагмент жив.
- Почему параметры передают через `arguments`, а не в конструктор? → система пересоздаёт фрагмент пустым конструктором.
- Чем `add` отличается от `replace` с точки зрения жизненного цикла? → при `add` у нижнего фрагмента View сохраняется, при `replace` — уничтожается.
- Что вызовется при возврате из back stack? → `onCreateView` → `onViewCreated` → … , но не `onCreate`: объект фрагмента тот же.
- Когда очищается ViewModel фрагмента? → в `onDestroy` фрагмента, а не при уничтожении View.
- Почему нельзя просто использовать `commitAllowingStateLoss`? → он прячет проблему: транзакция может молча потеряться.

![](<../../images/Pasted image 20241209173615.png>)
![](<../../images/Pasted image 20241212135855.png>)

Связано: [[1 Activity]], [[Navigation. BackStack]], [[1 ViewModel, ViewModelProvider]], [[Bundle]], [[1 View Binding]], [[Memory leaks. Detection]]
