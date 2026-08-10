### **0. В локальных переменных**
- в локальной переменной. При чтении данных из файла мы их сохраняем в переменную. Будет хранится в ОЗУ(оперативной памяти). После закрытия приложения данные удалятся

### 1. **SharedPreferences**

- Хранение пар ключ-значение.
- Подходит для небольших данных (настройки, токены, флаги).
- Пример использования:
    
    ```kotlin
    val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
    sharedPref.edit().putString("username", "User123").apply()
    val username = sharedPref.getString("username", "")
    ```

### 2. **DataStore (Jetpack)**

- Современная альтернатива `SharedPreferences` на основе `Kotlin Coroutines`.
- Поддерживает `Proto DataStore` (для сложных объектов) и `Preferences DataStore`.
- Пример (`Preferences DataStore`):
    
    ```kotlin
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val exampleKey = stringPreferencesKey("example_key")
    lifecycleScope.launch {
        dataStore.edit { it[exampleKey] = "Hello, DataStore!" }
    }
    ```

### 7. **Encrypted SharedPreferences / Encrypted File Storage**

- Шифрованное хранилище с использованием `AndroidX Security`.
- Позволяет безопасно хранить конфиденциальные данные.
- Пример `EncryptedSharedPreferences`:
    
    ```kotlin
    val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        MasterKey.DEFAULT_MASTER_KEY_ALIAS,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    ```

### 7. **Proto data store**
**Proto DataStore** — это одна из реализаций DataStore в Android, которая позволяет хранить данные в формате Protocol Buffers (protobuf). В отличие от SharedPreferences, DataStore (и его версии Proto и Preferences) работает асинхронно и безопасно для многопоточного доступа.
1. **Использует Protocol Buffers (protobuf)** – компактный и эффективный формат сериализации данных.
2. **Асинхронность** – работает с `Flow`, что делает его идеальным для реактивных приложений.
3. **Безопасность потоков** – избегает проблем, связанных с конкурентным доступом к данным.
4. **Более надежное хранилище** – нет риска повреждения данных при внезапном завершении работы приложения.
### 3. **Internal Storage (Внутреннее хранилище) - хранение в файле**

 - прочитать `file.readText`
 - прочитать и обрезать пробелы спереди и сзади `file.readText().trim()`
 - перезаписать `writeText()`
 - дозаписать `appendText`

- Доступно только внутри приложения (другие приложения не могут прочитать файлы).
- Пример записи и чтения:
    
    ```kotlin
    val file = File(filesDir, "example.txt")
    file.writeText("Hello, Internal Storage!")
    val content = file.readText()
    ```
    

### 4. **External Storage (Внешнее хранилище)**

- Можно сохранять файлы, доступные и другим приложениям (например, изображения, документы).
- Требует разрешений (`WRITE_EXTERNAL_STORAGE` для Android < 10).
- Пример сохранения файла:
    
    ```kotlin
    val file = File(getExternalFilesDir(null), "example.txt")
    file.writeText("Hello, External Storage!")
    ```

### 5. **Database**
SQLiteOpenHelper

- Встроенная реляционная база данных, подходит для сложных структурированных данных.
- Реализуется через `SQLiteOpenHelper`.
- Пример создания БД:
    
    ```kotlin
    class DBHelper(context: Context) : SQLiteOpenHelper(context, "myDB", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT)")
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    }
    ```
    

### 6. **Room (Jetpack)**

- Абстракция над `SQLite`, упрощает работу с БД.
- Поддерживает `DAO`, `LiveData`, `Coroutines`.
- Пример сущности:
    
    ```kotlin
    @Entity
    data class User(@PrimaryKey val id: Int, val name: String)
    ```
    
- DAO (Data Access Object) — это интерфейс, который определяет методы для взаимодействия с базой данных.
1. **Обеспечивает абстракцию** — скрывает SQL-запросы за методами.
2. **Гарантирует безопасность** — Room автоматически проверяет запросы во время компиляции.
3. **Работает с LiveData и Flow** — поддерживает реактивный доступ к данным.
    
    ```kotlin
    @Dao
    interface UserDao {
        @Insert fun insert(user: User)
        @Query("SELECT * FROM User") fun getAll(): List<User>
    }
    ```

### **Выбор метода хранения**

|Метод|Подходит для|
|---|---|
|**SharedPreferences**|Настройки, простые данные (строки, числа)|
|**DataStore**|Аналог `SharedPreferences`, но с `Coroutines` и `Flow`|
|**Internal Storage**|Конфиденциальные файлы, доступные только приложению|
|**External Storage**|Изображения, документы, видео (если нужен общий доступ)|
|**SQLite**|Работа с реляционной базой данных|
|**Room**|Простая работа с базой данных с использованием ORM|
|**Encrypted Storage**|Защищенные данные (пароли, токены)|

Если приложение работает с настройками — `SharedPreferences` или `DataStore`.  
Для структурированных данных лучше `Room`.  
Если нужна безопасность — `EncryptedSharedPreferences`.