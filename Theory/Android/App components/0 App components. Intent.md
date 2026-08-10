-это компоненты, элементы, с помощью которых любое приложение взаимодействует с системой, а значит и с другими приложениям

— Основными компонентами любого Android-приложения являются компоненты, управляемые средой выполнения: 
Активити, 
Сервисы, 
Broadcast Receiver
Content Provider. 
Конфигурация и взаимодействие этих компонентов определяют поведение приложения. Перечисленные компоненты имеют разные зоны ответственности и разные жизненные циклы, но все они являются точками входа в приложение. Из каждого компонента мы можем запустить любой другой с помощью интента. 
 - **Каждый компонент должен быть зарегистрирован в AndroidManifest.**


За что можно зацепиться в этом ответе и какие могут быть следующие вопросы? Попробуем каждый разобрать.

1. А что за среда выполнения? Можно поподробней?
2. Ты сказала, что у активити увеличивается объем затраченной памяти. А можно подробнее?
3. А точно каждый компонент должен быть зарегистрирован в манифесте?
4. Давай поподробнее про любой компонент и жизненный цикл. В каких потоках они выполняются.
5. Что за системный приоритет и как приложение может завершиться?
6. Какие бывают интенты?
7. Какие флаги при создании сервиса есть?

### **Что такое `Intent` в Android и его виды?**

`Intent` – это механизм в Android для взаимодействия между компонентами приложения и передачи данных. С его помощью можно:  
✅ Открывать `Activity` и `Service`  
✅ Запускать `BroadcastReceiver`  
✅ Передавать данные между компонентами  
✅ Запускать внешние приложения (например, открыть веб-сайт в браузере)

---

## **Виды `Intent`**

### **1. Явный (`Explicit Intent`)**

Используется для навигации внутри приложения, когда **точно знаем, какой компонент нужно запустить** (`Activity`, `Service`).

#### **Пример: переход на другую `Activity`**

```kotlin
val intent = Intent(this, SecondActivity::class.java)
startActivity(intent)
```

Можно передавать данные через `putExtra()`:

```kotlin
val intent = Intent(this, SecondActivity::class.java).apply {
    putExtra("USERNAME", "Alice")
}
startActivity(intent)
```

Получение данных в `SecondActivity`:

```kotlin
val username = intent.getStringExtra("USERNAME")
```

📌 **Когда использовать?**

- Вызов `Activity` внутри своего приложения
- Запуск `Service` внутри приложения

---

### **2. Неявный (`Implicit Intent`)**

Используется, когда **не знаем конкретный компонент**, но хотим выполнить определённое действие (например, открыть веб-сайт или отправить сообщение).

#### **Пример: открытие веб-сайта в браузере**

```kotlin
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
startActivity(intent)
```

#### **Пример: вызов телефона**

```kotlin
val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+123456789"))
startActivity(intent)
```

#### **Пример: отправка email**

```kotlin
val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_EMAIL, arrayOf("example@gmail.com"))
    putExtra(Intent.EXTRA_SUBJECT, "Тема письма")
    putExtra(Intent.EXTRA_TEXT, "Текст письма")
}
startActivity(Intent.createChooser(intent, "Выберите почтовый клиент"))
```

📌 **Когда использовать?**

- Открытие ссылок, звонков, почты, камеры
- Взаимодействие с другими приложениями

---

### **3. `Intent` для запуска `Service`**

Используется для запуска фоновых сервисов.

#### **Пример: запуск `ForegroundService`**

```kotlin
val intent = Intent(this, MyService::class.java)
startService(intent)
```

📌 **Когда использовать?**

- Фоновая загрузка данных
- Воспроизведение музыки

---

### **4. `Intent` для `BroadcastReceiver`**

Позволяет отправлять сообщения другим компонентам системы.

#### **Пример: отправка `Broadcast`**

```kotlin
val intent = Intent("com.example.CUSTOM_ACTION")
sendBroadcast(intent)
```

#### **Пример: прием `Broadcast` в `BroadcastReceiver`**

```kotlin
class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Получено сообщение!", Toast.LENGTH_SHORT).show()
    }
}
```

📌 **Когда использовать?**

- Реакция на системные события (`BOOT_COMPLETED`, `BATTERY_LOW`)
- Обмен данными между приложениями

---

## **Вывод: какой `Intent` выбрать?**

|Вид `Intent`|Когда использовать|Пример|
|---|---|---|
|**Явный (`Explicit`)**|Запуск `Activity` или `Service` в своём приложении|`Intent(this, SecondActivity::class.java)`|
|**Неявный (`Implicit`)**|Вызов других приложений (браузер, камера)|`Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))`|
|**Для `Service`**|Запуск фоновых задач|`startService(Intent(this, MyService::class.java))`|
|**Для `BroadcastReceiver`**|Отправка сообщений внутри системы|`sendBroadcast(Intent("com.example.CUSTOM_ACTION"))`|

🚀 **Вывод:**

- **Используйте `Explicit Intent`** для переходов внутри приложения.
- **`Implicit Intent` подходит для взаимодействия с другими приложениями**.
- **`Intent` для `Service` и `BroadcastReceiver`** используется для фоновых задач.