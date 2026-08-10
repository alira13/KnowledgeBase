
**Паттерн "Итератор"** – это поведенческий паттерн проектирования, который предоставляет **способ последовательного перебора элементов коллекции** без раскрытия ее внутренней структуры.

В **Kotlin** есть два типа итераторов:

- `Iterator<T>` – **однонаправленный** итератор (`next()`)
- `ListIterator<T>` – **двунаправленный** (`next()` и `previous()`)

Создаем свой
1. Помечаем объект Iterable. То есть говорим, что как-то хотим его элементы перебирать в цикле. Это интерфейс по сути обязывает в методе вернуть Iterator
2. Реализуем свой Custom Iterator, который реализует интерфейс Iterator. hasNext и next-функции. В цикле происходит проверка, если hasNext, тогда вызываем next.

Для чего нужен
 - для собственной логики перебора элементов, можем сначала отсортировать коллекцию, можем выводить только четные элементы, можем добавить доп инфу допустим для логирования, но это. Можно создать обертку для каких-то данных, чтобы каким-то образом по этим данным проходить.
 - для ускорения логики перебора элементов
 - для того, чтобы любой класс можно было использовать в цикле для перебора его элементов. Даже когда класс использует не список внутри, а просто набор входных переменных, мы из них сможем сделать список.


!!! В момент обхода коллеции всегда происходит проверка, не изменил ли кто-то коллекцию. Если изменил -> exception Iterable
Но есть еще интерфейс MutableIterable, который позволяет удалять элемент коллекции, по которой идет перебор, но только после того как этот элемент уже был перебран.
Добавляется метод remove по сравнению с Iterable(используется очень редко)

В **Kotlin** он реализован встроенным интерфейсом `Iterator<T>`.



---

## **1. Зачем нужен паттерн "Итератор"?**

🔹 Упрощает перебор элементов коллекции  
🔹 Абстрагирует детали реализации коллекции  
🔹 Позволяет обходить коллекцию **разными способами**

---

## **2. Использование встроенного `Iterator` в Kotlin**

Все стандартные коллекции (`List`, `Set`, `Map`) уже имеют встроенный итератор. Например:

```kotlin
val list = listOf("Apple", "Banana", "Cherry")
val iterator = list.iterator()

while (iterator.hasNext()) {
    println(iterator.next()) // Выводит Apple, Banana, Cherry
}
```

✔ **`hasNext()`** – проверяет, есть ли еще элементы  
✔ **`next()`** – возвращает следующий элемент

---

## **3. Создание собственного итератора**

Допустим, у нас есть **кастомная коллекция** (`BookShelf`), и мы хотим перебрать книги с помощью итератора.

### **Шаг 1: Создаем класс `Book`**

```kotlin
data class Book(val title: String)
```

### **Шаг 2: Создаем коллекцию `BookShelf`**

```kotlin
class BookShelf(private val books: List<Book>) : Iterable<Book> {
    override fun iterator(): Iterator<Book> {
        return BookShelfIterator(books)
    }
}
```

✔ **Реализуем `Iterable<T>`** – это позволяет использовать `forEach`.

### **Шаг 3: Создаем класс `BookShelfIterator`**

```kotlin
class BookShelfIterator(private val books: List<Book>) : Iterator<Book> {
    private var index = 0

    override fun hasNext(): Boolean {
        return index < books.size
    }

    override fun next(): Book {
        if (!hasNext()) throw NoSuchElementException()
        return books[index++]
    }
}
```

✔ **Запоминаем текущий индекс (`index`)**  
✔ **Перемещаемся вперед при вызове `next()`**

### **Шаг 4: Используем наш итератор**

```kotlin
val shelf = BookShelf(listOf(Book("Kotlin"), Book("Design Patterns"), Book("Clean Code")))

for (book in shelf) {
    println(book.title)
}
```

**Вывод:**

```
Kotlin
Design Patterns
Clean Code
```

✔ **Работает с `for` без явного вызова `iterator()`**

---

## **4. Альтернативный вариант с `operator fun next()`**

