# Networking. Retrofit + OkHttp

Стандартный стек сети в Android. Частые вопросы: интерцепторы, кэш, таймауты, обработка ошибок, отличие Retrofit от OkHttp.

## Кто за что отвечает
- **OkHttp** — HTTP-клиент: сокеты, пул соединений, кэш, интерцепторы, повторы, TLS. Низкий уровень.
- **Retrofit** — надстройка над OkHttp: превращает **интерфейс с аннотациями** в готовые запросы, сериализует/десериализует тела (через конвертер). Высокий уровень.

```kotlin
interface Api {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): User   // suspend — из коробки

    @POST("users")
    suspend fun create(@Body body: UserDto): Response<User>
}

val client = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .addInterceptor(loggingInterceptor)      // application interceptor
    .addNetworkInterceptor(cacheInterceptor) // network interceptor
    .cache(Cache(cacheDir, 10L * 1024 * 1024))
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.example.com/")
    .client(client)
    .addConverterFactory(KotlinxSerializationConverterFactory.create(json))
    .build()

val api = retrofit.create(Api::class.java)
```

## Интерцепторы (частый вопрос)
Перехватывают каждый запрос/ответ — логирование, авторизация, кэш, повторы.
```kotlin
class AuthInterceptor(val tokenStore: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .header("Authorization", "Bearer ${tokenStore.token}")
            .build()
        return chain.proceed(req)
    }
}
```
- **Application interceptor** (`addInterceptor`) — вызывается один раз на запрос, видит логику Retrofit; не видит редиректы/повторы.
- **Network interceptor** (`addNetworkInterceptor`) — ближе к сети, видит редиректы, реальные заголовки кэша.

## Обновление токена (Authenticator)
При 401 OkHttp вызывает `Authenticator` — там рефрешат токен и повторяют запрос. См. [[Security. SSL Pinning, KeyStore, secrets]].

## Обработка ошибок
- `suspend fun ...: User` кидает исключение при не-2xx/сетевой ошибке.
- `Response<T>` — даёт `isSuccessful`, `code()`, `body()`, `errorBody()` без исключений.
- Оборачивают в типизированный `Result<Data, DataError>` в data-слое. См. [[Repository pattern]], [[Clean architecture]].
```kotlin
suspend fun user(id: String): Result<User, DataError> = try {
    Result.Success(api.getUser(id))
} catch (e: HttpException) { Result.Error(mapHttp(e.code())) }
  catch (e: IOException) { Result.Error(DataError.NO_INTERNET) }
```

## Кэш и таймауты
- HTTP-кэш OkHttp работает по заголовкам (`Cache-Control`, `ETag`); часто добавляют интерцептор, форсирующий кэш офлайн.
- Таймауты: connect/read/write — задавать явно, иначе долгие зависания → ANR если ждать на main (сеть всегда в фоне/корутине). См. [[ANR. Application Not Responding]].

## Альтернатива
**Ktor Client** — мультиплатформенный (KMP), корутинный. См. [[KMP. Kotlin Multiplatform]].

## Вопрос-ловушка
«Чем `addInterceptor` отличается от `addNetworkInterceptor`?» → уровнем: application — один раз, до кэша/редиректов; network — на каждый реальный сетевой вызов, после редиректов.

Связано: [[Repository pattern]], [[Clean architecture]], [[Security. SSL Pinning, KeyStore, secrets]], [[0 Serialization. Serializable vs Parcelable]]
