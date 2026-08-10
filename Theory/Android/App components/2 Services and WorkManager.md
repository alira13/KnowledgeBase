
В **Android** сервисы (**Service**) — это компоненты, предназначенные для выполнения длительных операций в фоновом режиме без взаимодействия с пользователем. Они продолжают работу даже если пользователь закрыл приложение.

![](<../../images/Pasted image 20250325153105.png>)

### **Виды сервисов в Android**

##### 1. Background Service - без уведомления пользователя через оповещение

- **Описание**: Это сервисы, которые работают в фоновом режиме без прямого взаимодействия с пользователем. Они не отображают уведомления и не требуют внимания пользователя.
- **Ограничения**: Начиная с API уровня 26, Background Service имеют ограничения на выполнение задач, особенно когда приложение не находится в фокусе. Они могут быть остановлены системой, если приложение не активно[4](https://www.microsin.net/programming/android/android-services-overview.html).
- **Примеры использования**: Сжатие данных, очистка кэша и другие задачи, которые не требуют прямого взаимодействия с пользователем.

Проблема: из-за злоупотребления запуском таких сервисов во многих приложениях у пользователя очень быстро стала садиться зарядка или начал тратиться интернет, поэтому ввели ограничения с версии API уровня 26.

На **Background Service** в Android существуют несколько ограничений, введенных для оптимизации работы системы и экономии батареи:

1. **Ограничения на запуск**: Начиная с Android 8.0 (API уровень 26), Background Service не могут быть запущены, если приложение не активно. Если попытаться запустить обычный Background Service, когда приложение находится в фоне, система может остановить его или вызвать ошибку[3](https://itsobes.com/ru/android/kakie-ogranicheniia-sviazannye-s-fonovymi-servisami-byli-dobavleny-v-android-8-0/)[6](https://developer.android.com/about/versions/oreo/background).
2. **Ограничения на выполнение**: Background Service могут быть остановлены системой через несколько минут после того, как пользователь покидает приложение. Это связано с тем, что система пытается оптимизировать использование ресурсов и экономить заряд батареи[3](https://itsobes.com/ru/android/kakie-ogranicheniia-sviazannye-s-fonovymi-servisami-byli-dobavleny-v-android-8-0/)[4](https://dzen.ru/a/Zn6sz1i17iuehb60).
3. **Ограничения на данные**: Пользователи могут ограничить передачу данных в фоновом режиме для приложений, что может повлиять на работу Background Service, требующих сетевого доступа[1](https://www.samsung.com/ru/support/mobile-devices/how-to-restrict-background-data-for-apps/).
4. **Рекомендации по использованию**: Вместо Background Service рекомендуется использовать **Foreground Service** для задач, которые должны выполняться постоянно, или **WorkManager** для планирования фоновой работы. Это позволяет избежать ограничений и обеспечивает более стабильное выполнение задач[3](https://itsobes.com/ru/android/kakie-ogranicheniia-sviazannye-s-fonovymi-servisami-byli-dobavleny-v-android-8-0/)[4](https://dzen.ru/a/Zn6sz1i17iuehb60).
5. **Ограничения на доступ к ресурсам**: Приложения в фоне имеют ограниченный доступ к определенным ресурсам, таким как микрофон и камера, что может повлиять на работу сервисов[2](https://habr.com/ru/companies/broadcast/articles/734236/).
В какой-то момент поняли, что очень много злоупотреблений использования сервисов, когда они постоянно работают и у пользователей начала сильно разряжаться батарея. 

![](<../../images/Pasted image 20250325154357.png>)

##### 2. Foreground Service - с уведомлением

- **Описание**: Это сервисы, которые работают в фоновом режиме, но о которых пользователь осведомлен через уведомления в системной панели. Они требуют специального разрешения (`android.permission.FOREGROUND_SERVICE`) и должны быть явно объявлены в манифесте приложения[5](https://kmm.icerock.dev/learning/android/service).
- **Преимущества**: Foreground Service имеют больший приоритет и менее вероятно будут остановлены системой. Они подходят для задач, которые требуют постоянного взаимодействия с пользователем или должны выполняться без прерывания, таких как проигрывание музыки или отслеживание местоположения[3](https://itsobes.com/ru/android/chto-takoe-background-i-foreground-service/)[5](https://kmm.icerock.dev/learning/android/service).
- **Запуск**: Для запуска Foreground Service используется метод `startForegroundService()`, после которого необходимо вызвать `startForeground()` в течение 5 секунд, чтобы указать уведомление, которое будет отображаться пользователю[1](https://habr.com/ru/articles/773228/)[2](https://dzen.ru/a/Zn6sz1i17iuehb60).

##### 3. Bound Service
- **Foreground Service** — нужен для важных задач (музыка, навигация).
- **Background Service** — используется редко, предпочтительно WorkManager.
- **Bound Service** — подходит для взаимодействия с активностью.

#### **1. Background Service **

Для создания сервиса нужно унаследоваться от `Service` и переопределить `onStartCommand()`:

```kotlin
class MyService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null // Используется, если сервис не связанный
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Код выполнения фоновой задачи
        Log.d("MyService", "Сервис запущен")
        return START_STICKY // Автоперезапуск при завершении процесса
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyService", "Сервис уничтожен")
    }
}
```

**Добавление в `AndroidManifest.xml`:**

```xml
<service android:name=".MyService"/>
```

**Запуск и остановка сервиса:**

```kotlin
// Запуск
val intent = Intent(this, MyService::class.java)
startService(intent)

// Остановка
stopService(intent)
```

---
Метод `onStartCommand()` в сервисе (`Service`) возвращает одно из следующих значений, определяя, как система должна себя вести в случае завершения сервиса

|Возвращаемое значение|Перезапуск после завершения?|Передаётся ли `Intent` при перезапуске?|Использование|
|---|---|---|---|
|**`START_NOT_STICKY`**|❌ Нет|❌ Нет|Короткие фоновые задачи, которые не требуют перезапуска (например, одноразовая проверка обновлений)|
|**`START_STICKY`**|✅ Да|❌ Нет|Длительные задачи, работающие в фоне (например, музыкальный плеер, GPS-трекер)|
|**`START_REDELIVER_INTENT`**|✅ Да|✅ Да|Важные задачи, которые должны завершиться даже после завершения процесса (например, загрузка файла)|
|**`START_FLAG_REDELIVERY`**|✅ Да (аналогично `START_REDELIVER_INTENT`)|✅ Да|Аналог `START_REDELIVER_INTENT`, но передаётся через `flags`|
Если сервис должен **обязательно закончить выполнение своей задачи**, лучше использовать **`START_REDELIVER_INTENT`**.  
Если сервис работает постоянно (например, музыка или GPS), лучше **`START_STICKY`**.

☝️ В Android 8+ предпочтительно использовать **ForegroundService** или **WorkManager** вместо `Service`.

#### **2. Foreground Service( с уведомлением)**
Отличие только в том что в onCreate надо вызвать startForeground(1, createNotification())

```kotlin
class MyForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification()) // Запуск переднего сервиса
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }

    // c 26 версии нужно показывать уведомление при запуске сервиса
    private fun createNotification(): Notification {
    // c 26 версии нужно показывать уведомление в определенном канале
        val channelId = "my_channel"
        val channelName = "Foreground Service Channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Service")
            .setContentText("Сервис работает...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}
```

**Добавление в `AndroidManifest.xml`:**

```xml
<service
    android:name=".MyForegroundService"
    android:foregroundServiceType="location"/>
```

**Запуск сервиса:**

```kotlin
val serviceIntent = Intent(this, MyForegroundService::class.java)
startService(serviceIntent)
```

---

#### Остановка сервисов
иногда сервисы необходимо остановить вручную, чтобы вызвался OnDestroy и уведомление можно было удалить. Можно остановить сервис
 - изнутри(внутри самого сервиса) stopSelf
 - снаружи например в Activity StopService(intent)
 Однако, если вы явно вызываете `stopSelf()` внутри сервиса после выполнения его задачи, `onDestroy()` должен быть вызван независимо от продолжительности работы сервиса. Если же сервис завершается без явного вызова `stopSelf()`, поведение может варьироваться в зависимости от конкретной ситуации.
 
Если работа сервиса длится менее 5 секунд, метод `onDestroy()` может не вызываться по нескольким причинам:
1. **Принудительная остановка сервиса**: Если пользователь или система принудительно останавливает сервис через настройки устройства, `onDestroy()` может не вызываться[7](https://stackoverflow.com/questions/26058642/ondestroy-of-a-service-is-never-called).
2. **Нехватка памяти**: Если система испытывает нехватку памяти, она может убить сервис без вызова `onDestroy()`, чтобы освободить ресурсы[1](https://ru.stackoverflow.com/questions/267428/%D0%90%D0%BD%D0%B4%D1%80%D0%BE%D0%B8%D0%B4-%D0%9D%D0%B5-%D0%B2%D1%8B%D0%B7%D0%B2%D1%8B%D0%B2%D0%B5%D1%82%D1%81%D1%8F-ondestroy)[2](https://www.cyberforum.ru/android-dev/thread1833149.html).
3. **Системные ограничения**: В некоторых случаях система может не вызывать `onDestroy()` из-за внутренних ограничений или ошибок.
4. **Неправильное использование сервиса**: Если сервис не был правильно остановлен с помощью `stopService()` или `stopSelf()`, он может продолжать работать без вызова `onDestroy()`[3](https://startandroid.ru/ru/uroki/vse-uroki-spiskom/157-urok-92-service-prostoj-primer.html).
5. **Фоновые ограничения**: Начиная с Android 8.0 (API-26), фоновые сервисы имеют ограничения, и их поведение может отличаться от ожидаемого.

### Intent Service(для решения проблем обычного Service) выполняется в фоновом потоке
Проблемы класса Service
1. выполняется на главном потоке и нам нужно самим запускать в корутине
2. не останавливается сам - следить за остановкой
3. если хотим чтобы работал 1 сервис, то надо реализовать самостоятельно, потому что по умолчанию каждый раз по клику кнопки будет запускаться сервис новый
Решение IntentService(старый но используется все еще)
Нужно отнаследоваться от IntentService и переопределить onHandleIntent, а так это обычный сервис

### Job service и JobScedulaer(для запуска Job service) - для установки ограничений на запуск

Работает когда выполняются определенные условия
Допустим обновление системы-работает когда телефон подключен к зарядке чтобы не разрядить телефон в неудобное время или допустим в определенное время ночью
Загрузка данных происходит допустим только когда подключен к WiFi
Создание
1. наследуем от jobService
2. переопределяем методы onStartJob on onStopJob
```kotlin
// не забудь добавить в Manifest иначе не заработает  
// Создание  - наследуемся от JobService  
class MyJobService : JobService() {  
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)  
  
    override fun onCreate() {  
        super.onCreate()  
        showLog("onCreate")  
    }  
  
    // также на главном потоке выполняется  
    // выызвается при старте сервиса, в нем выполняется вся работа    override fun onStartJob(p0: JobParameters?): Boolean {  
        showLog("onStartJob")  
        //createNotification()  
        scope.launch {  
            for (i in 0..100) {  
                showLog(i.toString())  
                delay(1000L)  
            }  
            // если return true то вручную останавливаем  
            // если после НОРМАЛЬНОГО завершения нужно перезапустить сервис то jobFinished(true)            jobFinished(p0, true)  
        }  
        //СЕРВИС ЕЩЕ ВЫПОЛНЯЕТСЯ?  
        // ДА(true) - потому что мы выполняем асинхронные опреации и они не завершаются сами,        // мы завершим их когда сами решим. Кароче требуется ли остановить принудительно или нет(сама остановится)        // НЕТ - когда мы выполняем последовательные операции синхронно и они завершаются сами        return true  
    }  
  
    // выполняется когда  СИСТЕМА убила наш сервис(не мы сами)  
    // если после СИСТЕМНОГО убиения сервиса нужно перезапустить сервис то return true    override fun onStopJob(p0: JobParameters?): Boolean {  
        showLog("onStopJob")  
        return true  
    }  
  
    override fun onDestroy() {  
        super.onDestroy()  
        showLog("onDestroy")  
        scope.cancel()  
    }  
  
    private fun showLog(str: String) {  
        Log.d("MY_SERVICE", "MyService: $str")  
    }  
}
```
Запуск
```kotlin
btn4.setOnClickListener {  
    // компонент в котором мы указываем наш класс сервиса  
    val component = ComponentName(this, MyJobService::class.java)  
    // тут мы описываем условия по которым будем стартовать сервис  
    val jobInfo = JobInfo.Builder(111, component)  
        // только на зарядке  
        .setRequiresCharging(true)  
        // только с wifi  
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)  
        .build()  
    // получаем планировщика и запускаем с условиями  
    val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler  
    jobScheduler.schedule(jobInfo)  
}
```
Иногда нужно возобновить работу сервиса с какой-то точки, для этого используется метод enqueue - с помощью него можно накидывать задачи в очередь и если система убьет сервис, то задачи возобновятся не с первой а с последней выполненной(работает с версии 26) Если версия ниже 26, то можно заменить на intentService

### JobIntentService  = IntentService+JobService для всех версий вместо ><26 - выполняется в фоновом потоке
использует под каппотом 2 вида сервислв чтобы возобновлять работу сервиса с какой-то точки
для версий меньше 26 - intent
для версий больше 26  - job
Чтобы не реализовывать 2 вида сервисов для разных версий. Но нельзя настраивать info c описанием ситуаций когда возобновлять работу(наличие wifi и тд)
#### **3. Bound Service (Связанный сервис)**

В Android **Bound Service** – это сервис, который позволяет клиентам (например, Activity, Fragment или другой компонент) связываться с ним и взаимодействовать через интерфейс. Такой сервис работает в фоне, но при этом предоставляет API для взаимодействия с ним.

### 🔹 **Основные особенности Bound Service**

1. **Работает, пока к нему привязан клиент**  
    – Когда все клиенты отключаются, сервис автоматически уничтожается.
2. **Используется для обмена данными**  
    – Позволяет передавать данные между сервисом и клиентами.
3. **Поддерживает многопоточное взаимодействие**  
    – Можно организовать работу нескольких клиентов одновременно.

### 🔹 **Как создать Bound Service?**

Для создания bound-сервиса нужно:

1. Унаследоваться от `Service` и переопределить `onBind()`.
2. Определить интерфейс взаимодействия (например, через `Binder`).
3. Подключить сервис к клиенту с помощью `bindService()`.

### 🔹 **Пример кода**

📌 **Создание сервиса**

```java
public class MyBoundService extends Service {
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        MyBoundService getService() {
            return MyBoundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public String getData() {
        return "Hello from Bound Service!";
    }
}
```

📌 **Привязка сервиса в Activity**

```java
public class MainActivity extends AppCompatActivity {
    private MyBoundService myService;
    private boolean isBound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBoundService.LocalBinder binder = (MyBoundService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
            Toast.makeText(MainActivity.this, myService.getData(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyBoundService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
```

### 🔹 **Способы реализации Bound Service**

1. **Через `Binder` (как в примере выше)**  
    – Простой способ для работы в одном процессе.
    
2. **Через `Messenger`**  
    – Подходит, если сервис и клиент работают в разных процессах.
    
3. **Через AIDL (Android Interface Definition Language)**  
    – Используется для сложного взаимодействия между разными процессами.
    

### 🔹 **Когда использовать Bound Service?**

✅ Если нужно предоставить компонентам API для взаимодействия.  
✅ Когда сервис нужен только во время активности клиентов.  
✅ При необходимости работы с удалёнными процессами (через AIDL).

Если сервис должен работать даже после закрытия приложения, лучше использовать **Started Service** (`startService()`).

#### AlarmManager
Если хотиим запланировать выполнение какой-то задачи в будущем. Точное время для выполнения задачи
Как правило работает с broadcast receiver
### WorkManager
 - простой в реализации
 - работает с Api14
 - не нужно регистрировать в манифесте

класс из AndroidJetPack который пришел на смену сервисам так как с сервисами перестало удобно работать. теперь любую работу можно сделать с помощью него

Создание
```kotlin
  
class MyWorker(context: Context, private val workerParams: WorkerParameters) :  
    Worker(context, workerParams) {  
    // выполняется в main потоке и не блокирует его. Не нужно самим беспокоиться и стартовать корутину  
    override fun doWork(): Result {  
        showLog("doWork")  
        // раличные параметры передаются через WorkerParameters.inputData  
        val page = workerParams.inputData.getInt(PAGE_KEY, 0)  
        for (i in 0..3) {  
            showLog(i.toString())  
            Thread.sleep(1000L)  
        }  
  
        return Result.success()  
        // если исключение или что-то пошло не так Result.success() или Result.retry  
    }  
  
    private fun showLog(str: String) {  
        Log.d("MY_SERVICE", "MyWorker: $str")  
    }  
  
    companion object {  
        const val PAGE_KEY = "page"  
        const val WORKER_NAME = "My worker"  
  
        fun createRequest(page: Int): OneTimeWorkRequest {  
            return OneTimeWorkRequestBuilder<MyWorker>()  
                // передаем данные в сервис  
                .setInputData(workDataOf(PAGE_KEY to page))  
                // выставляем ограничения  
                .setConstraints(makeConstraints())  
                .build()  
        }  
  
        private fun makeConstraints(): Constraints {  
            return Constraints.Builder().build()  
        }  
    }  
}
```
Запуск

```kotlin
btn6.setOnClickListener {  
    val workManager = WorkManager.getInstance(applicationContext)  
    // создастся 10 workers и все они будут выполнятьтся  
    //workManager.enqueue()    // в 1 время работает 1 воркер. Передаем параметр что делать если какой-то воркер был запущен    // и мы пытаемся запустить новый    workManager.enqueueUniqueWork(  
        MyWorker.WORKER_NAME,  
        ExistingWorkPolicy.APPEND,  
        MyWorker.createRequest(page++)  
    )  
}
```

Режимы работы сервисов

Сервисы могут работать в двух режимах: started и bound.
- В первом случае просто запускаем сервис через context.startService(intent), где передаем явный или неявный Intent. Завершаем с помощью context.stopService(intent).
- Во втором режиме несколько разных компонентов могут подключиться к сервисы через context.bindService(intent, serviceConnection, flag), где тоже передаем явный или неявный интент. И вот после такого подключения компоненты могут взаимодействовать с сервисом через serviceConnection и разорвать связать через context.unbindService(serviceConnection). Сервис автоматически удаляется, когда последний компонент разорвет связь.

Собеседование
![](<../../images/Pasted image 20250326114919.png>)
![](<../../images/Pasted image 20250326114930.png>)
![](<../../images/Pasted image 20250326114945.png>)![](<../../images/Pasted image 20250326114959.png>)

![](<../../images/Pasted image 20250326115014.png>)

![](<../../images/Pasted image 20250326115023.png>)
![](<../../images/Pasted image 20250326115034.png>)
![](<../../images/Pasted image 20250326115046.png>)
![](<../../images/Pasted image 20250326115107.png>)
![](<../../images/Pasted image 20250326115122.png>)![](<../../images/Pasted image 20250326115142.png>)