Можно улучшить код с `operator fun next()`:

```kotlin
class BookShelfIterator(private val books: List<Book>) : Iterator<Book> {
    private var index = 0

    override fun hasNext() = index < books.size

    override operator fun next() = books[index++] // Теперь можно использовать `operator fun`
}
```

✔ **Теперь `next()` выглядит более идиоматично.**

---

## **5. Итератор с `yield` (Sequence)**

Kotlin поддерживает **ленивые итераторы** с `Sequence` и `yield`.

### **Пример генератора с `yield`**

```kotlin
fun bookSequence(books: List<Book>): Sequence<Book> = sequence {
    for (book in books) {
        yield(book) // Возвращает элементы по одному
    }
}

val books = listOf(Book("Kotlin"), Book("Java"), Book("Python"))
for (book in bookSequence(books)) {
    println(book.title)
}
```

✔ **Lazy (ленивый) обход элементов** – `yield()` возвращает элементы **по требованию**, экономя память.

---

## **Вывод**

|Подход|Преимущества|Когда использовать?|
|---|---|---|
|**Стандартный `Iterator`**|Встроен в Kotlin, удобен для стандартных коллекций|При работе с `List`, `Set`, `Map`|
|**Свой итератор**|Гибкость, поддержка кастомных коллекций|Если коллекция не стандартная|
|**Sequence + `yield`**|Ленивые вычисления, экономия памяти|Если коллекция **очень большая**|

Если вам нужно просто **перебирать элементы** → используйте `forEach`.  
Если нужна **кастомная логика обхода** → создайте свой итератор.  
Если коллекция **огромная и должна работать лениво** → используйте `Sequence` + `yield`.

Вот список **вопросов и ответов**, которые могут задать на собеседовании про **паттерн "Итератор"** в Kotlin.

---
### Вопросы собеседования
### **1. Что такое паттерн "Итератор"?**

📌 **Ответ:**  
**Итератор** – это поведенческий паттерн, который предоставляет способ последовательного перебора элементов коллекции **без раскрытия ее внутренней структуры**.  
В **Kotlin** он реализуется через `Iterator<T>` и `Iterable<T>`.

**Пример встроенного итератора:**

```kotlin
val list = listOf("A", "B", "C")
val iterator = list.iterator()
while (iterator.hasNext()) {
    println(iterator.next())
}
```

---

### **2. Какие методы должен реализовать итератор?**

📌 **Ответ:**  
Итератор должен реализовать два метода:

- `hasNext(): Boolean` – проверяет, есть ли еще элементы
- `next(): T` – возвращает следующий элемент

Пример:

```kotlin
class MyIterator(private val list: List<String>) : Iterator<String> {
    private var index = 0

    override fun hasNext() = index < list.size

    override fun next() = list[index++]
}
```

---

### **3. Как создать свой итератор в Kotlin?**

📌 **Ответ:**  
Нужно создать класс, реализующий `Iterator<T>`, и затем передать его в коллекцию, реализующую `Iterable<T>`.

Пример **кастомного итератора** для `BookShelf`:

```kotlin
class BookShelf(private val books: List<String>) : Iterable<String> {
    override fun iterator(): Iterator<String> {
        return BookShelfIterator(books)
    }
}

class BookShelfIterator(private val books: List<String>) : Iterator<String> {
    private var index = 0
    override fun hasNext() = index < books.size
    override fun next() = books[index++]
}
```

Использование:

```kotlin
val shelf = BookShelf(listOf("Kotlin", "Java", "Python"))
for (book in shelf) {
    println(book)
}
```

---

### **4. Чем `Iterator` отличается от `ListIterator`?**

📌 **Ответ:**  
В **Kotlin** есть два типа итераторов:

- `Iterator<T>` – **однонаправленный** итератор (`next()`)
- `ListIterator<T>` – **двунаправленный** (`next()` и `previous()`)

Пример `ListIterator`:

