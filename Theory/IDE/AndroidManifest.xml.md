**`AndroidManifest.xml`** — это один из ключевых файлов в любом приложении для Android. Этот файл является манифестом приложения, который описывает его основные характеристики, функции и компоненты для операционной системы Android. Он находится в корневом каталоге проекта и имеет следующий набор функций:

---

### Основные функции `AndroidManifest.xml`:

1. **Определение компонентов приложения**  
    Манифест описывает основные компоненты приложения, такие как:
    
    - **Активности (`Activity`)**
    - **Сервисы (`Service`)**
    - **Приемники широковещательных сообщений (`BroadcastReceiver`)**
    - **Контент-провайдеры (`ContentProvider`)**
    
    Например:
    
    ```xml
    <activity android:name=".MainActivity" />
    ```
    
2. **Определение разрешений**  
    Если приложению нужны доступы к системным ресурсам, таким как интернет, камера или хранилище, они указываются в манифесте.  
    Например:
    
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    ```
    
3. **Указание минимальной и целевой версии SDK**  
    Это позволяет задать минимальную версию Android, на которой приложение может работать, и целевую версию, для которой оно оптимизировано.  
    Например:
    
    ```xml
    <uses-sdk
        android:minSdkVersion="21"
        android:targetSdkVersion="33" />
    ```
    
4. **Определение точек входа**  
    Манифест определяет, какое из компонентов является начальной точкой входа (например, стартовая активность).
    
    ```xml
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
    ```
    
5. **Объявление настроек приложения**
    
    - Название приложения и иконка через `application`.
    - Темы и стили.
    - Определение пользовательских настроек.
    
    Пример:
    
    ```xml
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.App">
    </application>
    ```
    
6. **Указание используемых библиотек**  
    Если приложение использует внешние или встроенные библиотеки, их можно указать в манифесте.
    
    ```xml
    <uses-library android:name="org.apache.http.legacy" android:required="false" />
    ```
    
7. **Объявление конфигурации запусков и задач**  
    Манифест позволяет указывать режимы запуска компонентов (например, `singleTop`, `singleTask`).
    

---

### Пример минимального `AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapp">

    <application
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.MyApp">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

---

### Зачем нужен `AndroidManifest.xml`:

- Для системы Android он играет роль «дорожной карты», которая указывает, как приложение должно взаимодействовать с платформой.
- Это обязательный файл, без которого приложение не сможет быть скомпилировано или корректно работать.


В Android существует несколько видов **манифеста**, используемых в разных сценариях разработки:

### **1. Основной манифест (`AndroidManifest.xml`)**

- Находится в корне папки `src/main/`
- Описывает:
    - **Имя пакета** (`package`)
    - **Компоненты** (Activity, Service, BroadcastReceiver, ContentProvider)
    - **Разрешения** (`uses-permission`)
    - **Минимальную и целевую версии SDK**
    - **Значок и тему приложения**
    - **Фильтры интентов** (определяют, как приложение взаимодействует с системой)

### **2. Манифест для модулей**

- В многомодульных проектах каждый модуль (`feature`, `library`) может иметь свой `AndroidManifest.xml`
- Эти манифесты объединяются при сборке

### **3. Манифест для сборок (`build variants`)**

- Находится в `src/<variant>/AndroidManifest.xml`
    
- Позволяет задавать разные настройки для различных версий (например, `debug` и `release`)
    

### **4. Манифесты зависимостей (библиотек)**

- Если библиотека содержит `AndroidManifest.xml`, её манифест объединяется с основным приложением при компиляции
    
- Например, библиотеки Google Play Services могут добавлять в манифест нужные разрешения
    

### **5. Итоговый (сгенерированный) манифест**

- После сборки Gradle объединяет все манифесты в один
    
- Можно посмотреть его в **Build Output** (`app/build/intermediates/merged_manifest`)
    

Если тебе нужно что-то конкретное по манифестам, спрашивай! 😊