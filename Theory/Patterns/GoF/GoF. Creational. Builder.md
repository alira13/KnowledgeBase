Проблемы, которые решает паттерн
 - много параметров в конструкторе. Раньше не было понятия именованных параметров, было легко запутаться - решено в Kotlin
 - хочется использовать значения по умолчанию - решено в Kotlin, но хочется уметь и изменять значения, при этом чтобы конечные свойства класса не были изменяемыми -  в котлин это решается установкой значений по умолчанию и затем при создании объекта мы что-то оставляем по умолчанию, а что-то изменяем
 - но многие классы уже написаны, в Java некоторых удобных штук нет, поэтому стоит разбираться как он работает. Например Retrofit

Позволяет вынести всю работу по созданию  объекта в отдельный класс. В свое время он решал проблему default и именованных параметров.
Сейчас если нужно настроить объект после его создания, обычно используют функции высшего порядка типа apply

Работа
Создаем Builder
Через методы Builder переопределяем свойства
Вызываем build и он создает нам объект 

Пример
Retrofit.build()

```kotlin
package com.example.designpatterns.builder  
  
fun main() {  
    // создем билдер и передаем в него нужные свойства, затем вызываем build  
    val product = Product.Builder()  
        .name("Smartphone")  
        .price(999.99)  
        .manufacturer("TechCorp")  
        .warranty(24)  
        .build()  
  
    println(product)  
}  
  
// чтобы каким-то неправильным методом не создать Product и  
// только 1 вариант был через билдер - делаем конструктор приватным  
private data class Product private constructor(  
    private val name: String,  
    private val price: Double,  
    private val manufacturer: String,  
    private val warranty: Int  
) {  
    class Builder() {  
        // чтобы нельзя было изменить когда создаем список, мы через методы устанавливаем наши свойства  
        // здесь можем выставить значения по умолчанию        private var name: String = ""  
        private var price: Double = 0.0  
        private var manufacturer: String = ""  
        private var warranty: Int = 0  
  
        // для функционального стиля вызова возвращает Builder  
        fun name(name: String): Builder {  
            this.name = name  
            return this  
        }  
  
        fun price(price: Double): Builder {  
            this.price = price  
            return this  
        }  
  
        fun manufacturer(manufacturer: String): Builder {  
            this.manufacturer = manufacturer  
            return this  
        }  
  
        fun warranty(months: Int): Builder {  
            this.warranty = months  
            return this  
        }  
  
        // создаем объект  
        fun build(): Product {  
            return Product(this.name, this.price, this.manufacturer, this.warranty)  
        }  
    }  
}
```

## В Android SDK
Паттерн встречается постоянно — почти всё, что называется `...Builder`:
- `AlertDialog.Builder(context).setTitle(...).setMessage(...).create()`
- `NotificationCompat.Builder(context, channelId)...build()`
- `Retrofit.Builder().baseUrl(...).addConverterFactory(...).build()`
- `OkHttpClient.Builder()`, `Room.databaseBuilder(...)`
- `OneTimeWorkRequestBuilder<T>().setConstraints(...).build()` — см. [[2 Services and WorkManager]]

Общий признак: каждый метод возвращает сам builder (отсюда цепочка), а `build()` отдаёт готовый **неизменяемый** объект.

## Builder в Kotlin
Часть задач, ради которых паттерн придумали, язык решает сам: **именованные аргументы** и **значения по умолчанию** снимают проблему конструктора на восемь параметров, а `apply` настраивает объект после создания.
```kotlin
val product = Product(name = "Телефон", price = 100, warranty = 12)
val paint = Paint().apply { color = Color.RED; strokeWidth = 4f }
```
Свой билдер оправдан, когда объект собирается **по частям в разных местах кода**, когда в `build()` нужна валидация целостности и когда библиотекой пользуются из Java — там именованных аргументов нет.

## Вопрос-ловушка
Зачем builder, если в Kotlin есть именованные параметры? → сборка растянута по коду, нужна проверка перед созданием, нужен удобный Java-API.

Связано: [[GoF patterns]], [[Functions. Scope functions (let, run, with, apply, also)]], [[Classes. Constructors and init]]
