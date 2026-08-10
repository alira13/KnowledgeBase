## 🔥 **Ключевое слово `synchronized` в Kotlin** 🔥

В многопоточных приложениях важно **избегать состояния гонки** (race condition), когда несколько потоков одновременно изменяют одно и то же значение. Для этого используется **синхронизация**.

В Kotlin **нет встроенного ключевого слова `synchronized`**, как в Java, но есть **функция `synchronized()`**, которая работает аналогично.

---

## **1️⃣ Что делает `synchronized()`?**

Функция `synchronized()` **гарантирует, что только один поток** выполняет код внутри блока в любой момент времени.

### ✅ **Пример: защита общего ресурса**

```kotlin
val lock = Any() // Объект-заглушка для синхронизации
var count = 0

fun increment() {
    synchronized(lock) {
        count++
        println("Count увеличен: $count")
    }
}

fun main() {
    val threads = List(10) {
        Thread { increment() }
    }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
}
```

💡 **Как это работает?**

- `synchronized(lock) { ... }` блокирует `lock`, пока один поток выполняет код внутри блока.
- Другие потоки **ждут** освобождения `lock`, прежде чем зайти в `synchronized()`.

---

## **2️⃣ Как `synchronized` предотвращает состояние гонки?**

Допустим, у нас есть **несинхронизированный код**:

```kotlin
var count = 0

fun increment() {
    count++ // Потоки могут изменять count одновременно
}
```

При одновременном запуске потоков могут происходить **конфликты и потеря данных**, потому что несколько потоков могут **читать и изменять** `count` одновременно.

Использование `synchronized()` решает проблему:

```kotlin
fun increment() {
    synchronized(this) { // Блокировка на уровне объекта
        count++
    }
}
```

---

## **3️⃣ `synchronized` с `lazy` (Singleton)**

Если нужно **создать потокобезопасный Singleton**, можно использовать `synchronized()`:

```kotlin
class Singleton private constructor() {
    companion object {
        @Volatile
        private var instance: Singleton? = null

        fun getInstance(): Singleton {
            return synchronized(this) {
                if (instance == null) {
                    instance = Singleton()
                }
                instance!!
            }
        }
    }
}
```

💡 **Зачем `@Volatile`?**

- `@Volatile` гарантирует, что изменения переменной `instance` будут **видны всем потокам сразу**.

---

## **4️⃣ Альтернативы `synchronized` в Kotlin**

Вместо `synchronized` можно использовать **`ReentrantLock`** или атомарные переменные:

### ✅ **Пример: ReentrantLock**

```kotlin
import java.util.concurrent.locks.ReentrantLock

val lock = ReentrantLock()

fun safeIncrement() {
    lock.lock()
    try {
        count++
    } finally {
        lock.unlock()
    }
}
```

💡 **Когда `ReentrantLock` лучше?**

- Если нужна **гибкость** (например, попытка захвата блокировки `tryLock()`).

### ✅ **Пример: AtomicInteger**

```kotlin
import java.util.concurrent.atomic.AtomicInteger

val atomicCount = AtomicInteger(0)

fun incrementAtomic() {
    atomicCount.incrementAndGet()
}
```

💡 **Когда `AtomicInteger` лучше?**

- Если нужно **изменять число без блокировок** (работает быстрее, чем `synchronized`).

---

## **Вывод**

- **`synchronized()`** предотвращает одновременное выполнение кода разными потоками.
- Работает **по аналогии с `synchronized` в Java**.
- Используется для **потокобезопасного Singleton**.
- **Альтернативы**: `ReentrantLock` (гибкость) и `AtomicInteger` (без блокировок).

💡 **Используй `synchronized()`, если нужно просто и надежно защитить код от гонок потоков!** 🚀