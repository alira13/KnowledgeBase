# Any, Unit, Nothing и null

Три особых типа, которые задают границы системы типов Kotlin: `Any` — вершина иерархии, `Nothing` — её низ, `Unit` — «результата нет, но функция завершилась».

![](<../../images/Pasted image 20250122173801.png>)

## Any — вершина иерархии
Родитель **всех** классов (аналог `Object` в Java). Любой тип — его наследник, поэтому переменной `Any` можно присвоить что угодно, кроме `null` (для этого есть `Any?`).

Содержит ровно три метода: `equals()`, `hashCode()`, `toString()` — см. [[Classes. toString, equals, hashCode, copy]]. Обрати внимание: `wait`/`notify`/`getClass` из Java-`Object` в `Any` **не входят**.

```kotlin
fun printAny(value: Any) {
    println("Значение: $value, тип: ${value::class.simpleName}")
}
printAny(42)        // Int
printAny("Привет")  // String

fun printNullable(value: Any?) = println(value ?: "значения нет")
```
Верхняя граница обобщённого параметра по умолчанию — именно `Any?`, поэтому `<T>` принимает и nullable-типы. См. [[Generics. Basics]].

## Unit — «ничего полезного не вернули»
Аналог `void`, но с важным отличием: `Unit` — **настоящий тип и настоящее значение**, синглтон-`object`:
```kotlin
public object Unit {
    override fun toString() = "kotlin.Unit"
}
```
Любая функция без явного типа возвращает `Unit`:
```kotlin
fun knockKnock() { println("Who's there?") }
fun knockKnock(): Unit = println("Who's there?")   // то же самое

val result: Unit = println("Сообщение")
println(result)     // kotlin.Unit
```
**Зачем тип вместо `void`?** Чтобы «ничего» можно было подставить в обобщённый код: `void` в Java нельзя использовать как аргумент типа, а `Unit` — можно. Отсюда `Function0<Unit>`, `Continuation<Unit>`, `Result<Unit>` и лямбды `() -> Unit`. При компиляции в байткод функция, возвращающая `Unit`, становится `void`.

## Nothing — «сюда управление не вернётся»
Тип, у которого **нет ни одного значения**: создать его нельзя, конструктор приватный.
```kotlin
public class Nothing private constructor()
```
`Nothing` — наследник **любого** типа, даже `final`-класса. Он описывает функцию, которая никогда не завершится нормально: бросит исключение или зациклится.

```kotlin
fun fail(message: String): Nothing = throw IllegalArgumentException(message)

public inline fun TODO(): Nothing = throw NotImplementedError()
```
Именно поэтому компилируется вот это:
```kotlin
override fun getData(word: String): List<Data> = TODO()   // Nothing подходит под List<Data>
```
В Java так нельзя: `Void` не наследник `String`, код не соберётся.

### Зачем это на практике
`Nothing` позволяет компилятору выводить типы там, где ветка завершается аварийно:
```kotlin
val user = findUser(id) ?: throw NotFoundException()   // тип user — User, а не User?

val name = when (value) {
    in 1..10 -> "мало"
    else -> fail("вне диапазона")     // ветка типа Nothing не портит общий тип String
}
```
Без `Nothing` компилятору пришлось бы искать общий тип `String` и «результата нет».

Проверь себя:
```kotlin
fun funOne(): Unit { while (true) {} }      // ок
fun funTwo(): Nothing { while (true) {} }   // ок — никогда не вернётся
fun funThree(): Unit { println("hi") }      // ок
fun funFour(): Nothing { println("hi") }    // ОШИБКА: функция завершается нормально
```

## null и Nothing?
Тип литерала `null` — это `Nothing?`: единственное его значение и есть `null`. Отсюда поведение, которое иногда удивляет:
```kotlin
val list = listOf(null, null)      // List<Nothing?>
val empty = emptyList<Nothing>()   // подходит под List<чего угодно> — благодаря ковариантности
```
`Nothing?` — подтип всех nullable-типов, поэтому `null` присваивается любому из них. См. [[Types. 0 Nullable, not-null]], [[Generics. Variance (in, out)]].

## Сводка
| Тип | Сколько значений | Смысл | Где встречается |
| --- | --- | --- | --- |
| `Any` | все объекты | вершина иерархии, аналог `Object` | обобщённый код, гетерогенные коллекции |
| `Unit` | ровно одно (`Unit`) | функция завершилась, результата нет | функции без `return`, лямбды `() -> Unit` |
| `Nothing` | ни одного | функция не вернётся | `throw`, `TODO()`, бесконечный цикл |
| `Nothing?` | одно (`null`) | тип литерала `null` | `emptyList()`, вывод типов |

## Вопросы-ловушки
- Чем `Unit` отличается от `void`? → `Unit` — реальный тип со значением-синглтоном, его можно подставлять как аргумент типа; в байткоде превращается в `void`.
- Почему `TODO()` компилируется в функции, возвращающей `List<Data>`? → `TODO()` возвращает `Nothing`, а `Nothing` — подтип любого типа.
- Какой тип у `null`? → `Nothing?`.
- Можно ли создать экземпляр `Nothing`? → нет, приватный конструктор; у типа нет значений.
- Зачем `Nothing` нужен компилятору? → чтобы аварийные ветки (`?: throw`, `else -> fail()`) не влияли на вывод типа всего выражения.
- Есть ли в `Any` метод `wait()`? → нет, в отличие от Java-`Object`.

Источник: [Разбираемся в типах Kotlin](https://gb.ru/blog/razbiraemsya-v-tipah-kotlin-unit-nothing-any-i-null/)

Связано: [[Types. 0 Nullable, not-null]], [[Classes. toString, equals, hashCode, copy]], [[Generics. Basics]], [[Generics. Variance (in, out)]], [[Types. 1 Primitive types]]
