### **`CoroutineContext` в Kotlin**

**`CoroutineContext`** — это набор параметров, определяющих поведение корутины:

| Компонент              | Описание                                                                                                      |
| ---------------------- | ------------------------------------------------------------------------------------------------------------- |
| **`Dispatcher`**       | Определяет, в каком потоке выполняется корутина (`Dispatchers.Main`, `Dispatchers.IO`, `Dispatchers.Default`) |
| **`Job`**              | Управляет жизненным циклом корутины (можно отменить корутину)                                                 |
| **`CoroutineName`**    | Удобное имя для отладки корутин                                                                               |
| **`ExceptionHandler`** | Обрабатывает исключения внутри корутины                                                                       |

```kotlin
val customContext = Dispatchers.IO + CoroutineName("IO_Coroutine")

launch(customContext) {
    println("Running in ${coroutineContext[CoroutineName]}")
}
```

📌 **Выбирается только один `Dispatcher` (последний добавленный).****
## ✅ **1. `Dispatcher` – Определяет, где выполняется корутина**

Диспетчеры отвечают за то на каком потоке будет выполнятся корутина
Когда создаем свои suspend-функции, они должны быть неблокирующими

Можем создать свой диспатчер на основе executors и передать его в scope
```kotlin
private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()  
private val myScope = CoroutineScope(CoroutineName("My coroutine") + dispatcher)
```
В **Kotlin Coroutines** используются `Dispatchers` для управления потоками, а в **Java** и **Kotlin (до корутин)** для этого использовались `Executors`. Таблица ниже поможет понять, какие `Dispatchers` соответствуют `Executors`.

Все стандартыне dispatchers используют daemon-потоки, то есть как только все основные потоки завершили работу, то и корутина завершается - выход 
 - использовать runBlocking  - заблокирует main поток и не даст ему закончится, пока наша корутина не завершится. ИСПОЛЬЗУЕТСЯ ТОЛЬКО В ТЕСТАХ
 - создать свой dispatcher через Executors.newCachedTreadPool - который использует не daemon потоки

| Диспетчер                    | **Java Executor**                                                                                                                                                                                                                                        | Применение                                                                                                                                                          |
| ---------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`Dispatchers.Main`**       | `Dispatchers.newSingleThreadExecutor`<br>Использует  с только 1 потоком.                                                                                                                                                                                 | Основной поток (для обновления и работы с UI, если в нашей системе он не опеределен, то можно самим создать. Но в Android он определен и используется по умолчанию) |
| **`Dispatchers.IO`**         | `Dispatchers.newCachedThreadPool`. Потоки создаются по мере необходимости. Но есть максимумум в 64 потока                                                                                                                                                | Поток для I/O операций (сетевые запросы, работа с файлами, БД чтение и запись)                                                                                      |
| **`Dispatchers.Default`**    | ``Dispatchers.newFixedThreadPool(число_ядер_ЦП)`<br>Использует пул потоков ограниченной длины. Executor.fixsed с кол-вом потоков=кол-ву ядер. По умолчанию. Нет практически отличия между использованием executorService и корутиной с таким диспатчером | Для тяжёлых вычислений. Используется по умолчанию, если никакой другой при создани scope не был передан                                                             |
| **`Dispatchers.Unconfined`** | `Dispatchers` - не определен. выполняется на том потоке на котором ее создали до первой приостановки, а потом может продолжится на любом потоке                                                                                                          | Не рекомендован к использованию. Самое непредсказуемое поведение.                                                                                                   |

```kotlin
launch(Dispatchers.IO) {  
    val data = fetchData()  // Выполняется в фоновом потоке
}
```
---

## ✅ **2. `Job` – Управление жизненным циклом корутин**

Каждая корутина имеет `Job`, который позволяет управлять её выполнением.

Виды job
 - Job - eсли в одной корутине произошла ошибка, другие остановятся тоже
 - SupervisorJob - не отменяют другие корутины, если в одной произошла ошибка.
 Пример: в мессенджере если при отправке сообщения возникла ошибка, допустим интернет упал, то отменятся и корутины для отсылки нового сообщения, а нам надо иметь возможность что-то написать еще(SuperviserJob надо использовать)

**`Job`** и **`SupervisorJob`** — это два типа объектов, используемых в Kotlin Coroutines для управления жизненным циклом корутин. Основное различие между ними заключается в том, как они обрабатывают ошибки в дочерних корутинах.

```kotlin
val job = launch {
    delay(1000)
    println("Task done")
}
job.cancel() // Отменяем корутину
```

|Аспект|Job|SupervisorJob|
|---|---|---|
|Распределение ошибок|Ошибка в одной корутине отменяет все остальные|Ошибка в одной корутине не влияет на другие|
|Иерархия корутин|Все дочерние корутины равнозначны|Каждая корутина изолирована от других|
|Подходит для|Задач с общей логикой и зависимостями|Независимых задач|

Выбор между `Job` и `SupervisorJob` зависит от конкретных требований к обработке ошибок и отмене задач в вашем приложении.
```kotlin

