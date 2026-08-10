Классы с суффиксом **`Compat`** в Android — это классы совместимости, которые помогают использовать новые API на старых версиях Android. Они находятся в библиотеке **AndroidX** и позволяют разрабатывать приложения, которые корректно работают на разных версиях ОС.

---

## 🔹 **Примеры `Compat`-классов и их использование**

### **1. `ActivityCompat`**

Используется для работы с `Activity`, добавляя поддержку новых API в старых версиях.

✅ **Пример:** Запрос разрешений в Android 6+

```kotlin
if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE)
}
```

**Зачем?** В Android 6+ (API 23) появилось динамическое управление разрешениями, и `ActivityCompat` помогает с ним работать.

---

### **2. `ContextCompat`**

Обёртка для `Context`, обеспечивающая совместимость с разными версиями Android.

✅ **Пример:** Получение цвета (без `getResources().getColor()`, который устарел в API 23)

```kotlin
val color = ContextCompat.getColor(context, R.color.my_color)
```

---

### **3. `NotificationCompat`**

Позволяет создавать уведомления, совместимые со старыми версиями Android.

✅ **Пример:** Создание уведомления

```kotlin
val notification = NotificationCompat.Builder(this, CHANNEL_ID)
    .setContentTitle("Новое сообщение")
    .setContentText("Привет, мир!")
    .setSmallIcon(R.drawable.ic_notification)
    .build()
```

**Зачем?** В Android 8+ (API 26) появились **Notification Channels**, и `NotificationCompat` помогает управлять уведомлениями на старых устройствах.

---

### **4. `DrawableCompat`**

Позволяет использовать `Drawable` с новыми API на старых версиях.

✅ **Пример:** Изменение цвета иконки

```kotlin
val drawable = ContextCompat.getDrawable(context, R.drawable.ic_star)
drawable?.let {
    DrawableCompat.setTint(it, Color.RED)
}
```

---

### **5. `ViewCompat`**

Добавляет поддержку современных возможностей `View` на старых версиях Android.

✅ **Пример:** Добавление тени для `View`

```kotlin
ViewCompat.setElevation(myView, 10f)
```

---

### **6. `FragmentCompat`**

Позволяет работать с фрагментами на старых версиях.

✅ **Пример:** Запрос разрешений внутри `Fragment`

```kotlin
FragmentCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE)
```

---

### **Вывод**

- Классы **`Compat`** помогают разрабатывать приложения, которые корректно работают на **разных версиях Android**.
    
- Они находятся в **AndroidX** (`androidx.core`).
    
- Если вам нужно использовать **новые API на старых устройствах**, всегда ищите `Compat`-версию нужного класса.
    

⚡ **Хочешь больше примеров?** 😃