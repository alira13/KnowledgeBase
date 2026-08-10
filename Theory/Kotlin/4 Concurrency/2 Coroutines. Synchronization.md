### 🔹 **Синхронизация корутин в Kotlin**

Обычный synhronized-блок не работает из-за особенностей suspend-функции(они не умеют освобождать критическую секцию, так как работают с приостановками)

Корутины выполняются асинхронно, но иногда требуется синхронизация, чтобы избежать гонок данных, ошибок конкурентного доступа или обеспечить согласованность данных. В Kotlin для этого есть несколько механизмов.

![](<images/Pasted image 20250313175953.png>)
---

## **1. `Mutex` (Мьютекс)**

🔹 Используется для предотвращения одновременного доступа нескольких корутин к общему ресурсу.

📌 **Пример использования `Mutex`**:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

var counter = 0
val mutex = Mutex()

fun main() = runBlocking {
    val jobs = List(100) {
        launch {
            repeat(100) {
                mutex.withLock {
                    counter++
                }
            }
        }
    }
    jobs.forEach { it.join() }
    println("Final counter value: $counter") // Ожидаем 10000
}
```

✅ **Гарантирует, что только одна корутина изменяет `counter` в один момент времени.**

---

## **2. `Atomic` (атомарные переменные)**

🔹 Используется для потокобезопасных операций без блокировки, например, с `AtomicInteger`.

📌 **Пример использования `AtomicInteger`**:

```kotlin
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

val counterAtomic = AtomicInteger(0)

fun main() = runBlocking {
    val jobs = List(100) {
        launch {
            repeat(100) {
                counterAtomic.incrementAndGet()
            }
        }
    }
    jobs.forEach { it.join() }
    println("Final counter value: ${counterAtomic.get()}") // Ожидаем 10000
}
```

✅ **Работает быстрее, чем `Mutex`, так как не блокирует потоки.**

---

## **3. `withContext(Dispatchers.Default)`**

🔹 Позволяет переключаться на другой поток и выполнять код синхронно.  
🔹 Полезно, если нужно выполнить критическую секцию кода в одном потоке.

📌 **Пример:**

```kotlin
import kotlinx.coroutines.*

var counterSync = 0

fun main() = runBlocking {
    val jobs = List(100) {
        launch {
            repeat(100) {
                withContext(Dispatchers.Default) {
                    counterSync++ // Операция выполняется последовательно в одном потоке
                }
            }
        }
    }
    jobs.forEach { it.join() }
    println("Final counter value: $counterSync") // Ожидаем 10000
}
```

✅ **Гарантирует, что код выполняется последовательно, но может снизить производительность.**

---

## **4. `Channel` (каналы для передачи данных)**

🔹 Позволяет передавать данные между корутинами без блокировок.  
🔹 Работает по принципу "очереди сообщений".

📌 **Пример использования `Channel`**:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

val channel = Channel<Int>()

fun main() = runBlocking {
    launch {
        for (i in 1..5) {
            channel.send(i) // Отправка данных
            println("Sent $i")
        }
        channel.close() // Закрываем канал
    }

    launch {
        for (msg in channel) {
            println("Received $msg") // Получение данных
        }
    }
}
```

✅ **Позволяет синхронизировать корутины без явного блокирования.**

---

## **5. `Actor` (состояние с одной корутиной)**

🔹 `Actor` – это корутина, которая получает и обрабатывает сообщения последовательно.  
🔹 Полезен, если нужно изменять общее состояние из нескольких корутин.

📌 **Пример использования `Actor`**:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ActorScope
import kotlinx.coroutines.channels.actor

sealed class CounterMsg
object Increment : CounterMsg()
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg()

fun CoroutineScope.counterActor() = actor<CounterMsg> {
    var counter = 0

    for (msg in channel) {
        when (msg) {
            is Increment -> counter++
            is GetCounter -> msg.response.complete(counter)
        }
    }
}

fun main() = runBlocking {
    val counter = counterActor()

    repeat(100) { counter.send(Increment) }

    val response = CompletableDeferred<Int>()
    counter.send(GetCounter(response))
    println("Final counter value: ${response.await()}") // Ожидаем 100

    counter.close()
}
```

✅ **Гарантирует, что изменения состояния выполняются строго последовательно.**

---

### 🎯 **Что выбрать?**

|Метод|Когда использовать?|Плюсы|Минусы|
|---|---|---|---|
|**Mutex**|Если нужен явный контроль доступа к ресурсу|Гарантирует эксклюзивный доступ|Возможны блокировки|
|**Atomic (AtomicInteger, AtomicLong)**|Если нужна быстрая и потокобезопасная инкрементация|Быстро, не требует блокировки|Только для простых операций|
|**withContext(Dispatchers.Default)**|Если нужно синхронное выполнение критической секции|Простота использования|Может замедлить выполнение|
|**Channel**|Для обмена данными между корутинами|Безопасность, гибкость|Требует явного управления каналами|
|**Actor**|Если нужно изменять состояние одной корутиной|Безопасный доступ к данным|Может быть сложнее в понимании|

🚀 **Вывод:**

- Используйте `Mutex`, если требуется строгий контроль доступа к ресурсу.
- Используйте `AtomicInteger`, если нужно потокобезопасное изменение числовых данных.
- Используйте `Channel` или `Actor`, если нужно организовать потокобезопасную обработку данных.
