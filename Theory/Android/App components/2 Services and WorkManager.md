# Service и WorkManager

**Service** — компонент без интерфейса для работы, которая должна продолжаться, когда пользователь не смотрит на экран. Это один из четырёх основных компонентов Android.

Главное заблуждение, которое проверяют на собеседовании: **сервис — не поток**. Он работает на **главном потоке** того же процесса. Тяжёлая работа внутри `onStartCommand` без корутины или отдельного потока — это ANR. Сервис отвечает не за параллельность, а за **приоритет процесса**: пока он жив, система реже убивает приложение.

![](<../../images/Pasted image 20250325153105.png>)

## Три вида сервисов
| Вид | Виден пользователю | Когда применять |
| --- | --- | --- |
| **Background** | нет | почти никогда — с Android 8 жёстко ограничен, заменён WorkManager |
| **Foreground** | да, обязательное уведомление | музыка, навигация, запись, звонок — то, что пользователь осознанно запустил |
| **Bound** | нет | сервис как API для других компонентов, живёт пока есть клиенты |

## Background Service и ограничения Android 8
```kotlin
class MyService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null   // null — сервис не связанный

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // работа: обязательно в корутине/потоке, здесь главный поток!
        return START_STICKY
    }
}
```
```xml
<service android:name=".MyService" />
```
```kotlin
startService(Intent(this, MyService::class.java))
stopService(intent)
```

**Что произошло в Android 8 (API 26).** Приложения злоупотребляли фоновыми сервисами: батарея садилась, трафик тратился. Теперь приложение, ушедшее в фон, **не может запускать** обычный фоновый сервис (`IllegalStateException`), а уже запущенный система останавливает через несколько минут. Остались только два законных пути: **Foreground Service** (если пользователь должен знать) и **WorkManager** (если работа может подождать).

### Что возвращает onStartCommand
| Значение | Перезапуск после смерти процесса | Intent при перезапуске | Для чего |
| --- | --- | --- | --- |
| `START_NOT_STICKY` | ❌ | ❌ | разовая задача, потерять не жалко |
| `START_STICKY` | ✅ | ❌ (`null`) | долгая работа без данных запуска: плеер, GPS-трекер |
| `START_REDELIVER_INTENT` | ✅ | ✅ | работа обязана завершиться: загрузка файла |

Ловушка: при `START_STICKY` в пересозданный сервис приходит `intent == null` — код должен это переживать.

## Foreground Service
Отличается тем, что обязан показать уведомление: пользователь видит, что приложение работает.
```kotlin
class MyForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())   // обязательно в течение 5 секунд после старта!
    }

    private fun createNotification(): Notification {
        val channelId = "my_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // с API 26 уведомление только в канале
            val channel = NotificationChannel(channelId, "Foreground Service", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Сервис работает")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}
```
```kotlin
ContextCompat.startForegroundService(this, Intent(this, MyForegroundService::class.java))
```
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />

<service android:name=".MyForegroundService"
         android:foregroundServiceType="location" />
```

Что важно знать по версиям:
- **5 секунд** на вызов `startForeground()` после `startForegroundService()`, иначе система убьёт сервис с `ANR`/`ForegroundServiceDidNotStartInTimeException`.
- **Android 12 (API 31)**: запускать foreground service **из фона нельзя** (за исключениями вроде ответа на high-priority FCM или действия по нажатию уведомления).
- **Android 13 (API 33)**: уведомление показывается только с разрешением `POST_NOTIFICATIONS`.
- **Android 14 (API 34)**: `foregroundServiceType` **обязателен**, и под каждый тип нужно отдельное разрешение (`FOREGROUND_SERVICE_LOCATION`, `_MEDIA_PLAYBACK`, `_DATA_SYNC`…). Тип должен соответствовать реальной работе, иначе приложение не пройдёт ревью Google Play.

## Bound Service
Сервис как API: компоненты подключаются к нему и вызывают методы. Живёт, пока есть хотя бы один клиент, — после отключения последнего уничтожается сам.
```kotlin
class CounterService : Service() {
    inner class LocalBinder : Binder() {
        fun getService(): CounterService = this@CounterService
    }
    private val binder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder = binder

    fun currentValue(): Int = 42
}

// в Activity
private val connection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val bound = (service as CounterService.LocalBinder).getService()
    }
    override fun onServiceDisconnected(name: ComponentName?) {}
}

