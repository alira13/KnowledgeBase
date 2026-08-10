**BrodcastReceiver** — этот компонент принимает сообщения, которые отправляют . То есть по сути система или наши компоненты или другие приложения все время отправляет какие-то оповещения и если мы их хотим впоймать и обрабоатьь как-то, можно исепользовать **BrodcastReceiver** 

 - другие компоненты нашего приложения, 
 - другие компоненты чужих приложений или 
 - вообще сам Android(батарея разядилась)
Все входящие сообщения фильтруются, чтобы понять, какие именно сообщения нужны данному экземпляру. Компонент обязательно должен быть зарегистрирован, чтобы начать прием сообщений. А потом еще надо отменить регистрацию, чтобы зря не работал. Если регистрируем статически в манифесте, то компонент автоматически получает сообщения, пока приложение установлено.

### Создание и запуск

1. Создали класс MyBroadcastReceiver и отнаследовали его от BroadcastReceiver
2. Переопределили метод onReceive который принимает на вход intent и context: у intent вызвали свойство action и в зависимости от него выбрали действие

```kotlin
package com.example.broadcastreceiver  
  
import android.content.BroadcastReceiver  
import android.content.Context  
import android.content.Intent  
import android.widget.Toast  
  
class MyBroadcastReceiver : BroadcastReceiver() {  
    override fun onReceive(context: Context?, intent: Intent?) {  
        when (intent?.action) {  
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {  
                // сейчас Intent создает система и кладет нужные данные в Intent тоже система  
                val turnOn = intent.getBooleanExtra("state", false)  
                Toast.makeText(context, "Airplane mode is turnOn = $turnOn", Toast.LENGTH_SHORT)  
                    .show()  
            }  
  
            Intent.ACTION_BATTERY_LOW -> {  
                Toast.makeText(context, "Low battery", Toast.LENGTH_SHORT).show()  
            }  
        }  
    }  
}
```
 - Динамическая регистрация(предпочтительная)
3. Создали intent - фильтр в activity
4. Зарегистрировали в activiry передав фильтр
 - Статическая
добавили в манифест ресивер и к нему Intent фильтры
```kotlin
class MainActivity : AppCompatActivity() {  
    private val myBroadcastReceiver = MyBroadcastReceiver()  
  
    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        enableEdgeToEdge()  
        setContentView(R.layout.activity_main)  
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->  
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())  
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)  
            insets  
        }  
  
        // добавляем фильтры чтобы реагировать на конкретные события  
        val intentFilterAirplaneMode = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)  
        val intentFilterLowBattery = IntentFilter(Intent.ACTION_BATTERY_LOW)  
        // можем сначала создать intentFilter, а потом добавить несколько фильтров в него  
        val intentFilter = IntentFilter().apply {  
            addAction(Intent.ACTION_BATTERY_LOW)  
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)  
        }  
  
        // регистрируем receiver  
        // динамическая регистрация receiver - в момент работы программы - наиболее частый        // статическая регистрация в manifest c добавлением фильтров        // - начиная с версии 26 не реагируют на большинство action        // используются только когда нужно реагировать после перезапуска телефона        // не нужно убивать в onDestroy        registerReceiver(myBroadcastReceiver, intentFilter)  
        //registerReceiver(myBroadcastReceiver, intentFilterLowBattery)  
    }  
  
    // чтобы не было утечек памяти, нужно отписаться  
    override fun onDestroy() {  
        super.onDestroy()  
        unregisterReceiver(myBroadcastReceiver)  
    }  
}
```

#### Создание собственных событий
```kotlin
// если сами хотим отправить оповещение и допустим с параметром  
val btn = findViewById<Button>(R.id.btn)  
btn.setOnClickListener {  
    val intent = Intent(MyBroadcastReceiver.ACTION_CLICKED)  
    intent.putExtra(MyBroadcastReceiver.ACTION_CLICK_NUM, clickNum++)  
    sendBroadcast(intent)  
}  

// можем сначала создать intentFilter, а потом добавить несколько фильтров в него  
val intentFilter = IntentFilter().apply {  
    addAction(MyBroadcastReceiver.ACTION_CLICKED)  
}  

// регистрируем receiver  
registerReceiver(myBroadcastReceiver, intentFilter)  
//registerReceiver(myBroadcastReceiver, intentFilterLowBattery)
```
Вытаскиваем параметр и реагируем на наше событие
```kotlin
class MyBroadcastReceiver : BroadcastReceiver() {  
    override fun onReceive(context: Context?, intent: Intent?) {  
        when (intent?.action) {  
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {  
                // сейчас Intent создает система и кладет нужные данные в Intent тоже система  
                val turnOn = intent.getBooleanExtra("state", false)  
                Toast.makeText(context, "Airplane mode is turnOn = $turnOn", Toast.LENGTH_SHORT)  
                    .show()  
            }  
  
            Intent.ACTION_BATTERY_LOW -> {  
                Toast.makeText(context, "Low battery", Toast.LENGTH_SHORT).show()  
            }  
  
            // когда создаем свое событие и на него реагируем  
            ACTION_CLICKED -> {  
                val clickNum = intent.getIntExtra(ACTION_CLICK_NUM, 1)  
                Toast.makeText(context, "Button clicked $clickNum", Toast.LENGTH_SHORT).show()  
            }  
        }  
    }  
  
    companion object {  
        const val ACTION_CLICKED = "clicked"  
        const val ACTION_CLICK_NUM = "clickNum"  
    }  
}
```

!!! **вызывается на главном потоке но так как нет тяжеловесных операций то не блокирует поток  
override fun onReceive(context: Context?, intent: Intent?)**

### LocalBroadcastManager

**LocalBroadcastManager** — это класс в Android, который позволяет отправлять и получать широковещательные сообщения внутри одного приложения. Он используется для передачи данных между компонентами приложения, такими как Activity, Service и BroadcastReceiver, без выхода за рамки приложения.

## Основные особенности LocalBroadcastManager

1. **Внутриприложное общение**: LocalBroadcastManager позволяет отправлять сообщения только внутри приложения, что повышает безопасность и предотвращает утечку данных в другие приложения.
2. **Эффективность**: Использование LocalBroadcastManager более эффективно, чем стандартные широковещательные сообщения, поскольку не требует системных ресурсов для обработки сообщений между приложениями.
3. **Простота использования**: Для работы с LocalBroadcastManager необходимо зарегистрировать BroadcastReceiver с помощью `LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter)`, а затем отправлять сообщения с помощью `LocalBroadcastManager.getInstance(context).sendBroadcast(intent)`.

```kotlin
// создаем локальный менеджер
private val localBroadcastManager: LocalBroadcastManager by lazy {  
    LocalBroadcastManager.getInstance(this)  
}

// подписываемся через него
localBroadcastManager.registerReceiver(progressBarBroadcastReceiver, progressBarIntentFilter)
// через него удаляем
// отписываемся тоже через него
```

