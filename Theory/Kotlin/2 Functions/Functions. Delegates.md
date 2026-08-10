# Делегаты (by)

**Делегирование** — передача ответственности за работу другому объекту. В Kotlin для этого есть ключевое слово `by`, и работает оно в двух разных сценариях: делегирование **свойства** (кто хранит и отдаёт значение) и делегирование **реализации интерфейса** (кто выполняет методы).

![](<../../images/Pasted image 20250319155837.png>)

## 1. Делегирование свойств
Вместо поля значение свойства обслуживает объект-делегат: он решает, что вернуть при чтении и что сделать при записи.

```kotlin
class User {
    var password: String by EncryptedProperty()
}
```
Компилятор превращает это в вызовы `getValue`/`setValue` делегата. Чтобы класс годился в делегаты, ему нужны методы с нужной сигнатурой — обычно через готовые интерфейсы `ReadOnlyProperty` (для `val`) и `ReadWriteProperty` (для `var`):

```kotlin
class EncryptedProperty : ReadWriteProperty<User, String> {
    private var encrypted: String = ""

    override fun getValue(thisRef: User, property: KProperty<*>): String =
        String(Base64.getDecoder().decode(encrypted))

    override fun setValue(thisRef: User, property: KProperty<*>, value: String) {
        encrypted = String(Base64.getEncoder().encode(value.toByteArray()))
    }
}
```
Параметры говорят сами за себя: `thisRef` — объект, которому принадлежит свойство, `property` — метаданные (`property.name` даёт имя свойства, удобно для логов и для ключей в `SharedPreferences`).

Делегат можно отдавать и функцией — так сделан сам `lazy`:
```kotlin
fun encrypted() = EncryptedProperty()
var cardNumber: String by encrypted()
```

![](<../../images/Pasted image 20250318150753.png>)

## Встроенные делегаты

### by lazy
Значение вычисляется **при первом обращении** и запоминается. Идеально для дорогих объектов, которые могут и не понадобиться.
```kotlin
val config: Config by lazy { loadConfig() }   // loadConfig() вызовется максимум один раз
```
По умолчанию режим `LazyThreadSafetyMode.SYNCHRONIZED` — внутри **double-checked locking** с `@Volatile` и блоком `synchronized`, то есть безопасно из нескольких потоков (см. [[Multithreading]]). Если свойство заведомо используется из одного потока (например, только с UI-потока), можно сэкономить на блокировке:
```kotlin
val adapter by lazy(LazyThreadSafetyMode.NONE) { MyAdapter() }
```
`lazy` работает только с `val`.

### lateinit — не делегат
Частая путаница. `lateinit var` — не делегирование, а просто отложенная инициализация без проверки на null: обращение до присваивания бросает `UninitializedPropertyAccessException`. Отличия: `lazy` для `val` и сам вычисляет значение, `lateinit` для `var` и ждёт, пока значение присвоят снаружи; `lateinit` нельзя применить к примитивам. См. [[Variables. val, var, const, lateinit, by lazy]].

### observable и vetoable
```kotlin
var name: String by Delegates.observable("") { property, old, new ->
    println("${property.name}: $old → $new")     // колбэк после изменения
}

var age: Int by Delegates.vetoable(0) { _, _, new ->
    new >= 0                                      // false — изменение отклоняется
}
```

### notNull и делегирование в Map
```kotlin
var count: Int by Delegates.notNull()      // аналог lateinit для примитивов

class Config(map: Map<String, Any?>) {
    val host: String by map                 // ключ — имя свойства
    val port: Int by map
}
```
Последнее удобно для разбора JSON-подобных структур.

## Делегаты в Android
```kotlin
private val viewModel: MyViewModel by viewModels()          // Fragment/Activity KTX
private val args: DetailsArgs by navArgs()                  // Navigation Safe Args
private val binding by viewBinding(FragmentMainBinding::bind)
```
Это обычные property-делегаты: `by viewModels()` при первом обращении достаёт ViewModel из `ViewModelProvider`, а не создаёт новую при каждом чтении.

## 2. Делегирование реализации интерфейса
Когда класс обязан реализовать интерфейс, но фактически перекладывает работу на другой объект, `by` избавляет от простыней методов-переадресаций.

```kotlin
interface Player {
    val userName: String
    fun run()
    fun fight()
}

class FlyingPlayer(private val player: Player) : Player by player {
    // run() и fight() уже делегированы — писать их не нужно
    fun fly() = println("$userName летит")
}
```
Это композиция вместо наследования: расширяем поведение, не завися от реализации базового класса.

**Важная тонкость:** делегирование фиксируется на этапе компиляции. Если переопределить метод в наследнике, делегат об этом **не узнает** — внутренние вызовы из `player` пойдут по его собственной реализации:
```kotlin
class FlyingPlayer(private val player: Player) : Player by player {
    override fun run() = println("бежит по-своему")
    // но если player.fight() внутри себя вызывает run(), вызовется run() у player, а не наш
}
```

## Грабли
- **`by lazy` на свойстве, зависящем от жизненного цикла** (например, `binding` во фрагменте): значение закэшируется от первого View и переживёт `onDestroyView` — утечка и краш при повторном показе.
- Делегат создаётся **на каждый экземпляр** класса; тяжёлый делегат на часто создаваемом объекте — лишняя нагрузка.
- `by lazy` внутри `object`/синглтона удерживает вычисленное значение до конца процесса.
- Свойство с делегатом нельзя пометить `const`, и у него нет backing field — `field` внутри недоступен.
- Каждое делегированное свойство добавляет объект делегата и вызовы `getValue` — на горячем пути это заметно.

## Вопросы-ловушки
- Чем `by lazy` отличается от `lateinit`? → `lazy` — `val`, вычисляет сам, потокобезопасен по умолчанию; `lateinit` — `var`, значение присваивают снаружи, не работает с примитивами.
- Как реализован `lazy`? → делегат с double-checked locking: `@Volatile`-поле + `synchronized` при первом обращении.
- Что нужно классу, чтобы стать делегатом свойства? → методы `getValue` (и `setValue` для `var`) с параметрами `thisRef` и `KProperty`; проще — реализовать `ReadOnlyProperty`/`ReadWriteProperty`.
- Что произойдёт при переопределении делегированного метода интерфейса? → внешние вызовы пойдут в переопределение, но внутренние вызовы делегата останутся его собственными.
- Зачем `LazyThreadSafetyMode.NONE`? → убрать синхронизацию, когда доступ гарантированно однопоточный (UI-поток), — быстрее.

Связано: [[Variables. val, var, const, lateinit, by lazy]], [[Classes. Backing field]], [[Classes. Setter getter]], [[Multithreading]], [[GoF. Structural. Decorator]]
