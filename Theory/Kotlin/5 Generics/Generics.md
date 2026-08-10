# Generics

Одна заметка на всю тему: от `<T>` до `reified` и вариантности.

---

## 1. Что такое generic

Generic позволяет написать код, который работает с разными типами, сохраняя типобезопасность.

```kotlin
fun <T> printValue(value: T) {
    println(value)
}
```

Здесь `T` — переменная типа.

```kotlin
printValue("Hello")   // T = String
printValue(42)        // T = Int
```

Ментальная модель:

> `T` — это не конкретный тип. Это «сюда потом подставят настоящий тип».

---

## 2. Зачем это нужно

Без generic пришлось бы хранить всё как `Any?` и приводить типы руками:

```kotlin
class Box(val value: Any?)

val v = box.value as Int + 10   // приведение пишете вы, ошибётесь — упадёт в runtime
```

С generic то же самое делает компилятор:

```kotlin
class Box<T>(val value: T)

val v = box.value + 10          // приведение вставит компилятор
```

Итого generic даёт:

```text
переиспользование + type safety
```

И ещё важное свойство:

```kotlin
val stringBox = Box("Hello")
stringBox.value.uppercase()     // работает
stringBox.value + 10            // не скомпилируется
```

Ошибка ловится на этапе компиляции, а не у пользователя.

---

## 3. Generic-функция

```kotlin
fun <T> first(list: List<T>): T {
    return list[0]
}
```

Три `T` здесь связаны между собой:

```text
<T>       → объявили параметр типа
List<T>   → список содержит T
: T       → функция возвращает тот же T
```

Поэтому тип выводится сам:

```kotlin
val number = first(listOf(1, 2, 3))     // Int
val name   = first(listOf("A", "B"))    // String
```

Параметр типа объявляется **перед** именем функции.

---

## 4. Несколько параметров типа

```kotlin
fun <K, V> createPair(key: K, value: V): Pair<K, V> {
    return Pair(key, value)
}
```

Вызов:

```kotlin
createPair("id", 42)
```

Получится:

```text
K = String
V = Int
→ Pair<String, Int>
```

Обобщённой может быть и extension-функция:

```kotlin
fun <T> T.also(block: (T) -> Unit): T { block(this); return this }
```

Так устроена `also` из стандартной библиотеки.

---

## 5. Ограничения — upper bounds

Можно сказать: `T` — не любой тип.

```kotlin
fun <T : Number> sum(a: T, b: T) { ... }
```

Теперь:

```kotlin
sum(1, 2)          // можно
sum(1.5, 2.5)      // можно
sum("a", "b")      // нельзя
```

Модель:

```text
<T : Number>
     ↑
T должен быть Number или его наследником
```

Ограничение даёт доступ к методам этого типа: внутри `sum` доступен `toDouble()`.

Частый вариант — сравнение:

```kotlin
fun <T : Comparable<T>> max(a: T, b: T): T = if (a > b) a else b
```

Читается как «T должен уметь сравниваться сам с собой».

Несколько ограничений — через `where`:

```kotlin
fun <T> foo(value: T)
    where T : CharSequence,
          T : Appendable
```

---

## 6. `<T>` и `<T : Any>`

По умолчанию `T` **может быть nullable**:

```kotlin
<T>        → T может оказаться String?
<T : Any>  → T не может быть nullable
```

Верхняя граница по умолчанию — `Any?`.

Поэтому если nullable недопустим, границу задают явно:

```kotlin
fun <T : Any> requireValue(value: T) { ... }
```

---

## 7. Вариантность: зачем она

Вопрос, на который отвечает вариантность:

> Если `Dog` — наследник `Animal`, то как связаны `Box<Dog>` и `Box<Animal>`?

Для обычных типов всё просто:

```kotlin
val animal: Animal = Dog()   // можно всегда
```

Для generic это **не следует автоматически**. Правило нужно задать явно — иначе типобезопасность сломается.

