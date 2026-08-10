# Вариантность (in, out)

**Вариантность** отвечает на один вопрос: если `Programmer` — наследник `Worker`, то как относятся друг к другу `Box<Programmer>` и `Box<Worker>`?

Для обычных типов всё просто: **объект потомка всегда можно присвоить переменной родителя**, приведение не нужно — потомок уже *является* родителем.
```kotlin
val worker: Worker = Programmer("John")   // ок всегда
```
А вот для `List<Programmer>` → `List<Worker>` это **не следует автоматически**. Правило надо задать явно — это и есть вариантность.

## Зачем это придумали
Чтобы дать максимум гибкости присваивания, **не сломав типобезопасность**. Если бы контейнеры были ковариантны «по умолчанию», компилировался бы такой код:
```kotlin
val programmers: MutableList<Programmer> = mutableListOf(Programmer("Nick"))
val workers: MutableList<Worker> = programmers   // допустим, разрешили
workers.add(Manager("Bob"))                      // Worker? законно!
val p: Programmer = programmers[0]               // ClassCastException в рантайме
```
Дырка появилась потому, что список умеет **и отдавать, и принимать**. Отсюда основное правило: вариантность безопасна ровно настолько, насколько ограничено направление данных.

## Три случая

### Инвариантность (по умолчанию)
Подходит только точно такой же тип: ни родители, ни потомки.
```kotlin
val list: MutableList<Worker> = mutableListOf<Programmer>()  // ОШИБКА компиляции
```
Так устроены все **изменяемые** структуры (`MutableList`, `Array`, `MutableSet`) — они и читают, и пишут, поэтому безопасен только один вариант.

### Ковариантность — `out` (producer, только отдаёт)
`Box<out T>` означает: `Box<Programmer>` **является подтипом** `Box<Worker>`. Наследование параметра «протекает» на контейнер в том же направлении.
```kotlin
interface Producer<out T> {
    fun get(): T          // T только в возвращаемом значении — ок
    // fun add(item: T)   // ОШИБКА: T в позиции параметра запрещён
}

val workers: List<Worker> = listOf<Programmer>(Programmer("Nick"))  // ок: List<out E>
```
Почему безопасно: из `List<Programmer>`, прочитанного как `List<Worker>`, можно только **достать** элемент — а любой `Programmer` и правда `Worker`. Положить туда `Manager` компилятор не даст, потому что метода записи нет.

Именно поэтому в Kotlin неизменяемый `List<out E>` ковариантен, а `MutableList<E>` — инвариантен.

**`@UnsafeVariance`** — лазейка для методов, которые технически принимают `T`, но не изменяют объект:
```kotlin
public operator fun contains(element: @UnsafeVariance E): Boolean
```
Компилятор здесь предупреждает «на вашу ответственность»: `contains` только читает, поэтому дырки не создаёт.

### Контравариантность — `in` (consumer, только принимает)
`Box<in T>` разворачивает отношение: `Box<Worker>` является подтипом `Box<Programmer>`. Потребитель, умеющий обработать любого `Worker`, тем более справится с `Programmer`.
```kotlin
interface Consumer<in T> {
    fun consume(item: T)  // T только в параметрах — ок
    // fun produce(): T    // ОШИБКА: T в позиции возврата запрещён
}

val printProgrammer: Consumer<Programmer> = object : Consumer<Worker> {
    override fun consume(item: Worker) = println(item)
}
```
Пример из stdlib — `Comparator<in T>`: компаратор по `Worker` можно передать туда, где сортируют `Programmer`.

## PECS — Producer Extends, Consumer Super
Мнемоника из Java (`? extends T` / `? super T`), для Kotlin читается как **«producer — `out`, consumer — `in`»**. Правило выбора: смотри, что параметр делает с `T`.

```kotlin
fun copy(from: MutableList<out Worker>, to: MutableList<in Worker>) {
    for (item in from) to.add(item)
}
// from — источник (producer) → out: примет MutableList<Programmer>
// to   — приёмник (consumer) → in : примет MutableList<Any>
```
Здесь `out`/`in` стоят **в месте использования** — это **use-site variance** (в Java — wildcards). Когда `out`/`in` пишут при объявлении класса (`interface List<out E>`), это **declaration-site variance** — фича Kotlin, которой в Java нет: правило задаётся один раз, а не в каждой сигнатуре.

## Итоговая таблица

| | Направление данных | Отношение подтипов | Пример |
| --- | --- | --- | --- |
| Инвариантность | и чтение, и запись | только точное совпадение | `MutableList<Worker> = MutableList<Worker>` |
| Ковариантность `out` | только читать | `Box<Programmer>` → `Box<Worker>` (как у самих типов) | `List<Worker> = List<Programmer>` |
| Контравариантность `in` | только писать | `Box<Worker>` → `Box<Programmer>` (наоборот) | `Comparator<Programmer> = Comparator<Worker>` |

Крайние точки иерархии: `out Any?` — «что угодно» (звёздная проекция `*` для чтения), `in Nothing` — «ничего нельзя положить».

## Грабли
- **`Array<T>` в Kotlin инвариантен**, а в Java массивы ковариантны — там `Object[] a = new String[1]; a[0] = 1;` компилируется и падает в рантайме (`ArrayStoreException`). Kotlin эту дырку закрыл.
- `out` не означает «immutable-класс» формально, но на практике ковариантный класс обязан быть неизменяемым: любой метод записи компилятор запретит.
- Вариантность — правило **компиляции**. В рантайме параметр стёрт, см. [[Generics. Type erasure]].

## Вопросы-ловушки
- Почему `MutableList<Programmer>` нельзя присвоить `MutableList<Worker>`? → потому что через вторую ссылку можно было бы добавить `Manager`, и чтение из первой упало бы с `ClassCastException`.
- Можно ли пометить `out T` класс, у которого есть `fun contains(x: T)`? → напрямую нет; либо менять сигнатуру, либо `@UnsafeVariance`, если метод только читает.
- Чем `List<out Worker>` отличается от `List<Worker>`? → ничем: `List` уже объявлен ковариантным, `out` в этом месте избыточен. Разница видна на `MutableList`.

![](<../../images/Pasted image 20250314151424.png>)
![](<../../images/Pasted image 20250314152037.png>)
![](<../../images/Pasted image 20250314155230.png>)

Связано: [[Generics. Basics]], [[Generics. Type erasure]], [[Generics. Inline reified and star projection]], [[Collections. Overview]]