```kotlin
val list = listOf("A", "B", "C")
val iterator = list.listIterator()

while (iterator.hasNext()) {
    println(iterator.next()) // A, B, C
}

while (iterator.hasPrevious()) {
    println(iterator.previous()) // C, B, A
}
```

📌 **Использовать `ListIterator`, если нужно двигаться в обе стороны.**

---

### **5. В чем преимущества паттерна "Итератор"?**

📌 **Ответ:**  
✅ **Инкапсуляция** – скрывает детали реализации коллекции  
✅ **Универсальность** – можно использовать один итератор для разных структур данных  
✅ **Гибкость** – можно создать **разные** итераторы для одной коллекции

---

### **6. Можно ли создать ленивый итератор в Kotlin?**

📌 **Ответ:**  
Да! Используйте **Sequence** и `yield` для ленивой генерации элементов.

Пример **ленивого итератора**:

```kotlin
fun bookSequence(books: List<String>): Sequence<String> = sequence {
    for (book in books) {
        yield(book) // Возвращает элементы по запросу
    }
}

val books = listOf("Kotlin", "Java", "Python")
for (book in bookSequence(books)) {
    println(book)
}
```

✔ `Sequence` экономит память, так как элементы создаются **по мере необходимости**.

---

### **7. Какие аналоги итератора есть в Kotlin?**

📌 **Ответ:**  
✅ **`forEach {}`** – удобнее, чем `iterator()`, если не нужна гибкость  
✅ **`Sequence`** – для ленивой обработки данных  
✅ **`ListIterator`** – если нужен **двусторонний** итератор  
✅ **`map {}`, `filter {}`** – для функционального перебора элементов

---

### **8. Как сделать "обратный итератор" в Kotlin?**

📌 **Ответ:**  
Можно использовать `reversed()` или `ListIterator`:

```kotlin
val list = listOf("A", "B", "C").reversed()
for (item in list) {
    println(item) // C, B, A
}
```

Либо вручную реализовать `Iterator`:

```kotlin
class ReverseIterator<T>(private val list: List<T>) : Iterator<T> {
    private var index = list.size - 1
    override fun hasNext() = index >= 0
    override fun next() = list[index--]
}
```

---

### **9. Когда лучше использовать итератор вместо `forEach`?**

📌 **Ответ:**  
🔹 Если **нужно контролировать** итерацию (например, пропускать элементы)  
🔹 Если **коллекция кастомная** и `forEach` не работает  
🔹 Если **используется `ListIterator`** (движение вперед-назад)

---

### **10. В чем разница между паттернами "Итератор" и "ФорEach"?**

📌 **Ответ:**

|Параметр|Итератор (`Iterator`)|`forEach`|
|---|---|---|
|**Гибкость**|✅ Можно пропускать, прерывать|❌ Нельзя остановить|
|**Контроль**|✅ Полный контроль|❌ Автоматический перебор|
|**Поддержка `ListIterator`**|✅ Да|❌ Нет|
|**Сложность**|❌ Требует больше кода|✅ Короткий код|

Пример `forEach`:

```kotlin
listOf(1, 2, 3).forEach { println(it) }
```

✔ Если нужна **гибкость**, выбирайте **`Iterator`**.

---

## **Вывод**

✅ **Паттерн "Итератор"** полезен, когда:

- **Нужен кастомный порядок обхода коллекции**
- **Нужно перемещаться назад (`ListIterator`)**
- **Хотим скрыть внутреннюю структуру коллекции**

Если на собеседовании попросят **реализовать итератор**, лучше показать **кастомный `Iterator<T>`** или `Sequence` для ленивых вычислений. 🚀

Нужны **конкретные примеры кода** под ваш случай? 😊



### Реализация **`MutableIterable`** и **`MutableListIterator`** для **двунаправленного списка** в Kotlin.  
С его помощью можно **перебирать, добавлять, удалять и изменять** элементы прямо во время итерации.

---

## **1. Реализация двусвязного списка (`DoublyLinkedList`)**

