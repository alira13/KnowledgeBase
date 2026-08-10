# DataStore

Современная замена **SharedPreferences** (Jetpack). Хранит небольшие объёмы данных «ключ-значение» или типизированный объект. Работает **асинхронно на корутинах и Flow**, без блокирующих операций на main thread.

## Два вида
- **Preferences DataStore** — ключ-значение (аналог SharedPreferences), без схемы, ключи через `stringPreferencesKey(...)` и т.п.
- **Proto DataStore** — типизированное хранилище на основе Protobuf-схемы (type-safe, есть валидация структуры).

## Почему лучше SharedPreferences
| SharedPreferences | DataStore |
|---|---|
| синхронный API (`getString`), риск I/O на main thread → ANR | асинхронный, `Flow`/`suspend` |
| `apply()` тихо пишет в фоне, `commit()` блокирует | транзакционно, безопасно |
| нет обработки ошибок чтения | ошибки как исключения во Flow |
| не реактивный | реактивный — подписка на изменения через `Flow` |

См. [[ANR. Application Not Responding]], [[Data storage]].

## Пример (Preferences DataStore)
```kotlin
val Context.dataStore by preferencesDataStore(name = "settings")
val KEY_DARK = booleanPreferencesKey("dark_theme")

// чтение — реактивно
val darkFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK] ?: false }

// запись — suspend, транзакционно
suspend fun setDark(enabled: Boolean) {
    context.dataStore.edit { it[KEY_DARK] = enabled }
}
```

## Когда что
- **DataStore** — настройки, флаги, небольшие данные, сессии.
- **Room** — структурированные данные, запросы, связи. См. [[Databases]].
- **EncryptedSharedPreferences / KeyStore** — секреты (DataStore сам не шифрует). См. [[Security. SSL Pinning, KeyStore, secrets]].

Связано: [[Data storage]], [[1 Local storage (internal)]], [[Databases]]
