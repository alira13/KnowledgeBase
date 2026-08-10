Когда мы начинаем работать с UI, то мы хотим видеть действия, которые выполняются параллельно
 - считается логика таймера
 - отображаются цифры таймера на экране
 - идет загрузка данных
 - отображается по мере поступления на экране

Все действия по умолчанию происходят в главном потоке, и если мы попытаемся все эти операции сделать, они будут выполнятся последовательно и блокировать поток, пока не закончат свое выполнение.
Выход: создать отдельные потоки для каждой операции. 

```kotlin

  
class CallbackMainActivity : AppCompatActivity() {  
  
    private lateinit var loadBtn: Button  
    private lateinit var contentTv: TextView  
    private lateinit var timerTv: TextView  
  
    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        enableEdgeToEdge()  
        setContentView(R.layout.activity_calback_main)  
  
        loadBtn = findViewById(R.id.btn_load_button)  
        contentTv = findViewById(R.id.tv_content)  
        timerTv = findViewById(R.id.tv_timer)  
        
        // таймер стартуется в отельном потоке 
        startTimer()  
  
        // callback hell  
        loadBtn.setOnClickListener {  
            loadBtn.isEnabled = false  
            // callback 1  
            loadBook { book ->  
                contentTv.append("Loading book...\n\n")  
                contentTv.append(book.toString() + "\n\n")  
  
                contentTv.append("Loading author...\n\n")  
                // callback 2  
                loadAuthor { author: Author ->  
                    contentTv.append(author.toString() + "\n\n")  
                    contentTv.append("Finish loading...\n\n")  
                    // Нам нужно запустить в том же потоке, что и изменил первоначальное значение  
                    // это был Main(Ui-thread), поэтому мы и запускаем внутри него                    runOnUiThread {  
                        loadBtn.isEnabled = true  
                    }  
                }            }        }    }  
  
    // весь код по умолчанию выполняется в главном потоке,  
    // если мы не положим его в отельный поток, то этот код заблокирует главный поток    
    // то есть здесь будут происходить расчеты, но на UI не будет изменений    private fun startTimer() {  
        thread {  
            var totalSeconds = 0  
            while (true) {  
                val minutes = totalSeconds / 60  
                val seconds = totalSeconds % 60  
                timerTv.text = String.format("%02d:%02d", minutes, seconds)  
                Thread.sleep(1000)  
                totalSeconds++  
            }  
        }  
    }  
  
    // долгие операции выносят в отельный поток  
    // если нужно чтобы операция вернула даные по завершению, используют механизм callback    
    // вызов функции происходит не сразу,а в будущем после окончания метода sleep    private fun loadBook(callback: (Book) -> Unit) {  
        thread {  
            Thread.sleep(3000)  
            callback(Book("1984", 1949, "Dystopia"))  
        }  
    }  
  
    private fun loadAuthor(callback: (Author) -> Unit) {  
        thread {  
            Thread.sleep(3000)  
            callback(Author("Oruel", "British writer"))  
        }  
    }  
}
```
