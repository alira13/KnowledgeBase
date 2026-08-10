После компиляции никаких дженериков не существует.  Параметризированный тип на самом деле при декомпиляци в Java становится Object - то есть тип **стирается** и затем приводится к типу, который мы уже указали как T
Поэтому когда у нас есть параметризированный метод, в котором мы в зависимости от ПРОИЗВОДНОГО типа T хотим производить разные действия `T is List<String>`, это сделать невозможно, так как на самом деле List<T> это просто List при декомпиляции.

```kotlin
private fun main() {  
    val b1 = Box(10)  
    val b2 = Box(20)  
    val bv = b1.value as Int + b2.value as Int  
    println(bv)  
  
    // По сути  
    val p1 = ParamBox(30)  
    val p2 = ParamBox(50)  
    val pv = p1.value + p2.value  
    println(pv)  
  
    doSmth(p1)  
}  

// при декомпиляции работают одинаково. value являются Object - то есть его тип стирается,а затем приводятся к нужному типу.  
private class Box(val value: Any?)  
  
//Дженерик это просто синтаксический сахар, в котором приведение к типу происходит под капотом  
private class ParamBox<T>(val value: T)  
  
// c производным типа T не будет работать, потому что будет стирание типов  
private fun <T> doSmthWithList(el: List<T>) {  
    /*when (el) {  
        is List<String> -> println("This is String")        is List<Box> -> println("This is Box")        else -> println("I dont know")    }     */}  
// c исходным типом T все получается, проверка работает  
private fun <T> doSmth(el: T) {  
    when (el) {  
        is String -> println("This is String")  
        is Box -> println("This is Box")  
        else -> println("I dont know")  
    }  
}

Из-за стирания типов в рантайме по сути не существует параметризованных классов. Соответственно проверка `is List<String>` -  это тоже самое, что `is List<Object>` или просто `is List`. Соответственно, первый вариант после компиляции будет выглядеть примерно так:  

```kotlin
 private fun <T> doSmthWithList(el: List) {
    when (el) {
        is List -> println("This is List of String")
        is List -> println("This is List of Box")
        else -> println("I dont know")
    }
}
```

Получается код, в котором нет никакого смысла + дважды повторяется одно и то же условие в блоке when.  
Во втором варианте информация о типе Т также стирается и объект приводится к типу Object, но в проверках уже участвуют конкретные классы, а не обобщенный тип, такие проверки выполнять можно, поэтому программа работать будет, после компиляции получится что-то вроде  
 
```kotlin
private fun <T> doSmth(el: Any) {
    when (el) {
        is String -> println("This is String")
        is Box -> println("This is Box")
        else -> println("I dont know")
    }
}
```
