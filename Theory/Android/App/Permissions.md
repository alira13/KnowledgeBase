
Разрешения в **Android** используются для защиты личных данных и доступа к критическим функциям устройства (камера, микрофон, GPS и т. д.).

---

# **1️⃣ Виды разрешений в Android**

### 📌 **1.1. Стандартные (Normal permissions)**

- Доступны без запроса у пользователя.
- Разрешаются автоматически при установке приложения.
- **Примеры:**
    - `ACCESS_NETWORK_STATE` – проверка состояния сети.
    - `INTERNET` – доступ в интернет.

✅ **Добавление в `AndroidManifest.xml`:**

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

---

### 🔐 **1.2. Опасные (Dangerous permissions)**

- Требуют явного запроса у пользователя.
- В Android 6+ (API 23) используются **динамические разрешения**.
- Разделены на **группы** (например, `CAMERA`, `READ_CONTACTS` и т. д.).

✅ **Примеры "опасных" разрешений:**

|Группа|Разрешения|
|---|---|
|**Камера**|`CAMERA`|
|**Контакты**|`READ_CONTACTS`, `WRITE_CONTACTS`|
|**Хранилище**|`READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`|
|**Локация**|`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`|
|**Телефон**|`CALL_PHONE`, `READ_CALL_LOG`|

✅ **Добавление в `AndroidManifest.xml`:**

```xml
<uses-permission android:name="android.permission.CAMERA"/>
```

✅ **Запрос разрешения в коде (Kotlin, API 23+):**

```kotlin
if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE)
}
```

✅ **Обработка ответа пользователя:**

```kotlin
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == REQUEST_CODE) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Разрешение получено!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Разрешение отклонено!", Toast.LENGTH_SHORT).show()
        }
    }
}
```

---

### 🚀 **1.3. Разрешения времени выполнения (Runtime permissions)**

В **Android 6+ (API 23+)** пользователи должны **одобрять "опасные" разрешения во время работы приложения**.  
Ранее все разрешения выдавались при установке.

✅ **Проверка и запрос разрешений:**

```kotlin
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
}
```

---

### 🏆 **1.4. Специальные разрешения (Special permissions)**

Разрешения, которые **нельзя запросить через стандартный `requestPermissions()`**.  
Требуют перехода в настройки системы.

✅ **Примеры:**

|Разрешение|Описание|
|---|---|
|`SYSTEM_ALERT_WINDOW`|Показывать окна поверх других приложений.|
|`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`|Исключение из энергосбережения.|
|`MANAGE_EXTERNAL_STORAGE`|Полный доступ к файлам (Android 11+).|
|`INSTALL_UNKNOWN_APPS`|Установка APK-файлов из сторонних источников.|

✅ **Запрос специального разрешения:**

```kotlin
if (!Settings.canDrawOverlays(this)) {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
    startActivity(intent)
}
```

---

# **2️⃣ Разрешения в новых версиях Android**

### 📌 **2.1. Android 10 (API 29)**

- Разделены разрешения на **"Foreground"** и **"Background"** для локации.
- Новый тип `ACCESS_BACKGROUND_LOCATION`.

✅ **Пример запроса локации в фоне:**

```xml
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
```

```kotlin
ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQUEST_CODE)
```

---

### 📌 **2.2. Android 11 (API 30)**

- `MANAGE_EXTERNAL_STORAGE` даёт **полный доступ** ко всем файлам.
    
- `ACCESS_FINE_LOCATION` **не даёт** автоматический доступ к `ACCESS_BACKGROUND_LOCATION` — теперь нужно **запрашивать отдельно**.
    

✅ **Полный доступ к хранилищу (Android 11+):**

```xml
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>
```

```kotlin
val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:$packageName"))
startActivity(intent)
```

---

### 📌 **2.3. Android 12 (API 31)**

- Разрешения для датчиков: `BODY_SENSORS_BACKGROUND`.
    
- **Отдельные разрешения для медиафайлов:**
    
    - `READ_MEDIA_IMAGES`
        
    - `READ_MEDIA_VIDEO`
        
    - `READ_MEDIA_AUDIO`
        

✅ **Запрос разрешений на медиафайлы:**

