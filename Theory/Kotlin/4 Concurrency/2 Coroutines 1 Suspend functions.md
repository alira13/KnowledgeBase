### Suspend

Suspend-функция — это самый главный элемент построения корутин.

 - обычная функция - Поток, выполняющий обычную функцию, блокирует работу других функций до завершения ее выполнения. Если функция будет долго выполняться, например, из-за необходимости получить данные по сети через какой-нибудь внешний API, то это негативно скажется на производительности всей программы. Обычные функции типа Thread.sleep(1000) блокируют поток, не освобождая ресурсы. Другие функции в этот момент не могут выполняться

 - suspend функция - Вызов suspend-функции приостанавливает выполнение функции и позволяет потоку выполнять другие действия. Через некоторое время приостановленная функция может быть возобновлена в том же или другом потоке. Suspend-функции имеют возможность приостановиться, освободить ресурсы, которыми могут пользоваться другие функции и затем продолжить свое выполнение через некоторое время. Пример delay(1000)

Функции, объявленные с ключевым словом `suspend`, преобразуются во время компиляции и становятся асинхронными.

Suspend фнкции должны вызываться из корутины или из другой suspend-функции

Как реализованы suspend-функции? 
- suspend функции под капотом основываются на механизмах callback, но это все скрыто от разработчика
### **Как работают `suspend`-функции под капотом в Kotlin?**

`Suspend`-функции в **Kotlin Coroutines** выглядят как обычные функции, но могут **приостанавливать выполнение** без блокировки потока.

### 🔍 **Как Kotlin компилирует `suspend`-функции?**

При компиляции `suspend`-функции Kotlin превращает их в **state machine (машину состояний)** с использованием **continuation (продолжений)**.

---

## **1. Как Kotlin трансформирует `suspend`-функции**

Допустим, у нас есть `suspend`-функция:

```kotlin
suspend fun fetchData(): String {
    delay(1000)
    return "Data Loaded"
}
```

После компиляции Kotlin превращает её в **обычную функцию с дополнительным параметром**:

```kotlin
fun fetchData(continuation: Continuation<String>): Any {
    return when (continuation.state) {
        0 -> {
            continuation.state = 1
            return delay(1000, continuation) // Приостановка
        }
        1 -> {
            return "Data Loaded" // Возвращает результат после возобновления
        }
        else -> throw IllegalStateException()
    }
}
```

📌 **Ключевые моменты:**  
✔ `continuation.state` хранит текущее состояние функции  
✔ `delay(1000, continuation)` приостанавливает выполнение  
✔ После возобновления выполнение продолжается с `state = 1`

---

## **2. Интерфейс `Continuation<T>` (Продолжение)**

Kotlin использует специальный интерфейс `Continuation<T>` для отслеживания состояния `suspend`-функции:

```kotlin
interface Continuation<T> {
    fun resumeWith(result: Result<T>)
}
```

Когда `suspend`-функция приостанавливается, система **сохраняет текущее состояние и стек**, а затем **продолжает выполнение** с `resumeWith(result)`.

---

## **3. Как работает `delay()` в `suspend`-функции?**

Функция `delay(1000)` **не блокирует поток**. Вместо этого:

1. Она **приостанавливает** выполнение, сохраняя состояние в `Continuation`.
2. Устанавливает таймер (через `Dispatchers`).
3. Когда таймер срабатывает, `resumeWith(Unit)` возобновляет выполнение.

Пример:

```kotlin
suspend fun example() {
    println("Start")
    delay(1000)
    println("End")
}
```

Под капотом:

```kotlin
fun example(continuation: Continuation<Unit>): Any {
    return when (continuation.state) {
        0 -> {
            println("Start")
            continuation.state = 1
            return delay(1000, continuation) // Приостановка
        }
        1 -> {
            println("End")
            return Unit
        }
        else -> throw IllegalStateException()
    }
}
```

---

## **4. Пример: Несколько `suspend`-функций**

Допустим, у нас есть две `suspend`-функции:

```kotlin
suspend fun fetchData(): String {
    delay(1000)
    return "Data"
}

suspend fun processData(): String {
    val data = fetchData()
    return "Processed $data"
}
```

Под капотом они превращаются в **цепочку Continuation**:

```kotlin
fun processData(continuation: Continuation<String>): Any {
    return when (continuation.state) {
        0 -> {
            continuation.state = 1
            return fetchData(continuation) // Вызов suspend-функции
        }
        1 -> {
            val data = continuation.result as String
            return "Processed $data"
        }
        else -> throw IllegalStateException()
    }
}
```

📌 **При каждом `suspend` вызове функция превращается в state machine.**

---

## **5. `suspend` vs. `Thread.sleep()`**

🔹 **`Thread.sleep(1000)`** — блокирует поток (плохо для UI).  
🔹 **`delay(1000)`** — **не блокирует** поток, а **приостанавливает выполнение** и освобождает ресурсы.

---

## **Вывод**

- `suspend`-функции превращаются в **state machine**.
- `Continuation` отслеживает текущее состояние выполнения.
- `delay()` не блокирует поток, а **приостанавливает выполнение**.
- `suspend`-функции могут **возобновляться после остановки**, продолжая с того же места.

### Почему нельзя вызывать suspend не из другой suspend или корутины?

При компиляции `suspend`-функции Kotlin превращает их в **state machine** с параметром `Continuation<T>`.

`suspend fun fetchData(): String`

Компилируется в:

`fun fetchData(continuation: Continuation<String>): Any`

Обычная функция **не знает, как передавать `Continuation`**, поэтому прямой вызов невозможен.