---

## 8. `out` — producer, отдаёт

```text
Producer
T → наружу
```

```kotlin
interface Producer<out T> {
    fun produce(): T          // T только в возвращаемом значении
    // fun add(item: T)       // ошибка: T в позиции параметра запрещён
}
```

Что это даёт:

```kotlin
val animals: Producer<Animal> = producerOfDogs   // можно
```

Почему безопасно:

```text
объект обещает: «я дам тебе Animal»
реально даёт:   Dog
Dog — это Animal → всё в порядке
```

Направление наследования сохраняется:

```text
Dog → Animal
Producer<Dog> → Producer<Animal>
```

---

## 9. `in` — consumer, принимает

```text
Consumer
T ← внутрь
```

```kotlin
interface Consumer<in T> {
    fun consume(value: T)     // T только в параметрах
    // fun produce(): T       // ошибка: T в позиции возврата запрещён
}
```

Что это даёт:

```kotlin
val dogConsumer: Consumer<Dog> = consumerOfAnimals   // можно
```

Почему безопасно:

> Consumer, умеющий принять любого `Animal`, тем более примет `Dog`.

Направление переворачивается:

```text
Dog → Animal
Consumer<Animal> → Consumer<Dog>
```

Главная шпаргалка:

```text
out → отдаю T
in  → принимаю T
```

---

## 10. Три варианта вариантности

**Инвариантность** — по умолчанию:

```kotlin
Box<T>
```

`Box<Dog>` и `Box<Animal>` — несовместимые типы.

**Ковариантность**:

```kotlin
Box<out T>     // направление сохраняется: Dog → Animal
```

**Контравариантность**:

```kotlin
Box<in T>      // направление переворачивается: Animal ← Dog
```

---

## 11. Почему `List` ковариантен, а `MutableList` — нет

`List` объявлен так:

```kotlin
interface List<out E>
```

Поэтому можно:

```kotlin
val animals: List<Animal> = listOfDogs
```

Из списка собак мы только **получаем** животных — это безопасно.

С `MutableList` иначе: у него есть не только `get()`, но и `add()`.

Допустим, разрешили бы:

```kotlin
val animals: MutableList<Animal> = dogs
animals.add(Cat())                        // законно для Animal!
val dog: Dog = dogs[0]                    // а тут ClassCastException
```

Именно поэтому изменяемые коллекции инвариантны.

Правило, которое из этого следует:

> Ковариантный класс должен быть неизменяемым. Любой метод записи компилятор запретит.

---

## 12. PECS

Мнемоника из Java: **Producer Extends, Consumer Super**.

Для Kotlin читается так:

```text
producer → out
consumer → in
```

Классический пример:

```kotlin
fun copy(from: MutableList<out Animal>, to: MutableList<in Animal>) {
    for (item in from) to.add(item)
}
```

```text
from → источник  → out → примет MutableList<Dog>
to   → приёмник  → in  → примет MutableList<Any>
```

---

## 13. Declaration-site и use-site

Вариантность можно задать **в объявлении**:

```kotlin
interface Producer<out T>
```

Это **declaration-site variance** — фича Kotlin, которой нет в Java. Правило пишется один раз.

Или **в месте использования**:

```kotlin
fun copy(from: List<out Animal>, to: MutableList<in Animal>)
```

Это **use-site variance**, в Java ей соответствуют wildcards (`? extends`, `? super`).

---

## 14. `@UnsafeVariance`

Иногда метод формально принимает `T`, но объект не изменяет:

```kotlin
public operator fun contains(element: @UnsafeVariance E): Boolean
```

`contains` только читает, дырки не создаёт — поэтому компилятору говорят «под мою ответственность».

---

## 15. `*` — star projection

```kotlin
List<*>
```

Означает:

> «Я не знаю, какой здесь конкретный тип».

Мы знаем:

```text
это List чего-то
```

Но не знаем, чего именно:

```text
List<Int>? List<String>? List<User>?
```

