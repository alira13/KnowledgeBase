Паттерн **Команда** (Command) — это поведенческий шаблон проектирования, который инкапсулирует запросы в виде объектов, позволяя управлять ими как независимыми сущностями. Это обеспечивает гибкость при организации очередей команд, отмены операций и логирования действий.

В паттерне Команда все задачи - это объекты реализующие интерфейс Command с методом execute. Если нам нужно управлять вызовом команд как-то по особенному, допустим вызывать их в другом потоке по очереди или в зависимости от условий то мы опишем в конкретной реализации Invoker эту логику

 
## Основные компоненты

1. **Receiver** — объект c функциями, которые мы хотим вызывать по какому-то принципу. Он содержит просто описание действия, функции без всяких деталей как мы должны их вызывать. В другом потоке или по очереди или еще как-то. 
2. **Command** — интерфейс с методом `execute()` (и `undo()` для отмены) - представление команды в виде интерфейса
```kotlin
// любая команда, которую должен выполнить invoker  
fun interface Command {  
    fun execute()  
}
```
3. **ConcreteCommand** — реализация интерфейса Command. Набор объектов, которые получают в параметры ресивера и в execute вызывают команду ресивера. 
```kotlin
sealed interface DeviceCommand : Command {  
    class LightOnCommand(private val light: Light) : DeviceCommand {  
        override fun execute() {  
            light.turnOn()  
        }  
    }  
  
    class LightOffCommand(private val light: Light) : DeviceCommand {  
        override fun execute() {  
            light.turnOff()  
        }  
    }  
  
    class TVOnCommand(private val tv: TV) : DeviceCommand {  
        override fun execute() {  
            tv.turnOn()  
        }  
    }  
  
    class TVOffCommand(private val tv: TV) : DeviceCommand {  
        override fun execute() {  
            tv.turnOff()  
        }  
    }  
  
    class TVChangeChannelCommand(private val tv: TV, private val channel: Int) : DeviceCommand {  
        override fun execute() {  
            tv.changeChannel(channel)  
        }  
    }  
}
```
4. **Invoker** — вызывающий команды по какой-то логике или принципу.
```kotlin
// каким именно образом мы будем выполнять команды. Вытаскивать из очереди или по каким-то условиям  
interface Invoker<T : Command> {  
    fun executeCommand(command: T)  
}
```
5. **Реализация invoker** - инкапсулирует логику вызова команд
```kotlin
// вот тут в реализации и описываем по какой логике хотим исполнять команды. В данном случае класть  
// в очередь и потом вытаскивать первую в очереди и выполнять  
class RemoteControl : Invoker<DeviceCommand> {  
    //тип данных, который блокирует поток, пока очередь пуста  
    // когда в очереди что-то появляется, take берет первый элемент    
    // и удаляет его    
    private val commands = LinkedBlockingQueue<DeviceCommand>()  
  
    override fun executeCommand(command: DeviceCommand) {  
        thread {  
            commands.add(command)  
            while (true) {  
                //println("Waiting...")  
                val existedCommand = commands.take()  
                //println("Executing...$existedCommand")  
                existedCommand.execute()  
                //println("Executed...$existedCommand")  
            }  
        }  
    }  
}
```
6. **Client** — тот кто вызывает методы invoker в каком-то месте вместе со всей остальной логикой
```kotlin
// Client - просто вызывает методы Invoker  
fun runCommandTest() {  
    val light = Light()  
    val tv = TV()  
    val ac = AirConditioner()  
    val remote = RemoteControl()  
  
    remote.executeCommand(DeviceCommand.LightOnCommand(light))  
    remote.executeCommand(DeviceCommand.TVOnCommand(tv))  
    remote.executeCommand(DeviceCommand.TVChangeChannelCommand(tv, 5))  
    remote.executeCommand(DeviceCommand.AirConditionerOnCommand(ac))  
    remote.executeCommand(DeviceCommand.AirConditionerSetTempCommand(ac, 22))  
    remote.executeCommand(DeviceCommand.LightOffCommand(light))  
    remote.executeCommand(DeviceCommand.TVOffCommand(tv))  
    remote.executeCommand(DeviceCommand.AirConditionerOffCommand(ac))  
}  
  
fun main(){  
    runCommandTest()  
}
```

## В Android SDK
- **`Runnable`** — самая чистая команда: объект с методом `run()`, который кто-то другой решает когда и где выполнить (`Handler.post`, `Executor.execute`).
- **`Message`** в `Handler`/`Looper` — команда в очереди, с временем выполнения (`sendMessageDelayed`). См. [[Handler, Looper, MessageQueue]].
- **`WorkRequest`** — команда, поставленная в очередь с ограничениями и повторами. См. [[2 Services and WorkManager]].
- **`Intent`** — запрос к системе, оформленный объектом: его можно передать, отложить (`PendingIntent`) и выполнить позже от твоего имени. См. [[0 App components. Intent]].
- **MVI**: `Action`/`Intent` экрана — тот же паттерн, каждое действие пользователя описано объектом, что даёт лог и воспроизводимость.

## Command в Kotlin
Отдельный класс под каждую команду часто не нужен: функция — уже объект.
```kotlin
val commands: List<() -> Unit> = listOf(
    { light.on() },
    { tv.off() }
)
commands.forEach { it() }
```
Классы остаются оправданы, когда команде нужны **имя, undo и сериализация** — например, для истории операций или отправки на сервер.

## Вопросы-ловушки
- Зачем оборачивать вызов в объект? → чтобы им можно было управлять как данными: складывать в очередь, откладывать, логировать, отменять, повторять.
- Чем Command отличается от Strategy? → обе прячут поведение за интерфейсом, но Command описывает **что сделать** (один запрос, часто с undo), Strategy — **как сделать** (взаимозаменяемый алгоритм).

Связано: [[GoF patterns]], [[Handler, Looper, MessageQueue]], [[2 Services and WorkManager]], [[0 App components. Intent]], [[Comparing MVC, MVP, MVVM, MVI]]

