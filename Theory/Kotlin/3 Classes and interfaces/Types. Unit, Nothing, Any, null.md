[https://gb.ru/blog/razbiraemsya-v-tipah-kotlin-unit-nothing-any-i-null/](https://gb.ru/blog/razbiraemsya-v-tipah-kotlin-unit-nothing-any-i-null/)
 
![](<../../images/Pasted image 20250122173801.png>)
### Any
Any - любой. Является родителем всех классов. Все классы являются дочерними элементами класса Any. Содержит методы
 - equals()
 - hashCode()
 - toString()
```
// Так делать не надо, но возможность такая есть
val listOfAny = listOf<Any>(){1,"Hello", 2.3}
```
![](<../../images/Pasted image 20250318144125.png>)
### Unit
Наследник Any. Является синглтоном-object(то есть во всей программе только 1 экземпляр Unit).
 - Содержить все методы Any(так как является его наследником как и все классы). То есть можно например вызвать hashCode у Unit.
 - Переопределяет только метод toString(). Который выводит строку "kotlin.Unit"

Все функции в Kotlin если не указано возвращаемое значение, возвразщают Unit.
Используется в лямбда-выражениях.

Unit эквивалентен void в Java. В этом выражении возвращаемый тип можно не указывать, если функция ничего не возвращает. По умолчанию там будет Unit:

```Kotlin
fun knockKnock(){
	println("Who’s there?")
}
//аналог
fun knockKnock(): Unit = println("Who’s there?")
```

  
В стандартной библиотеке Kotlin Unit определён как объект, наследуемый от Any и содержащий единственный метод, переопределяющий toString():  

Обратите внимание на ключевое слово object. Это значит, что Unit является синглтоном. Unit ничего не возвращает, а метод toString всегда будет возвращать “kotlin.Unit”. При компиляции в java-код Unit всегда будет превращаться в void.

```Kotlin
public object Unit {
	override fun toString() = "kotlin.Unit"
}
```

### Nothing

`Nothing` является типом, который полезен при объявлении функции, которая не только ничего не возвращает, но и не завершается.  
  

Nothing — 
 - класс, который является наследником любого класса в Kotlin, даже класса с модификатором final. 
 - При этом Nothing нельзя создать — у него приватный конструктор. В коде он объявлен так:

```Plain
public class Nothing private constructor()
```

Он описывает результат «функции, которая никогда ничего не вернёт». То есть когда она НИКОГДА не завершится нормально

Примером может быть функция, которая выбрасывает exception или в которой запущен бесконечный цикл: в любом из этих случаев она никогда не вернёт значения. В приложениях — независимо от того, какой тип данных возвращает функция, — она может никогда не вернуть данные, потому что произошла ошибка или вычисления затянулись на неопределённый срок. В этом случае имеет смысл использовать Nothing.

Примеры использования:

1. функция TODO(), которая часто служит заглушкой в автоматически генерируемых методах.

```Kotlin
public inline fun TODO(): Nothing = throw NotImplementedError()
```

Вы можете наблюдать такую картину при автогенерации кода:

```Kotlin
override fun getData(word: String): List<Data> {
 TODO("not implemented")
}
```

И хотя возвращаемое значение тут List<Data>, мы возвращаем Nothing. Именно потому что Nothing наследуется от всех классов:

```kotlin
fun doSomething(): Something = TODO()
```

Код прекрасно скомпилируется, потому что Nothing наследуется от Something. Но приложение сразу же упадёт с NotImplementedError, если вы вызовете метод doSomething.

_Интересно, что в Java нельзя написать что-то подобное: код просто не скомпилируется, потому что Void не наследуется от String:_

```Plain
static Void todo(){
 throw new RuntimeException("Not Implemented");
}
String myMethod(){
 return todo();
}
```

1. Ещё один пример может касаться выполнения, например, запроса данных из БД или удалённого сервера. Если произошла ошибка, можно возвращать null вместо данных. И это абсолютно нормально, данных-то нет:

```Plain
fun getData(): Data? = ...
```

А если хочется немного больше информации, чем просто null? Например, узнать тип ошибки. Вот тут Nothing приходит на помощь:

```Plain
fun getData(onError: (SomeError) -> Nothing): Data = ...
```

Вот как это может выглядеть в коде:

```Kotlin
val data = getData() { err ->
		 when (err) {
		 is InvalidStatement -> throw Exception(err.parseError)
		 is NoSuchData -> ...
	 }
 }
 return Data() //успешный сценарий
}
```

Закрепим:

```Plain
//Скомпилируются нормально
fun funOne(): Unit { while (true) {} }
fun funTwo(): Nothing { while (true) {} }
//Ок
fun funThree(): Unit { println("hi") }
//Не ок
fun funFour(): Nothing { println("hi") }
```




В Kotlin три особенных типа — `Unit`, `Any` и `Nothing` — играют важную роль в языке. Рассмотрим каждый из них с примерами.

---

### 1. **`Unit`**

Тип `Unit` аналогичен `void` в Java. Он указывает, что функция ничего не возвращает (формально возвращает единичное значение). Используется для функций, не имеющих полезного результата.

#### Пример 1: Использование `Unit` в функции

```kotlin
fun printMessage(message: String): Unit {
    println(message)
}

// Эквивалентно:
fun printMessageShort(message: String) {
    println(message) // `Unit` можно опустить
}

fun main() {
    printMessage("Привет, Kotlin!") // Привет, Kotlin!
}
```

#### Пример 2: `Unit` как возвращаемое значение по умолчанию

```kotlin
val result: Unit = println("Сообщение") // println возвращает Unit
println(result) // Печатает "kotlin.Unit"
```

---

### 2. **`Any`**

`Any` — базовый тип всех классов в Kotlin, аналог `Object` в Java. Он может хранить значения любого типа, кроме `null`, если явно не указано `Any?`.

#### Пример 1: Переменная типа `Any`

```kotlin
fun printAny(value: Any) {
    println("Значение: $value, тип: ${value::class.simpleName}")
}

fun main() {
    printAny(42) // Значение: 42, тип: Int
    printAny("Привет") // Значение: Привет, тип: String
    printAny(3.14) // Значение: 3.14, тип: Double
}
```

#### Пример 2: Приведение типа

```kotlin
fun checkType(value: Any) {
    if (value is String) {
        println("Строка длиной ${value.length}")
    } else {
        println("Не строка")
    }
}

fun main() {
    checkType("Kotlin") // Строка длиной 6
    checkType(100) // Не строка
}
```

#### Пример 3: `Any?` для значений, допускающих `null`

```kotlin
fun printNullable(value: Any?) {
    println(value ?: "Значение отсутствует")
}

fun main() {
    printNullable(null) // Значение отсутствует
    printNullable("Текст") // Текст
}
```

---

### 3. **`Nothing`**

`Nothing` обозначает **недостижимый код** или функцию, которая никогда не возвращает значение (например, функция, выбрасывающая исключение). Он используется для указания, что выполнение программы дальше не продолжается.

#### Пример 1: Функция с исключением

```kotlin
fun fail(message: String): Nothing {
    throw IllegalArgumentException(message)
}

fun main() {
    fail("Ошибка!") // Выбрасывает исключение и завершает выполнение
}
```

#### Пример 2: Условие, всегда завершающееся с `Nothing`

```kotlin
fun checkNumber(value: Int): String {
    return when (value) {
        in 1..10 -> "Число в диапазоне 1-10"
        else -> fail("Число вне диапазона") // Завершает выполнение
    }
}

fun main() {
    println(checkNumber(5)) // Число в диапазоне 1-10
    println(checkNumber(15)) // Выбрасывает IllegalArgumentException
}
```

#### Пример 3: Использование в качестве "заглушки"

```kotlin
val notImplemented: Nothing
    get() = throw NotImplementedError("Этот функционал еще не реализован")

fun main() {
    println(notImplemented) // Выбрасывает NotImplementedError
}
```

---

### Сравнение типов

|Тип|Описание|Пример использования|
|---|---|---|
|**`Unit`**|Функции, которые ничего не возвращают (аналог `void`).|Функции `println`, обработка действий без результата.|
|**`Any`**|Базовый тип всех классов (аналог `Object`).|Универсальные коллекции, переменные любого типа.|
|**`Nothing`**|Тип, указывающий на недостижимый код (функция, которая не возвращает результата).|Функции с исключениями, заглушки.|

Эти типы помогают Kotlin быть более безопасным и читаемым, обеспечивая строгую типизацию и предсказуемость поведения.