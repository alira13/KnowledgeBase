# Singleton (Одиночка)

**Зачем:** гарантировать, что у класса ровно один экземпляр, и дать к нему глобальную точку доступа. Применяют, когда объект дорогой (БД, сетевой клиент) или обязан быть общим (кэш, менеджер настроек).

**Чем платим:** глобальное состояние. Синглтон тяжело подменить в тестах, он скрывает зависимости (класс просит его сам, вместо того чтобы получать извне) и живёт до конца процесса. Поэтому в современных проектах его почти всегда заменяют DI: объект остаётся один, но создаёт и раздаёт его контейнер — см. [[Dependency injection]].

**В Android SDK:** `WorkManager.getInstance()`, `LocalBroadcastManager.getInstance()`, `Room.databaseBuilder(...)` (базу держат в единственном экземпляре), `application` как объект.

### Простейший Singleton
Singleton — паттерн, который позволяет создать класс таким образом, чтобы он имел во всей программе только один свой экземпляр.
Самая простая реализация в kotlin: вместо класса  написать object(возможна, только если параметров в конструкторе конструктора!!!)
```
object SingletonClass{}
```

### Custom Singleton с companion object (непотокобезопасный)
Если при создании объекта мы должны передать какие-то параметры, тогда нам нужно самим реализовать singleton. 
 - Запретить создавать экземпляры класса извне(private constructor)
 - Но уметь создавать каким-то образом единственный объект класса
метод getInstance, в котором создаем объект если не был создан и возвращаем его.
 - Чтобы мы могли вызвать getInstance не создавая объект(мы это сделать не можем из-за private constructor), мы помещаем этот метод внутрь companion object, код внутри которого уже будет относиться именно к классу, а не его объектам
```kotlin
class SettingsManager private constructor(context: Context) : BaseManager(context) {  
    private val settings: MutableMap<String, String> = mutableMapOf()  
  
    init {  
        settings.putAll(context.defaultSettings)  
    }  
  
    fun getSetting(key: String): String? {  
        return settings[key]  
    }  
  
    companion object {  
        private var instance: SettingsManager? = null  
        fun getInstance(context: Context): SettingsManager {  
            if (instance == null) instance = SettingsManager(context)  
            return instance!!  
        }  
    }  
}
```
В текущем коде SettingsManager реализован как **обычный Singleton**, но он **не является потокобезопасным**. 

### Custom Singleton с companion object (потокобезопасный). Double check
В многопоточной среде возможна ситуация, когда два потока одновременно вызовут `getInstance()`, что может привести к созданию **нескольких экземпляров**.

Для этого нужен Double-Checked Locking Singleton, чтобы обеспечить потокобезопасность и избежать ненужной синхронизации после первой инициализации.
```kotlin
package com.example.designpatterns.singleton  
  
  
import kotlinx.serialization.json.Json  
import java.io.File  
  
class FinalSingleton private constructor() {  
  
    private val file = File("Persons.json")  
  
    private val _persons: MutableList<Person> = loadPersons()  
  
    val persons: List<Person>  
        get() {  
            return _persons.toList()  
        }  
  
    private fun loadPersons(): MutableList<Person> {  
        val fromFile = file.readText()  
        return Json.decodeFromString<MutableList<Person>>(fromFile)  
    }  
  
    // внутри companion object код относится не к экземпляру класса, а именно к самому классу,  
    // даже если не будет создано ни одного объекта класса, мы все равно сможем обратиться    // к свойствам и методам внутри companion object через название класса  
    // минусы реализации - !! небезопасный вызов instance  
  
    // Double check реализация    companion object {  
        private var instance: FinalSingleton? = null  
  
        // замок для синхронизации для решения проблемы гонки потоков  
        private var lock = Any()  
  
        fun getInstance(password: String): FinalSingleton {  
            if (password == "1") {  
                // FIRST CHECK  
                // чтобы параллельные потоки не ждали, если instance уже создан                // они же его не создают, а просто получают уже. Ничего опасного                // Заменили if (instance != null) return instance!! на безопасный вызов+let                instance?.let {  
                    return it  
                }                // SECOND CHECK  
                // если несколько потоков получают доступ к этому коду, то первый поток зашел,  // закрыл доступ к коду(критической секции), произвел действия и вышел  // в это время второй поток не может зайти пока lock не сменит значение,     // и когда lock освободится, то второй уже увидлит что instance не null                // `synchronized(lock) { ... }` блокирует `lock`, пока один поток выполняет код внутри блока.                synchronized(lock) {  
                    // если !null, то вернем instance, иначе ничего не сделаем  
                    instance?.let {  
                        return it  
                    }                    // тут точно ==null, иначе бы предыдущий return сработал  
                    // значит нам надо создать объект и его присвоить instance. И also вернет instance                    return FinalSingleton().also { instance = it }  
                }            } else throw Exception("Invalid passward $password")  
        }  
    }  
}
```