`inline` – это **ключевое слово** в **Kotlin**, используемое для оптимизации вызовов функций, особенно **высшего порядка функций** (функций, принимающих другие функции в качестве параметров).

---

## **🔹 1. Что такое inline-функции?**

В **обычных функциях** передача лямбды создает **новый объект** `Function` и вызывает его через **виртуальную таблицу (VTable)**.  
**`inline` встраивает код** функции **прямо в место вызова**, что:  
✅ **Устраняет накладные расходы на вызов**  
✅ **Избегает создания объектов лямбд**

Проще говоря при inline в месте вызова функции, использующей lambda в decompile мы увидим прям сам кусок кода функции, как будто бы мы этот код не выделяли в отдельную функцию, а прям прописали построчно сами. А без Inline будет создана реализация интерфейска Function и мы увидим ссылкку на созданный объект типа Function

### **Пример: без `inline`**

```kotlin
fun doAction(action: () -> Unit) {
    println("Before action")
    action()
    println("After action")
}

fun main() {
    doAction { println("Inside action") }
}
```

**Компиляция без `inline` делает:**

- Создает объект `Function0`
- Вызывает `invoke()`, что **медленнее**

### **Пример: с `inline`**

```kotlin
inline fun doAction(action: () -> Unit) {
    println("Before action")
    action()
    println("After action")
}

fun main() {
    doAction { println("Inside action") }
}
```

💡 **Теперь код `action()` встроится прямо в `doAction()` → без лишних вызовов и объектов**.

---

## **🔹 2. Преимущества `inline`**

### ✅ **1) Оптимизация производительности**

- **Без `inline`**: при передаче лямбды создается новый объект **Function**.
- **С `inline`**: лямбда **встраивается** прямо в код, **не создавая объекта**.

### ✅ **2) Ускорение работы с лямбдами**

Особенно полезно, когда функция часто вызывается в **циклах**.

---

## **🔹 3. Ограничения и побочные эффекты `inline`**

### ❌ **1) Увеличение размера байткода**

- Так как код **встраивается**, он **дублируется** в нескольких местах, увеличивая размер `.class` файлов.

### ❌ **2) Не все функции можно инлайнить**

- **Рекурсивные функции** нельзя сделать `inline`, потому что это вызовет бесконечную подстановку.

```kotlin
inline fun factorial(n: Int): Int {
    return if (n == 1) 1 else n * factorial(n - 1) // Ошибка
}
```

- **Функции с `private` или `protected` модификаторами в интерфейсах** не могут быть `inline`.

### ❌ **3) `inline` не работает с лямбдами, содержащими `return`**

**Пример проблемы:**

```kotlin
inline fun doSomething(action: () -> Unit) {
    action()
}

fun main() {
    doSomething {
        return // Ошибка: `return` нельзя использовать в `inline`-функции
    }
}
```

👉 **Решение**: используем `crossinline` (обсудим дальше).

---

## **🔹 4. `noinline` – когда НЕ нужно инлайнить?**

Если в `inline`-функции есть несколько лямбд, но не все из них нужно встраивать, можно пометить их `noinline`:

```kotlin
inline fun doAction(inlinedAction: () -> Unit, noinline nonInlinedAction: () -> Unit) {
    inlinedAction() // Встроится
    nonInlinedAction() // Не встроится
}
```

![](<images/Pasted image 20250317125254.png>)

### **Когда использовать `noinline`?**

- Когда **лямбда передается в другую функцию**.
- Когда **уменьшаем размер байткода**.

---

## **🔹 5. `crossinline` – когда нельзя `return`?**

Лямбда внутри `inline`-функции **может использовать `return`**, но если она **используется в другом контексте (например, передается в `forEach`)**, компилятор выдаст ошибку.

```kotlin
inline fun runAction(crossinline action: () -> Unit) {
    listOf(1, 2, 3).forEach {
        action() // Без `crossinline` тут была бы ошибка
    }
}
```

### **Когда использовать `crossinline`?**

- Когда лямбда **используется внутри вложенного контекста**.
- Когда **нужно предотвратить `return` из внешней функции**.

---

## **🔹 6. `reified` – инлайн-типизация для Generics**

В Kotlin **дженерики (`T`) стираются в байткоде** (type erasure), но с `inline` можно сохранить тип с помощью `reified`.

### **Пример без `reified` (не работает)**

```kotlin
fun <T> getClassName(): String {
    return T::class.java.simpleName // Ошибка: T стерся
}
```

### **Пример с `inline` и `reified`**

```kotlin
inline fun <reified T> getClassName(): String {
    return T::class.java.simpleName // Работает!
}

fun main() {
    println(getClassName<Int>()) // Int
}
```

### **Когда использовать `reified`?**

- Когда нужно сохранить тип **без рефлексии**.
- Когда передаем `Class<T>` без явного указания.

---

## **🔹 7. Ключевые отличия `inline`, `noinline`, `crossinline`, `reified`**

|**Модификатор**|**Значение**|**Когда использовать?**|
|---|---|---|
|`inline`|Встраивает код функции в место вызова|Уменьшает накладные расходы вызова|
|`noinline`|Запрещает инлайнинг для конкретной лямбды|Когда нужно передать лямбду в другую функцию|
|`crossinline`|Запрещает `return` в лямбде|Когда лямбда вызывается в `forEach`, `Runnable`|
|`reified`|Позволяет использовать `T::class`|При работе с generics внутри `inline`|

