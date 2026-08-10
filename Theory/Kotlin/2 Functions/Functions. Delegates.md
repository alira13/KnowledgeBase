### Делегирование свойствв
Делегаты существуют для того, чтобы передать ответственность за создание объекта или свойства кому-то другому. Например геттеров и сеттеров

![](<../../images/Pasted image 20250319155837.png>)



![](<../../images/Pasted image 20250318150753.png>)

В стандартной библиотеке Kotlin есть разные встроенные делегаты
by lazy - проверяет, было ли инициализировано поле, если да, присваивает мему значение, если нет, то присваивате то что было передано в lambda. Использует double check и блок synhronized

```kotlin
@file:RequiresApi(Build.VERSION_CODES.O)  
  
package com.example.functions.delegates    
  
private fun main() {  
    val user = User()  
    user.creditCardNumber = "1111 2222 3333 4444"  
    user.password = "123"  
    val decoded = user.readOnlyProperty  
    println(">>>>>" + decoded)  
  
    //println("card = ${user.creditCardNumber} password = ${user.password} ${user.someProp} ")  
}  
  
class User() {  
    val someProp by lazy {  
        "Created by lazy"  
    }  
  
    // создаем с помощью класса делегата  
    var password by EncryptedProperty()  
  
    // тоже самое что и верхняя строка  
    var creditCardNumber by encrypted()  
  
    // для read only property когда нам просто надо создать объект кем-то  
    val readOnlyProperty: String by ReadOnlyEncryptedProperty()  
}  
  
// по аналогии с lazy метод который возвращает экземпляр делегата  
private fun encrypted() = EncryptedProperty()  
  
class EncryptedProperty() : ReadWriteProperty<User, String> {  
    private var encryptedValue: String = ""  
  
    override fun getValue(thisRef: User, property: KProperty<*>): String {  
        println("Get value for $thisRef for property ${property.name}")  
        val decoded = String(Base64.getDecoder().decode(encryptedValue))  
        println("Decoded: $encryptedValue = $decoded")  
        return (decoded)  
    }  
  
    override fun setValue(thisRef: User, property: KProperty<*>, value: String) {  
        println("Set value for $thisRef for property ${property.name}")  
        encryptedValue = String(Base64.getEncoder().encode(value.toByteArray()))  
        println("Encoded: $value = $encryptedValue")  
    }  
}  
  
class ReadOnlyEncryptedProperty() : ReadOnlyProperty<User, String> {  
  
    override fun getValue(thisRef: User, property: KProperty<*>): String {  
        return "I set some value for ${property.name}"  
    }  
}
```

### Делегирование реализации интерфейсов
Когда нам нужно реализовать интерфейс, но все что мы делаем, это просто вызываем методы другого обхекта, описавшего эти методы, мы можем делигировать реализацию этих методов напрямую объекту чтобы не дублировать код

```kotlin
// пример с делегированием реализации интерфейса  
data class FlyingPlayer(val player: Player) : Player by player {  
    override val userName: String  
        get() = player.userName  
  
    // чтобы не писать повторяющийся код, мы просто делегировали переопределение методов обхекту player  
    /*    
    override fun run() {        player.run()    }  
    override fun fight() {        player.fight()    }     
    */  
    
    fun fly() {  
        println("$userName fly")  
    }  
}
```