```kotlin
class DoublyLinkedList<T> : MutableIterable<T> {
    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var size = 0

    class Node<T>(var value: T, var prev: Node<T>? = null, var next: Node<T>? = null)

    fun add(value: T) {
        val newNode = Node(value, tail, null)
        if (tail != null) {
            tail!!.next = newNode
        } else {
            head = newNode
        }
        tail = newNode
        size++
    }

    override fun iterator(): MutableListIterator<T> = DoublyLinkedListIterator(this)

    fun size(): Int = size
    fun getHead(): Node<T>? = head
    fun getTail(): Node<T>? = tail

    fun removeNode(node: Node<T>) {
        if (node.prev != null) {
            node.prev!!.next = node.next
        } else {
            head = node.next
        }
        if (node.next != null) {
            node.next!!.prev = node.prev
        } else {
            tail = node.prev
        }
        size--
    }
}
```

✔ Двусвязный список с **методом удаления узла (`removeNode()`)**  
✔ Метод `add(value: T)` **добавляет элементы в конец**

---

## **2. Реализация `MutableListIterator`**

Добавляем **изменяемый итератор** с поддержкой `remove()`, `add()` и `set()`.

```kotlin
class DoublyLinkedListIterator<T>(private val list: DoublyLinkedList<T>) : MutableListIterator<T> {
    private var current: DoublyLinkedList.Node<T>? = null
    private var lastReturned: DoublyLinkedList.Node<T>? = null
    private var index = 0

    init {
        current = list.getHead()
    }

    override fun hasNext(): Boolean = current != null

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        lastReturned = current
        val value = current!!.value
        current = current!!.next
        index++
        return value
    }

    override fun hasPrevious(): Boolean = lastReturned != null

    override fun previous(): T {
        if (!hasPrevious()) throw NoSuchElementException()
        current = lastReturned
        val value = current!!.value
        lastReturned = current!!.prev
        index--
        return value
    }

    override fun nextIndex(): Int = index
    override fun previousIndex(): Int = index - 1

    override fun remove() {
        lastReturned?.let {
            list.removeNode(it)
            lastReturned = null
        } ?: throw IllegalStateException("Call next() or previous() before remove()")
    }

    override fun set(element: T) {
        lastReturned?.let {
            it.value = element
        } ?: throw IllegalStateException("Call next() or previous() before set()")
    }

    override fun add(element: T) {
        val newNode = DoublyLinkedList.Node(element, lastReturned, lastReturned?.next)
        lastReturned?.next?.prev = newNode
        lastReturned?.next = newNode
        lastReturned = newNode
    }
}
```

✔ **Удаление элементов во время итерации (`remove()`)**  
✔ **Изменение текущего элемента (`set()`)**  
✔ **Добавление нового элемента (`add()`)**

---

## **3. Тестируем `MutableIterable`**

```kotlin
fun main() {
    val list = DoublyLinkedList<String>()
    list.add("A")
    list.add("B")
    list.add("C")
    list.add("D")

    val iterator = list.iterator()

    println("Удаляем элемент B и меняем C → X:")
    while (iterator.hasNext()) {
        val value = iterator.next()
        if (value == "B") iterator.remove()
        if (value == "C") iterator.set("X")
    }

    println("Добавляем элемент Y после X:")
    iterator.add("Y")

    // Проверяем результат
    val newIterator = list.iterator()
    while (newIterator.hasNext()) {
        println(newIterator.next()) // A, X, Y, D
    }
}
```

---

## **Вывод**

|Метод|Описание|
|---|---|
|`hasNext()`|Проверяет, есть ли следующий элемент|
|`next()`|Возвращает следующий элемент|
|`hasPrevious()`|Проверяет, можно ли вернуться назад|
|`previous()`|Возвращает предыдущий элемент|
|`nextIndex()`|Возвращает индекс следующего элемента|
|`previousIndex()`|Возвращает индекс предыдущего элемента|
|`remove()`|Удаляет последний возвращенный элемент|
|`set(value)`|Изменяет последний возвращенный элемент|
|`add(value)`|Добавляет элемент после последнего возвращенного|

