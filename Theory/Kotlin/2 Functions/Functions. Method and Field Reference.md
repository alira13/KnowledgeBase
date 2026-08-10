##  **Method Reference и Field Reference в Kotlin** 

В Kotlin можно передавать ссылки на методы и свойства с помощью `::`. Это называется **Method Reference** (ссылка на метод) и **Property Reference** (ссылка на свойство, aka Field Reference). Давай разберем их подробно.

---

## 🔹 **Method Reference (`::метод`)**

Method Reference позволяет передавать ссылку на функцию или метод **без его вызова**.

### ✅ **Пример: Ссылка на функцию**

```kotlin
fun greet(name: String) {
    println("Привет, $name!")
}

fun main() {
    val action: (String) -> Unit = ::greet  // Передаем ссылку на функцию
    action("Анна") // Выведет: Привет, Анна!
}
```

Здесь `::greet` передает ссылку на функцию `greet`, но не вызывает её сразу.

---

### ✅ **Пример: Ссылка на метод экземпляра класса**

```kotlin
class Printer {
    fun printMessage(message: String) {
        println(message)
    }
}

fun main() {
    val printer = Printer()
    val ref = printer::printMessage  // Ссылка на метод экземпляра
    ref("Hello, Kotlin!") // Выведет: Hello, Kotlin!
}
```

Здесь `printer::printMessage` передает ссылку на метод конкретного объекта `printer`.

---

### ✅ **Пример: Ссылка на статический метод (companion object)**

Если метод находится в **companion object**, можно передавать ссылку так:

```kotlin
class Utils {
    companion object {
        fun capitalize(text: String): String {
            return text.uppercase()
        }
    }
}

fun main() {
    val ref: (String) -> String = Utils::capitalize
    println(ref("hello"))  // Выведет: HELLO
}
```

Здесь `Utils::capitalize` передает ссылку на статический метод.

---

### ✅ **Method Reference в `map`, `filter`, `sortedBy`**

Ссылки на методы удобно использовать в функциональном программировании:

```kotlin
fun double(x: Int) = x * 2

fun main() {
    val numbers = listOf(1, 2, 3, 4)
    val doubled = numbers.map(::double) // Передаем ссылку на double()
    println(doubled) // [2, 4, 6, 8]
}
```

---

## 🔹 **Field Reference (`::поле`)**

Field Reference позволяет передавать **ссылку на свойство (переменную)**.

### ✅ **Ссылка на свойство класса**

```kotlin
class Person(val name: String)

fun main() {
    val person = Person("Иван")
    val ref = person::name  // Ссылка на свойство
    println(ref.get())  // Иван
}
```

Здесь `person::name` создает ссылку на свойство **конкретного объекта**.

---

### ✅ **Ссылка на свойство в `companion object`**

```kotlin
class Config {
    companion object {
        val version = "1.0"
    }
}

fun main() {
    val ref = Config::version  // Ссылка на статическое поле
    println(ref.get())  // 1.0
}
```

---

### ✅ **Ссылка на изменяемое свойство**

Если свойство `var`, его можно **изменять через ссылку**:

```kotlin
class Counter {
    var count = 0
}

fun main() {
    val counter = Counter()
    val ref = counter::count

    println(ref.get())  // 0
    ref.set(42)
    println(counter.count)  // 42
}
```

---

## 🔹 **Разница между Method Reference и Field Reference**

|🔹|Method Reference (`::метод`)|Field Reference (`::поле`)|
|---|---|---|
|Ссылка на...|Функцию или метод|Переменную или свойство|
|Использование|`::имяМетода`|`::имяСвойства`|
|Доступ|`ref(аргументы)`|`ref.get() / ref.set()`|
|Пример|`val ref = ::println`|`val ref = person::name`|

---

## 🔥 **Вывод**

- **Method Reference (`::метод`)** позволяет передавать ссылку на функцию/метод **без вызова**.
- **Field Reference (`::поле`)** позволяет передавать ссылку на **переменную или свойство**.
- Используются в **функциональном программировании**, например, в `map`, `filter`, `sortedBy`.
- Позволяют писать **более лаконичный и читаемый код**.

#### Method reference
Когда мы не хотим создавать свою функцию, а хотим передать ссылку на уже существующую
```kotlin
fun main() {  
    val list = listOf(1,2,3)  
  
    // можем писать так  
    list.forEach{  
        println(it)  
    }  
  
    // а можем сделать ссылку на метод println, чтобы не создавать свою lambda  
    list.forEach(::println)  
}
```

### Property Reference
```kotlin
companion object {  
    private lateinit var myInstance: ProtectedPersonRepositoryImpl2  
  
    fun getInstance(password: String): ProtectedPersonRepositoryImpl2 {  
        if (password == "1") {  
            // ссылка на свойство myInstance  
            if (::myInstance.isInitialized)  
                myInstance = ProtectedPersonRepositoryImpl2()  
            return myInstance  
        } else throw Exception("Invalid passward $password")  
    }  
}