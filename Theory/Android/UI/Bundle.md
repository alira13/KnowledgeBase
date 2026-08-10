# Bundle

**Bundle** — контейнер «ключ → значение», через который Android передаёт данные между компонентами и сохраняет состояние экрана. По сути это типизированная `Map<String, Any>`, но с важным ограничением: класть в неё можно только то, что система умеет **сериализовать в `Parcel`**.

Почему нельзя просто передать ссылку на объект: получатель может оказаться **в другом процессе** (или в том же, но после смерти текущего). Данные должны быть представимы как плоский набор байт — отсюда и Parcel, и ограничение по типам, и лимит размера. См. [[IPC. How two apps communicate]].

## Что можно положить
- примитивы и их массивы (`putInt`, `putBoolean`, `putIntArray`);
- `String`, `CharSequence`, их списки;
- объекты, реализующие **`Parcelable`** (предпочтительно) или **`Serializable`**;
- вложенный `Bundle`.

```kotlin
val args = bundleOf(
    "num" to 10,
    "name" to "John",
    "user" to user            // user: Parcelable
)
val num = args.getInt("num")          // 0, если ключа нет
val name = args.getString("name")     // String? — nullable
```
Геттеры возвращают значение по умолчанию (`0`, `false`, `null`), если ключа нет, — молча, без исключения. Отсюда классический баг «опечатка в ключе → всегда ноль». Ключи выносят в `const val`.

## Три сценария использования

### 1. Аргументы фрагмента
```kotlin
class DetailsFragment : Fragment() {
    companion object {
        private const val ARG_ID = "id"
        fun newInstance(id: Long) = DetailsFragment().apply {
            arguments = bundleOf(ARG_ID to id)
        }
    }

    private val id: Long get() = requireArguments().getLong(ARG_ID)
}
```
**Почему не через конструктор?** Система пересоздаёт фрагмент сама, вызывая **пустой конструктор**, — всё, что ты передал в свой, потеряется при повороте или process death. `arguments` же система сохранит и вернёт. Это же причина, по которой фрагмент обязан иметь публичный конструктор без аргументов.

### 2. Передача между Activity (через Intent)
```kotlin
startActivity(Intent(this, DetailsActivity::class.java).apply {
    putExtra("id", 42L)          // extras — это Bundle внутри Intent
})
// в DetailsActivity:
val id = intent.getLongExtra("id", -1L)
```
См. [[0 App components. Intent]].

### 3. Сохранение состояния
```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(KEY_QUERY, query)
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    query = savedInstanceState?.getString(KEY_QUERY).orEmpty()
}
```
Это единственный механизм, переживающий **смерть процесса**. В современном коде то же самое делают через `SavedStateHandle` во ViewModel — под капотом всё тот же Bundle. См. [[1 Activity]], [[1 ViewModel, ViewModelProvider]].

## Лимит размера
Bundle уходит через **Binder**, а у binder-транзакции жёсткий лимит **~1 МБ на все активные транзакции процесса** — не на один вызов. Превышение → `TransactionTooLargeException`.

Для `onSaveInstanceState` практический потолок ниже: с Android 7.0 система падает примерно на **500 КБ**, а рекомендация Google — держаться в пределах **50 КБ**.

Правило: в Bundle кладут **идентификаторы, а не данные**. Не список товаров, а `productId`; не битмап, а путь к файлу. Сами данные — Room, DataStore, файл, кэш. Подробнее — [[Intent and Bundle data size limits]].

## Bundle vs другие способы хранения
| | Что это | Когда |
| --- | --- | --- |
| `Bundle` | передача и восстановление состояния экрана | аргументы, `savedInstanceState` |
| `SharedPreferences`/DataStore | долговременное хранение настроек | переживает перезапуск приложения |
| `Intent` | описание намерения + `extras` (тот же Bundle) | запуск компонентов |
| `ViewModel` | состояние экрана в памяти | переживает поворот, но не смерть процесса |

## Грабли
- **Опечатка в ключе** не вызывает ошибку — вернётся значение по умолчанию.
- `getSerializable`/`getParcelable` без типа устарели с API 33; используй `getParcelable(key, User::class.java)` или `BundleCompat`.
- Класть большие `Bitmap`/`ByteArray` — самый частый способ поймать `TransactionTooLargeException`, причём краш прилетит не там, где положили, а в момент транзакции.
- `Bundle` **ленив**: данные распаковываются при первом обращении, поэтому исключение о неизвестном классе всплывает не при получении, а при `get*`.
- Собственные классы в Bundle должны быть доступны загрузчику классов получателя — передавать свои объекты в чужое приложение нельзя.

## Вопросы-ловушки
- Почему аргументы фрагмента передают через `Bundle`, а не через конструктор? → система пересоздаёт фрагмент пустым конструктором; переживут только `arguments`.
- Сколько данных влезает в Bundle? → лимит Binder ~1 МБ на процесс, для `savedInstanceState` — до ~500 КБ, рекомендуется ≤50 КБ.
- Почему в Bundle нельзя положить любой объект? → он должен переживать переход между процессами, поэтому нужен `Parcelable`/`Serializable`.
- Чем `Bundle` отличается от `Map`? → фиксированный набор допустимых типов, сериализация в `Parcel`, типизированные геттеры со значениями по умолчанию.

Связано: [[0 Serialization. Serializable vs Parcelable]], [[Intent and Bundle data size limits]], [[1 Activity]], [[2 Fragments]], [[1 ViewModel, ViewModelProvider]], [[0 App components. Intent]]
