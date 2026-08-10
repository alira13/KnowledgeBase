# BroadcastReceiver

**BroadcastReceiver** — компонент, который принимает широковещательные сообщения (broadcast). Система, твоё приложение или чужое приложение рассылают события — «заряд батареи низкий», «включён режим полёта», «телефон загрузился», — а receiver ловит те, на которые подписан.

Это один из четырёх основных компонентов Android. Модель — «издатель-подписчик»: отправитель не знает получателей, получатель фильтрует сообщения по `action` через `IntentFilter`.

## Создание
Наследуемся от `BroadcastReceiver` и разбираем `intent.action`:
```kotlin
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                val turnOn = intent.getBooleanExtra("state", false)   // данные положила система
                Toast.makeText(context, "Режим полёта: $turnOn", Toast.LENGTH_SHORT).show()
            }
            Intent.ACTION_BATTERY_LOW ->
                Toast.makeText(context, "Низкий заряд", Toast.LENGTH_SHORT).show()
        }
    }
}
```

## Два способа регистрации

### Динамическая (предпочтительная)
Receiver живёт, пока зарегистрирован, и работает только когда приложение запущено.
```kotlin
class MainActivity : AppCompatActivity() {
    private val receiver = MyBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)     // обязательно, иначе утечка
    }
}
```
С **Android 13 (API 33)** при регистрации приёмника не-системных broadcast'ов **обязательно** указывать флаг `RECEIVER_EXPORTED` или `RECEIVER_NOT_EXPORTED` — иначе `SecurityException`. По умолчанию выбирают `NOT_EXPORTED`: принимать только свои сообщения.

Регистрировать/отменять симметрично: `onCreate`/`onDestroy` либо `onStart`/`onStop`. Забыл `unregisterReceiver` — утечка активити.

### Статическая (в манифесте)
```xml
<receiver android:name=".MyBroadcastReceiver" android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```
Работает, пока приложение установлено, даже если оно не запущено. **Но с Android 8 (API 26)** статически зарегистрированные приёмники **не получают большинство неявных broadcast'ов** — это сделано ради батареи, чтобы событие не будило десятки приложений сразу.

Исключения, которые всё ещё приходят: `BOOT_COMPLETED`, `LOCALE_CHANGED`, `MY_PACKAGE_REPLACED`, события SMS и некоторые другие из белого списка. Явные broadcast'ы (адресованные конкретному компоненту) приходят всегда.

Практический вывод: статическая регистрация сегодня — почти только для «сделать что-то после перезагрузки телефона», и то работу оттуда планируют через WorkManager.

## Свои события
```kotlin
// отправка
val intent = Intent(MyBroadcastReceiver.ACTION_CLICKED).apply {
    putExtra(MyBroadcastReceiver.ACTION_CLICK_NUM, clickNum++)
    setPackage(packageName)          // ограничить своим приложением
}
sendBroadcast(intent)

// приём
ACTION_CLICKED -> {
    val num = intent.getIntExtra(ACTION_CLICK_NUM, 1)
    Toast.makeText(context, "Клик №$num", Toast.LENGTH_SHORT).show()
}
```
`setPackage()` превращает неявный broadcast в адресный — иначе событие увидят все установленные приложения.

## Ограничения onReceive
- **Выполняется на главном потоке** — тяжёлые операции вызовут ANR. Лимит на выполнение ~10 секунд, дальше система считает приёмник зависшим.
- Нужна долгая работа — не запускай поток из `onReceive`: после выхода из метода процесс могут убить. Правильно — `goAsync()` для короткой доработки или (чаще) поставить задачу в **WorkManager**:
```kotlin
override fun onReceive(context: Context, intent: Intent) {
    WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<SyncWorker>().build())
}
```
- Контекст в `onReceive` **ограниченный**: нельзя показывать диалог и нельзя вызывать `bindService()`. См. [[Context]].

## LocalBroadcastManager — устарел
Использовался для сообщений внутри приложения (безопаснее и быстрее системных broadcast'ов), но **признан deprecated**: это глобальная шина событий, которая размывает архитектуру и обходит жизненный цикл.

Чем заменять сегодня:
| Задача | Решение |
| --- | --- |
| события между слоями приложения | `SharedFlow`/`StateFlow` в общем репозитории или ViewModel |
| результат от фрагмента | Fragment Result API |
| фоновая работа по событию | WorkManager |
| события между приложениями | обычный broadcast с разрешениями или ContentProvider |

## Безопасность
- `android:exported="true"` без проверки прав — любое приложение сможет прислать твоему receiver'у что угодно; проверяй `action` и данные.
- Отправка чувствительных данных обычным `sendBroadcast` — их прочитают все; ограничивай `setPackage()` или собственным `permission`.
- **Ordered broadcast** (`sendOrderedBroadcast`) доставляется по очереди с учётом приоритета, и получатель может прервать цепочку (`abortBroadcast()`) — исторически так перехватывали SMS.

## Вопросы-ловушки
- Почему статический receiver не получает событие на Android 8+? → ограничение на неявные broadcast'ы ради экономии батареи; работают только события из белого списка и явные интенты.
- На каком потоке вызывается `onReceive`? → на главном; долгая работа → ANR, поэтому WorkManager или `goAsync()`.
- Что будет, если не вызвать `unregisterReceiver`? → утечка контекста активити и лишняя работа после закрытия экрана.
- Чем заменить `LocalBroadcastManager`? → `SharedFlow`/`StateFlow`, Fragment Result API — они уважают жизненный цикл.
- Зачем флаг `RECEIVER_NOT_EXPORTED` с Android 13? → явно указать, принимать ли сообщения от других приложений; без флага — `SecurityException`.

Связано: [[0 App components. Intent]], [[2 Services and WorkManager]], [[Context]], [[IPC. How two apps communicate]], [[1 Activity]]