✔ **Полноценная поддержка `MutableListIterator`**  
✔ **Можно удалять, изменять и добавлять элементы в процессе перебора**


### Вот реализация **`MutableIterable`** и **`MutableIterator`** для **однонаправленного списка (Single Linked List)** в Kotlin.  
С его помощью можно **перебирать, удалять и изменять** элементы списка во время итерации.

---

## **1. Реализация однонаправленного списка (`SinglyLinkedList`)**

```kotlin
class SinglyLinkedList<T> : MutableIterable<T> {
    private var head: Node<T>? = null
    private var size = 0

    class Node<T>(var value: T, var next: Node<T>? = null)

    fun add(value: T) {
        if (head == null) {
            head = Node(value)
        } else {
            var current = head
            while (current!!.next != null) {
                current = current.next
            }
            current.next = Node(value)
        }
        size++
    }

    override fun iterator(): MutableIterator<T> = SinglyLinkedListIterator(this)

    fun removeNode(prev: Node<T>?, node: Node<T>) {
        if (prev == null) {
            head = node.next // Удаляем голову
        } else {
            prev.next = node.next // Пропускаем узел
        }
        size--
    }

    fun getHead(): Node<T>? = head
}
```

✔ **Односвязный список с удалением узлов**  
✔ Метод `add(value: T)` **добавляет элементы в конец**

---

## **2. Реализация `MutableIterator`**

Добавляем **изменяемый итератор** с поддержкой `remove()` и `set()`.

```kotlin
class SinglyLinkedListIterator<T>(private val list: SinglyLinkedList<T>) : MutableIterator<T> {
    private var current: SinglyLinkedList.Node<T>? = list.getHead()
    private var prev: SinglyLinkedList.Node<T>? = null
    private var lastReturned: SinglyLinkedList.Node<T>? = null

    override fun hasNext(): Boolean = current != null

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        lastReturned = current
        prev = if (lastReturned == list.getHead()) null else prev?.next
        current = current!!.next
        return lastReturned!!.value
    }

    override fun remove() {
        lastReturned?.let {
            list.removeNode(prev, it)
            lastReturned = null
        } ?: throw IllegalStateException("Call next() before remove()")
    }

    fun set(element: T) {
        lastReturned?.let {
            it.value = element
        } ?: throw IllegalStateException("Call next() before set()")
    }
}
```

✔ **Удаление элемента во время итерации (`remove()`)**  
✔ **Изменение текущего элемента (`set()`)**  
❌ **Метода `add()` нет, так как `SinglyLinkedList` не поддерживает вставку в середину**

---

## **3. Тестируем `MutableIterable`**

```kotlin
fun main() {
    val list = SinglyLinkedList<String>()
    list.add("A")
    list.add("B")
    list.add("C")
    list.add("D")

    val iterator = list.iterator()

    println("Удаляем элемент B и меняем C → X:")
    while (iterator.hasNext()) {
        val value = iterator.next()
        if (value == "B") iterator.remove()
        if (value == "C") (iterator as SinglyLinkedListIterator).set("X")
    }

    // Проверяем результат
    val newIterator = list.iterator()
    while (newIterator.hasNext()) {
        println(newIterator.next()) // A, X, D
    }
}
```

---

## **Вывод**

|Метод|Описание|
|---|---|
|`hasNext()`|Проверяет, есть ли следующий элемент|
|`next()`|Возвращает следующий элемент|
|`remove()`|Удаляет последний возвращенный элемент|
|`set(value)`|Изменяет последний возвращенный элемент|

✔ **Полноценная поддержка `MutableIterator`**  
✔ **Можно удалять и изменять элементы в процессе перебора**  
❌ **Нет `add()`, потому что сложно вставлять в середину односвязного списка**

Хочешь добавить поддержку вставки (`add()`) или оставить так? 😊


### **`MutableIterable`** и **`MutableListIterator`** для **динамического массива (ArrayList)** в Kotlin.  
С его помощью можно **перебирать, добавлять, удалять и изменять** элементы прямо во время итерации.