private val job = Job()  
private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()  
private val exceptionHandler = CoroutineExceptionHandler(  
    { _, _ ->  
        println("Exception:Smth is wrong")  
    })  
private val scope = CoroutineScope(job + dispatcher + exceptionHandler)  
  
private val supervisorJob = SupervisorJob()  
private val supervisorJobScope = CoroutineScope(supervisorJob + dispatcher + exceptionHandler)  
  
fun main() {  
    // вот тут 2 операция отменится, потому что в 1 произошло исключение  
    runOnScope(scope)    
    // вот тут 2 операция закончится  
	runOnScope(supervisorJobScope)  
}  
  
fun runOnScope(scope: CoroutineScope) {  
    scope.launch {  
        longOperation(2000, 1)  
        error("")  
    }  
  
    scope.launch {  
        longOperation(3000, 2)  
    }  
}  
  
suspend fun longOperation(timeMillis: Long, num: Int) {  
    println("Start long operation $num")  
    delay(timeMillis)  
    println("Stop long operation $num")  
}
```
## ✅ **3. `CoroutineName` – Задаёт имя корутины (полезно для отладки)**

```kotlin
val job = launch(CoroutineName("MyCoroutine")) {
    println("Running in MyCoroutine")
}
```

📌 **Позволяет легко отслеживать корутины в логах.**

---

## ✅ **4. `CoroutineExceptionHandler` – Обрабатывает ошибки в корутинах**

Используется для обработки **необработанных исключений** в `launch`.

```kotlin
val handler = CoroutineExceptionHandler { _, exception ->
    println("Caught exception: ${exception.message}")
}

val job = CoroutineScope(Dispatchers.Main).launch(handler) {
    throw RuntimeException("Oops!")
}
```

📌 **Не работает с `async`, так как `async` возвращает `Deferred`, который сам управляет исключениями.**


### Смена контекста
Контекст включает в себя 4 параметра. Можно создать один scope с одним контекстом, но затем определенные участки кода запускать с другим контекстом. Менять диспатчер, имя, родительскую job или ExeptioтHandler(не для дочерних корутин)
## 🔹 **1. Использование `withContext`**

| Способ                                 | Когда использовать?                                                   |
| -------------------------------------- | --------------------------------------------------------------------- |
| `withContext`                          | Когда нужно временно сменить контекст без создания новой корутины     |
| `launch(Dispatchers.X)`                | Когда нужно запустить новую корутину в другом контексте или передать  |
| `async(Dispatchers.X)`                 | Когда нужно запустить новую корутину в другом контексте с результатом |
| `CoroutineScope(Dispatchers.X)`        | Когда нужно управлять группой корутин в одном контексте               |
| `newSingleThreadContext("ThreadName")` | Когда нужен отдельный поток (например, для блокирующих операций)      |



