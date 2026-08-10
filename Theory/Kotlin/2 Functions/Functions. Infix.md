Существует для удобства чтения
Объект, из которого она вызывается пробел аргумент функции

Требования к Infix
 - в аргументах 1 только 1 параметр, не 0 и не >1
 - не должна иметь значений по умолчанию
 - либо функция расширения либо должна находиться внутри какого-то класса


```kotlin
package com.example.infix  
  
private fun main() {  
    val myList1 = listOf(1.myTo("One"), 2.myTo("Two"))  
    val myList2 = listOf(1 myTo "O", 2 myTo "T")  
    myList1.forEach {  
        println(it)  
    }  
  
    myList2.forEach {  
        println(it)  
    }  
}  
  
// инфиксная форма просто позволяет написать тип расширения_имя функции расширения_аргумент  
private infix fun <F, S> F.myTo(second: S): Pair<F, S> {  
    return Pair(this, second)  
}
```