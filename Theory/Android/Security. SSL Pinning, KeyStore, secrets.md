# Security. SSL Pinning, KeyStore, хранение секретов

Набор senior-вопросов по безопасности мобильного приложения.

## Хранение чувствительных данных
- **Никогда** не хранить пароли/токены в открытом виде в `SharedPreferences`/файлах/логах.
- **EncryptedSharedPreferences** / **DataStore + Tink** — шифрование на ключе из Android KeyStore.
- Токены/ключи шифрования — в **Android KeyStore** (ключ не покидает защищённое хранилище/TEE/StrongBox).
- Не хранить секреты в коде и в `strings.xml` — их легко достать из APK. См. [[R8 and ProGuard. Minification and obfuscation]] (обфускация ≠ защита секретов).

## Android KeyStore
Системное хранилище криптоключей. Ключ **генерируется и живёт в защищённой среде** (TEE / StrongBox), приложение получает только «ручку» для операций (шифрование/подпись), но не сам ключевой материал.
- Можно привязать использование ключа к **биометрии** (`setUserAuthenticationRequired`).
- Используется под капотом EncryptedSharedPreferences/Jetpack Security.

## SSL/TLS Pinning
Защита от **MITM**: приложение доверяет не любому валидному сертификату из системного хранилища, а **конкретному** сертификату/публичному ключу сервера.

Способы:
- **Network Security Config** (XML, декларативно) — pin по хешу публичного ключа:
```xml
<domain-config>
  <domain includeSubdomains="true">api.example.com</domain>
  <pin-set>
    <pin digest="SHA-256">base64Hash=</pin>
    <pin digest="SHA-256">backupHash=</pin>   <!-- запасной! -->
  </pin-set>
</domain-config>
```
- **OkHttp CertificatePinner** (программно).

Риск: если сертификат сервера сменится, а pin не обновить — приложение перестанет ходить в сеть. Поэтому держат **backup-пин** и план ротации.

## Прочие меры (частый список)
- **HTTPS/TLS** везде, `cleartextTrafficPermitted=false`.
- **PendingIntent** — использовать `FLAG_IMMUTABLE`.
- **exported** компоненты — явно `android:exported`, не экспонировать лишнее; защищать `permission`.
- Проверка на **root / эмулятор / debug** для чувствительных приложений (Play Integrity API).
- **ProGuard/R8** — усложняет реверс (но не замена шифрованию).
- Не логировать чувствительное; `FLAG_SECURE` для экранов с приватными данными (запрет скриншотов).
- Проверка целостности и обфускация нативного кода при высоких требованиях.
- **Биометрия** через `BiometricPrompt`.

## OWASP Mobile Top 10
Ориентир для собеса: небезопасное хранение данных, слабая криптография, небезопасная коммуникация, обратная разработка, некорректная аутентификация и т.д.

Связано: [[Permissions]], [[R8 and ProGuard. Minification and obfuscation]], [[WebView]], [[Data storage]]