```xml
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
```

```kotlin
ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE)
```

---

### 📌 **2.4. Android 13 (API 33)**

- **Новые разрешения для уведомлений:**
    
    - `POST_NOTIFICATIONS` – нужно для отправки уведомлений.
        
    - Запрашивается **динамически**.
        

✅ **Пример запроса уведомлений в Android 13+**

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE)
}
```

---

# **3️⃣ Разрешения в Foreground Service**

Для **Foreground Service** в Android 9+ (API 28+) нужно добавить:

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
```

А для Android 10+ дополнительные типы:

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CAMERA"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MICROPHONE"/>
```

---

# **4️⃣ Проверка, отозвал ли пользователь разрешение**

С Android 11+ пользователь может **отозвать разрешение**, даже если ранее дал его.

✅ **Проверка:**

```kotlin
val isDenied = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
```

Если `true` → пользователь ранее **отклонил** запрос.

✅ **Переход в настройки:**

```kotlin
val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
startActivity(intent)
```

---

# **5️⃣ Классификация по способу выдачи**

| Тип | Когда выдаётся |
|---|---|
| **normal** (install-time) | автоматически при установке, объявляется в манифесте |
| **signature** (install-time) | только приложениям, подписанным **той же подписью**, что и объявившее разрешение |
| **runtime** (dangerous) | запрашивается у пользователя в момент использования |
| **special** | только через настройки системы (`SYSTEM_ALERT_WINDOW`, `MANAGE_EXTERNAL_STORAGE` и т.п.) |

**signature** — механизм доверия по автору. Пример: `android.permission.WRITE_SETTINGS` (правка системных настроек) доступно только приложениям Google/производителя. Вы можете объявить своё signature-разрешение, чтобы дать доступ к своим данным только приложениям с вашей подписью (напр., предустановленное приложение звонков и приложение настроек одного вендора).

**one-time permissions** (Android 11, API 30) — одноразовое разрешение («Только в этот раз»): действует, пока приложение видно + ~30 сек в фоне, затем система отзывает. Поэтому **проверяйте наличие разрешения перед каждым использованием**.

# **6️⃣ Как это работает и отзыв разрешений**

- По сути запрос разрешения — это **коммуникация между двумя Activity**: наша и системная (диалог запроса).
- Пользователь может **отозвать** ранее выданное разрешение в настройках в любой момент.
- Система тоже может отозвать разрешение (auto-reset): если приложением давно не пользовались или оно активно ест батарею в фоне. Логика в современных Android определяется автоматикой.
- Вывод: **никогда не полагайтесь на «раз выдали — навсегда»**, проверяйте перед каждым доступом.

# **7️⃣ Современный подход (Jetpack / Compose)**

В коде на View/Activity вместо устаревшего `onRequestPermissionsResult` используют **Activity Result API**:
```kotlin
val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
    if (granted) onGranted() else onDenied()
}
launcher.launch(Manifest.permission.CAMERA)
```

В Compose — `rememberLauncherForActivityResult(...)` либо библиотека **Accompanist Permissions**:
```kotlin
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCamera(onGranted: () -> Unit) {
    val state = rememberPermissionState(Manifest.permission.CAMERA)
    when {
        state.status.isGranted -> onGranted()
        state.status.shouldShowRationale -> { /* показать объяснение → настройки */ }
        else -> LaunchedEffect(Unit) { state.launchPermissionRequest() }
    }
}
```
Для возврата из системных настроек (когда пользователь выбрал «Больше не спрашивать») отслеживают `ON_RESUME` через `DisposableEffect` и перепроверяют разрешение.

Связано: [[Security. SSL Pinning, KeyStore, secrets]], [[Compose Lifecycle]], [[1 Activity]]

# **🔥 Итог**

- Разрешения бывают **нормальные**, **опасные**, **специальные**.
    
- В Android 6+ **опасные разрешения запрашиваются во время работы**.
    
- В новых версиях (Android 10+) появились **ограничения на локацию, хранилище и уведомления**.
    
- Для **Foreground Service** теперь нужны **отдельные разрешения**.
    

💡 **Нужно больше примеров или разбор конкретных случаев?** 😃