Декоратор-обертка
Позволяет добавить доп функциональности уже существующим классам
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