Поэтому читать можно:

```kotlin
val value = list[0]     // тип Any?
```

А добавлять — нельзя: неизвестно, какой тип там ожидается.

По смыслу `*` ≈ `out Any?` для чтения и `in Nothing` для записи.

---

## 16. `Any` vs `*`

Не путать:

```kotlin
List<Any>   // список, который содержит значения типа Any
List<*>     // список неизвестного конкретного типа
```

В первый можно положить что угодно:

```kotlin
listOf(1, "hello", true)
```

Во второй нельзя положить ничего, зато туда подойдёт любой список:

```kotlin
val a: List<*> = listOf<String>("a")
val b: List<*> = listOf<Int>(1)
```

---

## 17. `Nothing` и generics

`Nothing` — тип, у которого нет ни одного значения, и он является подтипом **любого** типа.

Поэтому он идеален для «пустых» состояний:

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

Раз `UiState` ковариантен:

```text
UiState<Nothing> подойдёт вместо UiState<User>
```

Поэтому один `Loading` работает для любого экрана.

---

## 18. Стирание типов

Вы пишете проверку:

```kotlin
if (list is List<String>) { }
```

И код не компилируется:

```text
cannot check for instance of erased type
```

Причина: параметр типа существует только для компилятора. Тот проверил типы, расставил приведения — и выбросил информацию о `T`.

```text
List<String>
List<Int>
      ↓ стирание
    List
```

Сам `T` становится `Object`.

> Generic живёт на этапе компиляции. Во время выполнения его уже нет.

**Почему так сделали.** Generics появились в Java 5, когда уже существовали миллионы строк кода и работающая JVM. Стирание — плата за обратную совместимость. Kotlin компилируется в тот же байткод и наследует это ограничение.

---

## 19. Что запрещено из-за стирания

Нельзя проверить аргумент типа:

```text
is List<String>
is List<Box>
      ↓ стирание
is List
is List
```

Две одинаковые ветки, вторая недостижима — поэтому компилятор запрещает.

А вот проверить сам объект можно:

```kotlin
when (value) {
    is String -> "строка"      // спрашиваем про класс объекта — он есть
    is List<*> -> "список"     // не утверждаем ничего об элементах
}
```

Разница принципиальная:

```text
is List<String>  → аргумент типа   → его нет в runtime
is String        → класс объекта   → он есть
```

Остальные следствия:

```text
T()          → нет конструктора в runtime
T::class     → нет класса в runtime
Array<T>     → нельзя создать напрямую
```

И нельзя две перегрузки `f(List<String>)` и `f(List<Int>)` — после стирания сигнатуры совпадут.

**Уточнение:** сигнатуры полей и методов сохраняют информацию о типах в метаданных класса, её достаёт рефлексия — на этом построены `TypeToken` в Gson и `typeOf<T>()` в kotlinx.serialization. Исчезают значения в runtime, а не объявления в байткоде.

---

## 20. `reified`

Есть случай, когда тип известен: **inline-функция**. Её тело подставляется в место вызова.

```kotlin
inline fun <reified T> Any.isTypeOf(): Boolean = this is T

"hello".isTypeOf<String>()   // true
```

То, что было запрещено в разделе 19, здесь работает:

```text
inline
  ↓
тело встраивается в место вызова
  ↓
там T уже известен
  ↓
компилятор подставляет String
```

Так устроены знакомые функции:

```kotlin
filterIsInstance<T>()
Gson().fromJson<T>()
koinViewModel<T>()
```

Например, свой `filterIsInstance` пишется так:

```kotlin
inline fun <reified T> List<*>.myFilterIsInstance(): List<T> =
    filter { it is T } as List<T>       // проверка is T возможна только с reified
```

---

## 21. Почему `reified` только с `inline`

```text
reified
   ↓
только inline-функции
```

Ни у классов, ни у обычных функций его не бывает.

Если встраивание не подходит — тип передают явным аргументом:

