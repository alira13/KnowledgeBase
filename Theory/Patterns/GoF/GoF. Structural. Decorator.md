# Decorator (Декоратор)

**Зачем:** добавить объекту поведение, не меняя его класс и не плодя наследников. Декоратор реализует тот же интерфейс, что и обёрнутый объект, поэтому клиент не замечает подмены, а декораторы можно навешивать друг на друга.

**Альтернатива — наследование**, и вот чем декоратор лучше: комбинации. Три независимых доработки дали бы восемь подклассов на все сочетания; с декораторами их собирают на лету во время выполнения.

**В Android SDK:**
- `ContextWrapper` — обёртка над `Context`, добавляющая или подменяющая поведение (на ней стоят `Activity`, `Service`, `Application`). См. [[Context]].
- Потоки Java: `BufferedInputStream(FileInputStream(...))` — буферизация поверх чтения.
- **Интерцепторы OkHttp** — каждый оборачивает вызов, добавляя логирование, заголовки, повторы. См. [[Networking. Retrofit and OkHttp]].
- `Modifier` в Compose — цепочка декораторов над композаблом.

Декоратор-обёртка позволяет добавить дополнительную функциональность уже существующим классам
```kotlin
data class PremiumPlayer(val player: Player) : Player {  
    override val userName: String  
        get() = player.userName  
  
    override fun run() {  
        player.run()  
    }  
  
    override fun fight() {  
        player.fight()  
    }  
  
    fun alive() {  
        println("$userName alive")  
    }  
}
```

Коротка версия с использованием делегатов реализации интерфейсов
```kotlin
// пример с делегированием реализации интерфейса  
data class FlyingPlayer(val player: Player) : Player by player {  
    override val userName: String  
        get() = player.userName  
  
    // чтобы не писать повторяющийся код, мы просто делегировали переопределение методов обхекту player  
    /*    override fun run() {        player.run()    }  
    override fun fight() {        player.fight()    }     */  
    fun fly() {  
        println("$userName fly")  
    }  
}
```

Еще пример Логирование, без делегата пришлось бы все методы MutableList переопределять руками, а так под капотом они сами переопределились черещ by list
```kotlin
fun main() {  
    val list: MutableList<Int> = mutableListOf(1, 2, 3)  
    val loggedList = LoggedList(list)  
    loggedList.add(1, 4)  
    println(loggedList)  
}  
  
data class LoggedList<T>(private val list: MutableList<T>) : MutableList<T> by list {  
    override fun add(index: Int, element: T) {  
        list.add(index, element).also {  
            println("Added element list[$index] = $element")  
        }  
    }  
}
```
Именно здесь делегирование через `by` окупается: у `MutableList` десятки методов, а переопределить нужен один. См. [[Functions. Delegates]].

## Грабли
- Делегирование фиксируется на этапе компиляции: если обёрнутый объект вызывает свой метод изнутри, он вызовет **свою** реализацию, а не твоё переопределение.
- Длинная цепочка декораторов усложняет отладку — по стеку не сразу видно, кто что добавил.
- Декоратор ломает проверку `is`/приведение к конкретному классу: снаружи виден только интерфейс.

## Вопрос-ловушка
Чем декоратор отличается от прокси? → оба реализуют тот же интерфейс и оборачивают объект, но декоратор **расширяет поведение**, а прокси **контролирует доступ** (ленивое создание, права, кэш, удалённый вызов).

Связано: [[GoF patterns]], [[Functions. Delegates]], [[Context]], [[Networking. Retrofit and OkHttp]]