---

## **1. Реализация динамического массива (`DynamicArray`)**

```kotlin
class DynamicArray<T> : MutableIterable<T> {
    private var array = arrayOfNulls<Any>(10) // Начальный размер 10
    private var size = 0

    fun add(value: T) {
        if (size == array.size) {
            array = array.copyOf(array.size * 2) // Увеличиваем размер в 2 раза
        }
        array[size++] = value
    }

    fun removeAt(index: Int) {
        if (index !in 0 until size) throw IndexOutOfBoundsException()
        for (i in index until size - 1) {
            array[i] = array[i + 1]
        }
        array[size - 1] = null
        size--
    }

    fun set(index: Int, value: T) {
        if (index !in 0 until size) throw IndexOutOfBoundsException()
        array[index] = value
    }

    fun get(index: Int): T {
        if (index !in 0 until size) throw IndexOutOfBoundsException()
        @Suppress("UNCHECKED_CAST")
        return array[index] as T
    }

    fun size(): Int = size

    override fun iterator(): MutableListIterator<T> = DynamicArrayIterator(this)
}
```

✔ **Автоматически расширяет массив**  
✔ **Методы `add()`, `removeAt()`, `set()`, `get()`**  
✔ **Быстрое обращение по индексу (O(1))**

---

## **2. Реализация `MutableListIterator`**

```kotlin
class DynamicArrayIterator<T>(private val list: DynamicArray<T>) : MutableListIterator<T> {
    private var index = 0
    private var lastReturnedIndex = -1

    override fun hasNext(): Boolean = index < list.size()

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        lastReturnedIndex = index
        return list.get(index++)
    }

    override fun hasPrevious(): Boolean = index > 0

    override fun previous(): T {
        if (!hasPrevious()) throw NoSuchElementException()
        lastReturnedIndex = --index
        return list.get(index)
    }

    override fun nextIndex(): Int = index
    override fun previousIndex(): Int = index - 1

    override fun remove() {
        if (lastReturnedIndex == -1) throw IllegalStateException("Call next() or previous() before remove()")
        list.removeAt(lastReturnedIndex)
        index = lastReturnedIndex
        lastReturnedIndex = -1
    }

    override fun set(element: T) {
        if (lastReturnedIndex == -1) throw IllegalStateException("Call next() or previous() before set()")
        list.set(lastReturnedIndex, element)
    }

    override fun add(element: T) {
        list.add(element)
    }
}
```

✔ **Полноценный `MutableListIterator`**  
✔ **Можно добавлять (`add()`), удалять (`remove()`), изменять (`set()`)**  
✔ **Поддерживает движение вперед (`next()`) и назад (`previous()`)**

---

## **3. Тестируем `MutableIterable`**

```kotlin
fun main() {
    val list = DynamicArray<String>()
    list.add("A")
    list.add("B")
    list.add("C")
    list.add("D")

    val iterator = list.iterator()

    println("Удаляем элемент B и меняем C → X:")
    while (iterator.hasNext()) {
        val value = iterator.next()
        if (value == "B") iterator.remove()
        if (value == "C") iterator.set("X")
    }

    println("Добавляем элемент Y:")
    iterator.add("Y")

    // Проверяем результат
    val newIterator = list.iterator()
    while (newIterator.hasNext()) {
        println(newIterator.next()) // A, X, D, Y
    }
}
```

---

## **Вывод**

|Метод|Описание|
|---|---|
|`hasNext()`|Проверяет, есть ли следующий элемент|
|`next()`|Возвращает следующий элемент|
|`hasPrevious()`|Проверяет, можно ли вернуться назад|
|`previous()`|Возвращает предыдущий элемент|
|`nextIndex()`|Возвращает индекс следующего элемента|
|`previousIndex()`|Возвращает индекс предыдущего элемента|
|`remove()`|Удаляет последний возвращенный элемент|
|`set(value)`|Изменяет последний возвращенный элемент|
|`add(value)`|Добавляет элемент в конец списка|

