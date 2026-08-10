**Deep Link** — это специальная ссылка, которая открывает **конкретный экран** в приложении. Она может передавать параметры, чтобы, например, сразу открыть товар в интернет-магазине или конкретную статью в новостном приложении.

📌 **Пример использования:**
- **Обычная ссылка**: `https://example.com/news` → просто открывает сайт.
- **Deep Link**: `myapp://news/123` → открывает экран новости с ID = 123 внутри приложения.

---

## **🔗 Виды Deep Links**

1. **Простые (Custom URI Schemes)**
    - Используют кастомные URI (`myapp://...`).
    - Работают только если приложение установлено.
    - Пример: `myapp://product/42` → открывает карточку товара.
2. **App Links (Android App Links)**
    - Основаны на HTTPS (`https://example.com/...`).
    - Работают и без установленного приложения (открывают сайт).
    - Требуют подтверждения домена через `assetlinks.json`.
3. **Deferred Deep Links**
    - Работают даже если **приложение не установлено**.
    - Используются в рекламе, пуш-уведомлениях.
    - После установки приложения сохраняют и обрабатывают ссылку.
---

## **🛠 Как настроить Deep Link?**

### **1. Добавление в `AndroidManifest.xml`**

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW"/>
    <category android:name="android.intent.category.DEFAULT"/>
    <category android:name="android.intent.category.BROWSABLE"/>
    <data android:scheme="myapp" android:host="news"/>
</intent-filter>
```

Теперь `myapp://news/123` откроет приложение.

---
**Deeplink** — это механизм, позволяющий напрямую открывать конкретные экраны внутри приложения. Это достигается с помощью интентов (intents), которые определяют, куда перенаправить пользователя при нажатии на ссылку.

## Принципы работы глубоких ссылок

1. **Регистрация в манифесте**: В файле `AndroidManifest.xml` необходимо зарегистрировать глубокую ссылку, указав, какие ссылки должна обрабатывать ваша активность.
2. **Обработка интентов**: В активности нужно обработать интент, чтобы понять, что делать с полученными данными.
### **2. Обработка Deep Link в `Activity`**

```kotlin
class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val data: Uri? = intent?.data
        val newsId = data?.lastPathSegment // Получаем "123" из "myapp://news/123"
        
        Log.d("DeepLink", "Открыта новость с ID: $newsId")
    }
}
```

---

### **3. Использование Navigation Component (рекомендуемый способ)**

В `nav_graph.xml` добавь `<deepLink>`:

```xml
<fragment
    android:id="@+id/newsFragment"
    android:name="com.example.NewsFragment">
    <deepLink app:uri="myapp://news/{newsId}" />
</fragment>
```

Теперь система автоматически откроет `NewsFragment` при переходе по `myapp://news/123`.

---

## **🔥 Где используют Deep Links?**

✅ Открытие приложения по ссылке из **браузера, мессенджера**.  
✅ Отслеживание переходов **из рекламы** (Deferred Deep Link).  
✅ Открытие **определённых экранов** при пуш-уведомлениях.  
✅ Интеграция с **QR-кодами**.

Ты уже работал с Deep Links или только изучаешь? 🚀