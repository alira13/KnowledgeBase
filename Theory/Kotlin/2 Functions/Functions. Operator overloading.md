Существует ограниченное количество операторов в Kotlin
Чтобы переопределить оператор, нужно
```kotlin
operator fun plus(number:Int)

override fun plus(element: Int) {  
    add(element)  
}
```