bindService(Intent(this, CounterService::class.java), connection, Context.BIND_AUTO_CREATE)
// в onStop:
unbindService(connection)
```
Три способа реализации: **Binder** (свой процесс), **Messenger** (очередь сообщений между процессами), **AIDL** (параллельные вызовы между процессами) — см. [[IPC. How two apps communicate]].

## Остановка
- изнутри — `stopSelf()`;
- снаружи — `stopService(intent)`;
- bound-сервис — сам, когда отключился последний клиент.

`onDestroy()` **не гарантирован**: при нехватке памяти или принудительной остановке из настроек система убивает процесс без него. Полагаться на него для сохранения данных нельзя.

## Устаревшее семейство (для понимания вопросов)
| Класс | Что делал | Статус |
| --- | --- | --- |
| `IntentService` | сам уходил в фоновый поток, обрабатывал интенты по очереди, сам останавливался | **deprecated** с API 30 → WorkManager |
| `JobScheduler` + `JobService` | запуск при условиях (зарядка, Wi-Fi, простой) | жив, но это низкий уровень; WorkManager использует его внутри |
| `JobIntentService` | совместимая обёртка над обоими | **deprecated** → WorkManager |
| `AlarmManager` | запуск в **точное время**, обычно через BroadcastReceiver | жив — но только для будильников/напоминаний; с Android 12 точные будильники требуют `SCHEDULE_EXACT_ALARM` |

Смысл `JobService` стоит помнить: `onStartJob` возвращает `true`, если работа продолжается асинхронно (тогда сам вызываешь `jobFinished()`), и `false`, если всё сделано синхронно.

## WorkManager
Библиотека Jetpack — **рекомендованный способ** фоновой работы, которая должна выполниться гарантированно, даже если приложение закрыли или телефон перезагрузили. Внутри сама выбирает механизм (`JobScheduler` или `AlarmManager`), переживает перезагрузку, хранит очередь в своей БД.

Когда её брать: синхронизация, отправка логов, загрузка/выгрузка файлов, периодические задачи. Когда **не** брать: работа, которая нужна прямо сейчас и только пока открыт экран (это корутина во ViewModel), и работа, которую видит пользователь (это foreground service).

### Worker
```kotlin
class MyWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val page = inputData.getInt(PAGE_KEY, 0)      // параметры через inputData
        return try {
            sync(page)
            Result.success(workDataOf("result" to "ok"))
        } catch (e: IOException) {
            Result.retry()                             // повторить по backoff-политике
        } catch (e: Exception) {
            Result.failure()                           // не повторять
        }
    }

    companion object { const val PAGE_KEY = "page" }
}
```
`doWork()` выполняется **на фоновом потоке** — в отличие от сервиса, отдельный поток заводить не нужно. Для корутин есть `CoroutineWorker`, у которого `doWork()` — suspend-функция:
```kotlin
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        repo.sync()
        Result.success()
    }
}
```

### Ограничения и запуск
```kotlin
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.UNMETERED)   // только Wi-Fi
    .setRequiresCharging(true)
    .setRequiresBatteryNotLow(true)
    .build()

val request = OneTimeWorkRequestBuilder<MyWorker>()
    .setInputData(workDataOf(MyWorker.PAGE_KEY to 1))
    .setConstraints(constraints)
    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
    .build()

WorkManager.getInstance(applicationContext).enqueueUniqueWork(
    "sync",
    ExistingWorkPolicy.KEEP,      // KEEP / REPLACE / APPEND / APPEND_OR_REPLACE
    request
)
```
Уникальная работа — важный приём: без неё каждый клик создаст новый воркер, и десять запусков дадут десять параллельных задач.

Периодическая задача:
```kotlin
val periodic = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS).build()
```
Минимальный интервал — **15 минут**, и точное время не гарантируется: система группирует задачи ради батареи.

Цепочки и наблюдение:
```kotlin
WorkManager.getInstance(context)
    .beginWith(compressWorker)
    .then(uploadWorker)          // выполнится, только если предыдущий вернул success
    .enqueue()

workManager.getWorkInfoByIdLiveData(request.id).observe(this) { info ->
    if (info?.state == WorkInfo.State.SUCCEEDED) { /* ... */ }
}
```

Ограничения: `inputData`/`outputData` идут через Bundle и ограничены **~10 КБ** — передавай id, а не данные. Для работы, которую нужно выполнить немедленно и показать пользователю, есть `setExpedited()`.

## Что выбрать
| Задача | Инструмент |
| --- | --- |
| работа, пока открыт экран | корутина во ViewModel |
| пользователь видит и ждёт (музыка, навигация, запись) | Foreground Service |
| должно выполниться, но не срочно (синхронизация, аплоад) | **WorkManager** |
| нужен API у долгоживущего компонента | Bound Service |
| точное время (будильник, напоминание) | AlarmManager |
| межпроцессное взаимодействие | AIDL / Messenger |

## Вопросы-ловушки
- Сервис — это отдельный поток? → нет, главный поток того же процесса; параллельность делаешь сам.
- Почему нельзя запустить фоновый сервис из фона на Android 8+? → ограничения ради батареи; используй WorkManager или foreground service.
- Что придёт в `onStartCommand` при перезапуске с `START_STICKY`? → `intent == null`.
- Сколько времени есть на `startForeground()`? → 5 секунд после `startForegroundService()`, иначе краш.
- Гарантирован ли `onDestroy()` у сервиса? → нет, при убийстве процесса он не вызывается.
- Чем `CoroutineWorker` лучше `Worker`? → `doWork()` — suspend, отмена работы корректно отменяет корутины.
- Какой минимальный интервал у периодической работы? → 15 минут.

## Иллюстрации с собеседования
![](<../../images/Pasted image 20250325154357.png>)
![](<../../images/Pasted image 20250326114919.png>)
![](<../../images/Pasted image 20250326114930.png>)
![](<../../images/Pasted image 20250326114945.png>)
![](<../../images/Pasted image 20250326114959.png>)
![](<../../images/Pasted image 20250326115014.png>)
![](<../../images/Pasted image 20250326115023.png>)
![](<../../images/Pasted image 20250326115034.png>)
![](<../../images/Pasted image 20250326115046.png>)
![](<../../images/Pasted image 20250326115107.png>)

Связано: [[4 Broadcast Receiver]], [[0 App components. Intent]], [[IPC. How two apps communicate]], [[Context]], [[Handler, Looper, MessageQueue]], [[Permissions]]
