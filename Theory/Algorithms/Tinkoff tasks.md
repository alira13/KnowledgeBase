1
```kotlin
fun main() {  
    //256 Мб 1сек, кол-во чисел<10^5 число<=10^9(Int)  
    val inStr = "0-5,8-9,11"  
    val intervals = inStr.split(',') 
    val resultString = "" 
    for (interval in intervals) {  
        val syms = interval.split('-')  
        if (syms.size >= 2) {  
            val first = syms[0].toInt()  
            val last = syms[1].toInt()  
            for (i in first..last) {  
                print("$i ")  
                // работаем через string builder
                // создать тест с кол-вом чисел и запустить
            }  
        } else  
            print("${syms[0]} ")  
    }  
}
```

2
```kotlin
fun main() {  
    val sheetsOrDaysCount = readln().toInt()  
    val currentHeights = readln().split(' ')  
  
    var minExpected = 1  
    var isOk = true  
  
    for (i in 0..<sheetsOrDaysCount) {  
        println(">>>>>>>>>>$i")  
        val current = currentHeights[i].toInt()  
        if (current == -1) {  
            minExpected++  
            isOk = true  
        } else if (current < minExpected) {  
            println("Подмена current=$current minExpected = $minExpected")  
            isOk = false  
            break        } else {  
            println("{Все ок current=$current minExpected = $minExpected")  
            minExpected = current + 1  
            isOk = true  
        }  
    }  
    println("isOk=$isOk")  
}
```