```kotlin
Class<T>
KClass<T>
```

Так делают Retrofit и Room.

---

## 22. Где встречается в Android

Обобщённый репозиторий:

```kotlin
interface Repository<T> {
    suspend fun get(): T
}

class UserRepository : Repository<User> {
    override suspend fun get(): User { ... }
}
```

Обобщённый UI-state — см. раздел 17.

И `reified`-хелперы:

```kotlin
inline fun <reified T> NavBackStackEntry.getArgument(): T?
```

---

## 23. Java interoperability

Kotlin по умолчанию генерирует wildcards в сигнатурах для Java-кода. Управляется аннотациями:

```text
@JvmSuppressWildcards  → убрать wildcard
@JvmWildcard           → добавить wildcard
```

Важно помнить: в Java **массивы ковариантны**, и это дырка:

```java
Object[] a = new String[1];
a[0] = 1;                    // ArrayStoreException в runtime
```

В Kotlin `Array<T>` инвариантен — эта дырка закрыта.

---

## 24. Что знать на каком уровне

**Junior**

```text
<T>, generic-класс, generic-функция
List<T>, Pair<K, V>
```

Понимать: generic = переиспользование + type safety.

**Middle**

```text
<T : SomeType>, where
in / out, вариантность
*, Any vs *
Nothing, <T : Any>
```

Уметь объяснить: producer → out, consumer → in.

**Middle+**

```text
type erasure
reified + inline
declaration-site / use-site variance
star projection
```

И почему `List<Dog>` подходит вместо `List<Animal>`, а `MutableList<Dog>` — нет.

**Senior**

```text
PECS, nested generics
variance + наследование
Java interop: raw types, wildcards
@JvmSuppressWildcards
```

Спрашивают уже не «что такое out», а «почему здесь компилятор разрешает подстановку, а здесь запрещает».

---

## 25. Вопросы-ловушки

- **Зачем дженерики?** Типобезопасность на этапе компиляции + переиспользование без дублирования и без приведения типов.
- **Можно ли `List<Number>` присвоить `List<Int>`?** Да: `List` ковариантен. С `MutableList` — нет.
- **Почему `MutableList<Dog>` нельзя присвоить `MutableList<Animal>`?** Через вторую ссылку можно было бы добавить `Cat`, и чтение из первой упало бы с `ClassCastException`.
- **Чем `List<out Animal>` отличается от `List<Animal>`?** Ничем: `List` уже ковариантен, `out` здесь избыточен. Разница видна на `MutableList`.
- **Почему нельзя `T()`?** Из-за стирания: в runtime нет конструктора параметра типа. Нужен фабричный параметр или `reified`.
- **Почему нельзя перегрузить по `List<String>` и `List<Int>`?** После стирания это один и тот же `List`, сигнатуры совпадают.
- **Как обобщённая функция узнаёт свой тип?** `reified` в inline-функции либо явно переданный `KClass`/`TypeToken`.
- **Что будет при `list as List<Int>`, если там строки?** Приведение не проверяется (unchecked cast), `ClassCastException` прилетит позже — при первом обращении к элементу.
- **Есть ли стирание в C#?** Нет, там generics существуют и в runtime. Ограничение специфично для JVM.
- **Что вернёт `list[0]` для `List<*>`?** `Any?` — конкретный тип неизвестен.

---

## Коротко

```text
1. Generic = параметр типа → переиспользование + type safety.

2. out = producer → T выходит наружу.
   in  = consumer → T входит внутрь.

3. Ковариантный класс должен быть неизменяемым.

4. * = конкретный тип неизвестен (не путать с Any).

5. JVM стирает типы → reified + inline возвращают
   информацию о T там, где она нужна.
```

Связано: [[Functions. Inline, noinline, crossinline, reified]], [[Types. Unit, Nothing, Any, null]], [[Collections. Overview]], [[Classes. final, enum, data, sealed, abstract, object, companion]]