---

## **🔹 8. Практические примеры**

### **📌 Использование `inline` для производительности**

```kotlin
inline fun measureTime(action: () -> Unit) {
    val start = System.currentTimeMillis()
    action()
    val end = System.currentTimeMillis()
    println("Время выполнения: ${end - start} ms")
}

fun main() {
    measureTime {
        Thread.sleep(100)
    }
}
```

### **📌 Использование `reified` для логирования**

```kotlin
inline fun <reified T> log(message: String) {
    println("${T::class.simpleName}: $message")
}

fun main() {
    log<String>("Это строка") // String: Это строка
}
```

---

## **🔹 9. Итог: что спрашивают на собеседовании?**

### **1. Что делает `inline`?**

- Встраивает код в место вызова.
- Убирает накладные расходы на вызов функций.
- Уменьшает создание объектов лямбд.

### **2. Чем `inline` отличается от `noinline`?**

- `inline` **встраивает** код, а `noinline` запрещает это.

### **3. Что делает `crossinline`?**

- **Запрещает `return` внутри лямбды**, если она вызывается в другом контексте.

### **4. Как работает `reified`?**

- Позволяет **использовать `T::class`** в `inline`-функции.

### **5. Когда НЕ стоит использовать `inline`?**

- Если функция большая → увеличится байткод. Вызовем 1000 раз Inline и будет дофига кода. То есть нужно использовать Inline только в маленьких функциях
- Если лямбда не критична для производительности.

![](<images/Pasted image 20250317124248.png>)

---

🔥 **Теперь ты готов к собеседованию!** Если зададут вопросы про `inline`, ты ответишь уверенно! 🚀


```kotlin
package com.example.generics  
  
private fun main() {  
    val myList = (0..10).toList()  
  
  
    myList.myFilter {  
        // можем потому что inline  
        // дойдет до 9 и выйдет из main, println не напечатает ничего        // (it == 9) return - non local return(когда мы находимся в одной функции, но выходим из той, которая ее вызвала)        it % 2 == 0  
    }.forEach { println(it) }  
  
    myList.myFilter(object : Condition<Int> {  
        override fun isCorrect(item: Int): Boolean {  
            // тут не можем  
            // if (item==9) return            return item % 2 == 0  
        }  
    })  
}  
  
// В byte-code реализация 1 и 2 будут абсолютно идентичны,  
// вместо lambda будет создан объект интерфейсного типа Condition  
  
// Проблемы:  
// 1. нельзя вызвать suspend функцию внутри myFilter,  
// так как все происходит в объекте Condition в методе isCorrect которые не suspend  
  
// 2. каждый раз при вызове будет создаваться лишний объект Condition - долго и затратно по памяти.  
// В случае inline он все еще будет аргументом в методе,  
// но сам метод не будет нигде использоваться, будет напрямую вставляться код  
  
// 3. нельзя прервать работу метода, который вызвал myFilter, в нашем случае main  
// Решение  - сделать inline myFilter  
  
//4. без inline не можем работать с типом дженерика T(reified используется только с inline)  
  
// реализация 1.  
private inline fun <T> List<T>.myFilter(lambda: (T) -> Boolean): List<T> {  
    val resultList = mutableListOf<T>()  
    this.forEach { if (lambda(it)) resultList.add(it) }  
    return resultList  
}  
  
// реализация 2  
private fun <T> List<T>.myFilter(condition: Condition<T>): List<T> {  
    val resultList = mutableListOf<T>()  
    this.forEach { if (condition.isCorrect(it)) resultList.add(it) }  
    return resultList  
}  
  
interface Condition<T> {  
    fun isCorrect(item: T): Boolean  
}
```

Crossinline
```kotlin
package com.example.generics  
  
import kotlinx.coroutines.CoroutineScope  
import kotlinx.coroutines.Dispatchers  
import kotlinx.coroutines.launch  
import kotlin.concurrent.thread  
  
// crossinline - другой контекст выполнения, а значит  
// мы не можем внутри crossinline сделать non-local return  
// не можем внутри crossinline вызывать suspend-функцию,так как она будет находиться вне корутины  
private fun main() {  
    CoroutineScope(Dispatchers.IO).launch {  
        doSmth {  
            println("Command1")  
            // не получится так как мы указали crossinline, что у нее другой контекст выполнения и она не будет вызвана в рамках этой корутины  
            //delay(1000)            // нельзя потому что crossinline            // return        }  
    }}  
  
// crossinline указывается у lambda-параметра функции, которая принимает lambda, // выполняющуюся в другом контексте, например в другом потоке, котрутине или анонимном классе  
// без добавления crossinline не будет работать, так как может содержать non-local return  
// если хотим сделать функцию inline, тогда нам нужно пометить,  
// что у command1 другой контекст выполнения - а значит нелокальный return нельзя будет сделать  
private inline fun doSmth(crossinline command1: () -> Unit) {  
    thread {  
        command1()  
    }  
}
```