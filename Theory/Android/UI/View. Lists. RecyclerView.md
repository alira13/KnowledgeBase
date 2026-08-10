# RecyclerView

## Способы отображения списков
- **ScrollView + LinearLayout** — создаём `View` для **каждого** элемента через `inflate` и складываем в контейнер. Работает, пока элементов десяток: на длинном списке это тормоза и перерасход памяти, потому что живут все View сразу.
- **Spinner** — выпадающий список для небольшого набора значений.
- **ListView**, **GridView** — устарели, заменены `RecyclerView`.
- **RecyclerView** — стандарт для списков.
- **LazyColumn/LazyRow** — эквивалент в Compose с той же идеей переиспользования.

> **`LayoutInflater`** — класс, создающий `View` из layout-файла методом **`inflate`**.
> Параметры: **`resource`** — id layout-файла; **`root`** — родительский `ViewGroup`, от которого берутся `LayoutParams`; **`attachToRoot`** — присоединять ли созданный View к root. В адаптере всегда передают `false`: добавит его сам RecyclerView.

## Идея RecyclerView
Создавать View только для **видимых** элементов (плюс небольшой запас за краем экрана) и **переиспользовать** их: элемент ушёл вверх — его View не выбрасывается, а получает данные следующего элемента. Отсюда название: список перерабатывает (recycle) свои View.

Именно поэтому список из 10 000 строк требует ~15 View, а не 10 000.

## Составные части
| Компонент | Роль |
| --- | --- |
| **RecyclerView** | сам `ViewGroup`, добавляется в разметку как обычный компонент |
| **ViewHolder** | держит ссылки на View одного элемента, чтобы не звать `findViewById` при каждой привязке |
| **Adapter** | связывает данные с их представлением, создаёт и наполняет ViewHolder'ы |
| **LayoutManager** | как расположены элементы: `LinearLayoutManager`, `GridLayoutManager`, `StaggeredGridLayoutManager` |
| **ItemDecoration** | разделители, отступы, фоны между элементами |
| **ItemAnimator** | анимации добавления, удаления, перемещения |
| **DiffUtil** | вычисляет разницу между старым и новым списком |
| **RecycledViewPool** | хранилище свободных ViewHolder'ов, можно делить между списками |

Обрати внимание: **ViewHolder — это паттерн, придуманный ради производительности**. В `ListView` его приходилось писать руками, в `RecyclerView` он обязателен по API.

## Базовый адаптер
```kotlin
class UserAdapter : ListAdapter<User, UserAdapter.Holder>(UserDiffCallback) {

    class Holder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.name.text = user.name
        }
    }

    // вызывается редко — только когда свободного холдера нет
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    // вызывается часто — при каждом появлении элемента на экране
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}
```
Разделение важно: `onCreateViewHolder` — дорогой `inflate`, происходит несколько раз за жизнь списка; `onBindViewHolder` — вызывается на каждом скролле, поэтому в нём **нельзя** ходить в сеть, в БД или создавать объекты.

```kotlin
recyclerView.layoutManager = LinearLayoutManager(context)
recyclerView.adapter = adapter
adapter.submitList(users)
```

## DiffUtil — главное про обновления
`notifyDataSetChanged()` перерисовывает **весь** список: теряется позиция скролла, ломаются анимации, лишняя работа. `DiffUtil` вместо этого вычисляет минимальный набор изменений (алгоритм Майерса) и вызывает точечные `notifyItemInserted/Removed/Changed`.

```kotlin
object UserDiffCallback : DiffUtil.ItemCallback<User>() {
    // тот же самый элемент? — сравнение по идентификатору
    override fun areItemsTheSame(old: User, new: User) = old.id == new.id

    // содержимое не изменилось? — вызывается только если areItemsTheSame вернул true
    override fun areContentsTheSame(old: User, new: User) = old == new

    // что именно изменилось — для частичного обновления
    override fun getChangePayload(old: User, new: User): Any? =
        if (old.name != new.name) PAYLOAD_NAME else null
}
```
Два метода легко перепутать — на этом строится частый вопрос:
- `areItemsTheSame` — «это одна и та же сущность?» (сравнивай **id**);
- `areContentsTheSame` — «нужно ли перерисовать?» (сравнивай **содержимое**, для `data class` это `==`).