✔ **Полноценная поддержка `MutableListIterator`**  
✔ **Можно удалять, изменять и добавлять элементы в процессе перебора**

### Реализация **`MutableIterable`** для `HashSet` в Kotlin

В `HashSet` элементы **неупорядочены**, и в нем **нет индексов**. Это означает, что мы можем **перебирать**, **удалять** и **изменять** элементы, но **не можем добавить элемент на конкретное место**.

---

## **1. Реализация `MutableHashSet`**

```kotlin
class MutableHashSet<T> : MutableIterable<T> {
    private val map = HashMap<T, Boolean>() // Используем HashMap для хранения элементов

    fun add(value: T) {
        map[value] = true
    }

    fun remove(value: T) {
        map.remove(value)
    }

    fun contains(value: T): Boolean = map.containsKey(value)

    override fun iterator(): MutableIterator<T> = MutableHashSetIterator(this)

    fun elements(): MutableSet<T> = map.keys // Получаем элементы множества
}
```

✔ **Использует `HashMap` для хранения уникальных значений**  
✔ **Методы `add()`, `remove()`, `contains()`**  
✔ **Быстрое добавление и удаление (O(1))**

---

## **2. Реализация `MutableIterator`**

```kotlin
class MutableHashSetIterator<T>(private val set: MutableHashSet<T>) : MutableIterator<T> {
    private val iterator = set.elements().iterator()
    private var lastReturned: T? = null

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): T {
        lastReturned = iterator.next()
        return lastReturned!!
    }

    override fun remove() {
        lastReturned?.let {
            set.remove(it)
            lastReturned = null
        } ?: throw IllegalStateException("Call next() before remove()")
    }
}
```

✔ **Позволяет перебирать элементы `HashSet`**  
✔ **Поддерживает `remove()`, но не `set()` или `add()` (так как порядок не гарантирован)**  
✔ **Безопасно удаляет элементы во время итерации**

---

## **3. Тестируем `MutableIterable`**

```kotlin
fun main() {
    val set = MutableHashSet<String>()
    set.add("A")
    set.add("B")
    set.add("C")
    set.add("D")

    val iterator = set.iterator()

    println("Удаляем элемент B:")
    while (iterator.hasNext()) {
        val value = iterator.next()
        if (value == "B") iterator.remove()
    }

    println("Оставшиеся элементы:")
    for (value in set) {
        println(value) // A, C, D (без B)
    }
}
```

---

## **Вывод**

|Метод|Описание|
|---|---|
|`hasNext()`|Проверяет, есть ли следующий элемент|
|`next()`|Возвращает следующий элемент|
|`remove()`|Удаляет последний возвращенный элемент|

✔ **Быстрое добавление и удаление (O(1))**  
✔ **Безопасное удаление элементов во время итерации**  
❌ **Нельзя использовать `set()`, так как элементы неупорядочены**  
❌ **Нельзя `add()` через итератор, так как `HashSet` не имеет порядка вставки**

## В Android SDK и Kotlin
- Любой `for (x in collection)` — это вызов `iterator()`; свой класс станет пригодным для `for`, если объявить `operator fun iterator()`.
- **`Sequence`** — ленивый итератор: элементы вычисляются по требованию, промежуточные коллекции не создаются. См. [[3 Sequence]].
- **`Cursor`** в SQLite и `ContentProvider` — итератор по строкам результата запроса (`moveToNext()`).
- **`Flow`** — асинхронный итератор: те же «есть ли следующий элемент» и «дай следующий», но с приостановкой вместо блокировки. См. [[4 Flow]].
- **Paging 3** — итерирование по страницам данных из сети или БД.

## Грабли
- Изменение коллекции во время обхода → `ConcurrentModificationException`. Удалять только через `iterator.remove()` или `removeAll { }`.
- Итератор одноразовый: после полного обхода получают новый. У `Sequence` то же ограничение — повторный `collect` пересчитает всё заново.

Связано: [[GoF patterns]], [[3 Sequence]], [[4 Flow]], [[Collections. Overview]]