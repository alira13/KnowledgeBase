
Суть паттерна заключается в том, что для наблюдения за определённым объектом, который может отдавать какие-то данные или выполнять какие-то действия, вводится механизм **подписки** (**subscription**). Наблюдаемый объект (`Observable`) позволяет другим объектам (`Observer`) подписаться на обновления. Каждый раз, когда `Observable` что-то делает, он сообщает своим подписчикам об этом, и те реагируют нужным им образом.  

### Классическая реализация 
Примеры
LiveDate Flow Single Comlitable Observable

Состоит из:
 1. Observer  - подписчик(наблюдатель), который должен как-то реагировать на изменения данных(Логгер). 
	 Реализован в виде функционального параметризированного(тип данных) интерфейса с 1 методом onDataChanged(), в котором описывает, что нужно делать при изменении данных
	 
```kotlin
package observer.classic  
  
// Где T-тип данных, на изменения которых мы подписываемся, например список строк  
interface Observer<T> {  
    fun onDataChanged(newData: T)  
}
```
 
 2. Observable - наблюдаемый объект, в котором изменяются данные (репозиторий, в котором данные удаляются или добавляются). Является интерфейсом, который содержит
 -  изменяемые данные
  - коллекцию observers
  - метод добавления нового observer в коллекцию
  - метод удаления observer из коллекции
  - метод оповещения всех observer об изменении данных(вызов у `observer.onDataChanged())`

```Kotlin
package observer.classic  
  
// Где T-тип данных, на изменения которых мы подписываемся, например список строк  
interface Observable<T> {  
    // 1 Данные, на изменение которых мы подписываемся  
    val data : T  
  
    // 2 Подписчики  
    val observers:List<Observer<T>>  
  
    // 3 Метод подписки  
    fun addOnDataChangeListener(observer: Observer<T>) {  
        // не можем использовать, потому что нужен mutable list  
        // поэтому только в реализации можем описать метод        //listeners.add(listener)    }  
  
    // 4 Метод отписки  
    fun deleteOnDataChangeListener(observer: Observer<T>) {  
        // не можем использовать, потому что нужен mutable list  
        // поэтому только в реализации можем описать метод        //listeners.remove(listener)    }  
  
    // 5 Метод уведомления подписчиков  
    fun notifyObservers() {  
        observers.forEach { it.onDataChanged(data) }  
    }  
}
```

 ##### Как использовать?
1. Нужно реализовать Observable в классе, в котором данные будут изменяться
2.  И можно реализовать, а можно и нет варианты Observers. Так как это функциональный интерфейс, можно и просто вместо них лямбду использовать

### Mutable Observable - реализация
На практике более частый и удобный подход

1. Разница в том, что мы создаем реализацию интерфейса Observable с var data вместо val data
2. И вместо того, чтобы реализовывать интерфейс в Observable, мы просто создаем внутри класса ссылку на реализацию Observable 
	1. не нужно реализовывать доп методы, а лишь передать ссылку. Смотрится чище и аккуратнее
	2. можно наблюдать за множеством объектов, при этом не нужно реализовывать для каждого интерфейс, а лишь создать еще одну ссылку на реализацию

```kotlin
package observer.mutableObservable  
  
class MutableObservable<T>(private val initialValue: T) : Observable<T> {  
  
    // Разница с обычной реализацией 1 Вот тут изменяемая дата,   
    // которая как только в нее что-то положат, сразу всех оповещает.   
    // Не нужно дополнительно вызывать методы оповещения  
    override var data: T = initialValue  
        set(value) {  
            field = value  
            notifyObservers()  
        }  
  
    // 2 Подписчики  
    private var _observers: MutableList<Observer<T>> = mutableListOf()  
    override val observers: List<Observer<T>>  
        get() = _observers  
  
    // 3 Метод подписки  
    override fun addOnDataChangeListener(observer: Observer<T>) {  
        // не можем использовать, потому что нужен mutable list  
        // поэтому только в реализации можем описать метод
        _observers.add(observer)  
        observer.onDataChanged(data)  
    }  
  
    // 4 Метод отписки  
    override fun deleteOnDataChangeListener(observer: Observer<T>) {  
        // не можем использовать, потому что нужен mutable list  
        // поэтому только в реализации можем описать метод
        _observers.remove(observer)  
    }  
  
    // 5 Метод уведомления подписчиков  
    override fun notifyObservers() {  
        observers.forEach { it.onDataChanged(data) }  
    }  
}
```
Именно накой подход используется в LiveData