Если в `areItemsTheSame` сравнить объекты целиком, DiffUtil решит, что элемент удалён и вставлен новый: вместо плавного обновления получишь мигание и прыжок скролла.

**`ListAdapter`** — готовый адаптер с DiffUtil внутри: `submitList()` сам считает разницу **в фоновом потоке** и применяет обновления. Для длинных списков это обязательный вариант, ручной `DiffUtil.calculateDiff()` на главном потоке подвесит UI.

### Payload — частичное обновление
Когда изменилось одно поле (счётчик лайков), перепривязывать весь элемент незачем:
```kotlin
override fun onBindViewHolder(holder: Holder, position: Int, payloads: MutableList<Any>) {
    if (payloads.isEmpty()) {
        super.onBindViewHolder(holder, position, payloads)   // полная привязка
    } else {
        holder.bindLikes(getItem(position))                  // только изменившееся
    }
}
```

## Несколько типов элементов
```kotlin
override fun getItemViewType(position: Int): Int = when (getItem(position)) {
    is Item.Header -> TYPE_HEADER
    is Item.Content -> TYPE_CONTENT
}
```
`viewType` приходит в `onCreateViewHolder` — там и выбирают разметку. Холдеры разных типов хранятся в пуле раздельно.

## Производительность
- **`ListAdapter`/DiffUtil** вместо `notifyDataSetChanged()`.
- **Стабильные id** (`setHasStableIds(true)` + `getItemId()`) — помогают анимациям и переиспользованию.
- **`setHasFixedSize(true)`**, если размер списка не меняется от содержимого: RecyclerView пропустит лишние перерасчёты.
- **Плоская разметка элемента**: вложенные `LinearLayout` — самая частая причина тормозов; `ConstraintLayout` в один уровень быстрее.
- **Не создавать объекты в `onBindViewHolder`** (форматтеры, слушатели, `SimpleDateFormat`) — выносить в поля или в UI-модель.
- **Общий `RecycledViewPool`** для вложенных списков одинакового типа.
- Изображения — через Coil/Glide с отменой загрузки при переиспользовании холдера.
- **Не вкладывать RecyclerView в ScrollView** — переиспользование выключается, создаются все элементы сразу. Нужен один скролл — используй `ConcatAdapter` или разные `viewType`.

## Грабли
- **Состояние в ViewHolder**: холдер переиспользуется, поэтому всё нужно задавать в `bind` целиком. Забыл сбросить видимость/цвет — «поедут» чужие данные при скролле.
- Слушатель, вешаемый в `onBindViewHolder`, создаётся заново на каждой привязке — лучше ставить его в `onCreateViewHolder` и брать `bindingAdapterPosition`.
- `adapterPosition` может быть `NO_POSITION` (−1) во время анимации — проверяй перед использованием.
- Изменение списка **на месте** (`list.add(...)` в том же экземпляре) ломает DiffUtil: он сравнит список сам с собой и не увидит изменений. Всегда передавай **новый** список.

## Вопросы-ловушки
- В чём разница между `onCreateViewHolder` и `onBindViewHolder` и какой вызывается чаще? → создание дорогое и редкое, привязка дешёвая и частая.
- Чем `areItemsTheSame` отличается от `areContentsTheSame`? → идентичность сущности (по id) против совпадения содержимого.
- Почему после `submitList` ничего не обновилось? → передан тот же экземпляр списка, изменённый на месте.
- Как обновить один элемент, не перерисовывая его целиком? → `getChangePayload` + перегрузка `onBindViewHolder` с `payloads`.
- Что не так с RecyclerView внутри ScrollView? → отключается переиспособление View, теряется весь смысл компонента.
- Чем `ListView` хуже? → нет обязательного ViewHolder, нет LayoutManager, нет встроенных анимаций и DiffUtil.

![](<../../images/Pasted image 20241120164415.png>)
![](<../../images/Pasted image 20241121125945.png>)
![](<../../images/Pasted image 20241121130132.png>)
![](<../../images/Pasted image 20241121130256.png>)
![](<../../images/Pasted image 20241121130542.png>)
![](<../../images/Pasted image 20241121131113.png>)
![](<../../images/Pasted image 20241121131311.png>)

Связано: [[View]], [[View. UI rendering stages]], [[2 Fragments]], [[Performance. Profiling and UI optimization]], [[1 View Binding]]
