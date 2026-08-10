Если реализация класса нужна только в 1м месте, то используются анонимные классы

Логика аннонимного класса. 
 - Он имеет такой же синтаксис, только без имени
 - Можно также его объект положить в какую-то переменную

```kotlin
// аннонимный класс
// нам нужен синглтон, поэтому object. По сути синтаксис такой же, как у обычного класса,  
// только нет имени у класса, поэтому и анонимный  
val anonymousObject = 
	object : Condition() {  
	    override fun isSuitable(product: Product): Boolean {  
	        return product.name.endsWith("up")  
	    }  
	}
  
// аналог анонимного класса
object FilterLikeAnonymousEndWithUp : Condition() {  
    override fun isSuitable(product: Product): Boolean {  
        return product.name.endsWith("up")  
    }  
}  
```