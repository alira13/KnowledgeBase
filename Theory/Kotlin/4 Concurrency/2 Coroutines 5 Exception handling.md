1. Обернуть не саму корутину(поскольку сам запуск проииходит успешно), а именно код внутри корутины в блок try-catch
2. runCatching - принимает на вход лямбду, которую оборачивает под капотом в try-catch
3. если в корутине все-таки было выброшено исключение и оно не было обработано, оно поднимается в иерархии к родительскому job. Обработано оно в родительском или нет, все дочерние корутины будут отменены. Разница лишь в том, что увидет пользователь.
4. Чтобы обработать в родительском job исключение, можно создать и передать объект ExceptionHandler в Context
5. При переключении контекста через передачу параметров launch в ДОЧЕРНИЕ корутины можно передавать все кроме ExeptionHandler - он проигнорируется
6. Вне зависимости от корутин-билдера исключение все равно работает одинаково, оно происходит и если не обработано то поднимается наверх к родительской корутине
```kotlin
package com.example.multithreading.exceptionHandler  
  
import kotlinx.coroutines.CoroutineExceptionHandler  
import kotlinx.coroutines.CoroutineScope  
import kotlinx.coroutines.asCoroutineDispatcher  
import kotlinx.coroutines.delay  
import kotlinx.coroutines.launch  
import java.util.concurrent.Executors  
  
val exceptionHandler =  
    CoroutineExceptionHandler { _,  
                                _ ->  
        println("Parent:FAIL")  
    }  
val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()  
val scope = CoroutineScope(dispatcher + exceptionHandler)  
  
fun main() {  
    exceptionTask()  
    exceptionRunCatchingTask()  
    someTask()  
    taskWithException()  
}  
  
fun exceptionTask() {  
    scope.launch {  
        try {  
            println("exceptionTask")  
            delay(2000)  
            throw RuntimeException("My exception")  
        } catch (ex: Exception) {  
            println("Something is going wrong")  
        }  
    }  
}  
  
// тоже самое что и try-catch  
fun exceptionRunCatchingTask() {  
    scope.launch {  
        runCatching {  
            println("exceptionRunCatchingTask")  
            delay(3000)  
            throw RuntimeException("My exception")  
        }  
            .onSuccess { println("OK") }  
            .onFailure { println("FAIL") }  
    }}  
  
fun someTask() {  
    scope.launch {  
        delay(10000)  
        println("someTask")  
    }  
}  
  
fun taskWithException() {  
    scope.launch {  
        println("taskWithException")  
        delay(5000)  
        throw RuntimeException("My exception")  
    }  
}
```

Обновлённая таблица с добавлением случая `withContext`:

| Метод                       | Работает с `launch` | Работает с `async`                                                                                 | Прерывает другие корутины       | Применение                                                                           |
| --------------------------- | ------------------- | -------------------------------------------------------------------------------------------------- | ------------------------------- | ------------------------------------------------------------------------------------ |
| `try-catch` внутри корутины | ✅                   | ✅                                                                                                  | Да, если нет `supervisorScope`  | Локальная обработка исключений внутри корутины                                       |
| `CoroutineExceptionHandler` | ✅                   | ❌<br>async возвращает объект Derefered. Чтобы мы увидели исключение, нужно распаковать через await | Да, если нет `supervisorScope`  | Централизованная обработка исключений в `launch`                                     |
| `supervisorScope`           | ✅                   | ✅                                                                                                  | ❌ (не отменяет другие корутины) | Позволяет дочерним корутинам продолжать работу при сбое одной из них                 |
| `SupervisorJob()`           | ✅                   | ✅                                                                                                  | ❌ (не отменяет другие корутины) | Используется в `CoroutineScope` для управления иерархией корутин без массовой отмены |


![](<../../images/Pasted image 20250305094554.png>)Если у нас возникает исключение в блоке async, который обернут в Launch, то async вернет defered, Launch распакует его и наверх отправит уже исключение

Если у нас есть rootHandler - при создании scope
Мы его в launch меняем на ParentHandler - он будет обрабатывать исключения
Но если мы в дочерние отправим childHandler в аргументах launch или через withContext, то это никакого эффекта не сыграет. То есть в дочерних корутинах обрабатываем только через tryCatch или runCatching