`reified` - позволяет получить инфу о типе дженериков в runtime, используется только вместе с` `inline`
`*(start projection)` - используется когда сам дженерик тип внутри функции нам не нужен, нам все равно какой тип был передан в качестве параметра

```kotlin
package com.example.generics  
  
private fun main() {  
    val workers = listOf(Director("Max"), Programmer("John"), Programmer("Lisa"))  
  
    val programmers1 =  
        workers.filter { it is Programmer }.map { it as Programmer }.forEach { it.doWork() }  
    println("--------------------")  
  
    val programmers2 = workers.myFilterIsInstance<Programmer>().forEach {  
        it.doWork()  
    }  
}  
  
open class Worker(val name: String)  
  
private class Director(name: String) : Worker(name)  
  
private class Programmer(name: String) : Worker(name) {  
    fun doWork() {  
        println("Im programming...")  
    }  
}  
  
private inline fun <reified OUT> List<Any>.myFilterIsInstance(): List<OUT> {  
    // тип workers существует только для того, чтобы мы могли обозначить тип List  
    // но если мы хотим работать с абсолютно любым ьтипом коллекции и при этом нам не важно имя, мы его не используем внутри метода    // можно пометить этот тип * star projection чтобы не указывать его при вызове и не параметризировать им функцию    // * анлогична out Any?  - то есть всем Any и всем его наследникам    val resultList = mutableListOf<OUT>()  
    this.forEach {  
        // так как инфа о типе в дженериках стирается, то проверку t is OUT сделать нельзя  
        // тогда мы хотим сразу взять тело функции и подставить в нужную нам точку inline,        // и тогда вместо IN и OUT сразу подставятся нужные типы        // но по умолчанию параметры стираются даже у inline, но у inline можно это отключить. Пометить reified OUT        // reified позволяет получить инфу о типе параметра во время runtime        if (it is OUT) resultList.add(it as OUT)  
    }  
    return resultList  
}
```


