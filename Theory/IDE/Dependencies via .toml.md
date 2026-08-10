Подключение библиотек через файл `.toml` — это новый способ, представленный Google для Android-проектов, который отличается от традиционного метода использования `build.gradle` файлов на Groovy. Новый способ позволяет управлять зависимостями через **файл конфигурации в формате TOML**, что дает ряд преимуществ в организации и управлении зависимостями.

### Подключение библиотек через `.toml`

1. **Создайте файл `libs.versions.toml`** в директории `gradle` (например, в `gradle/libs.versions.toml`).
   
2. **Добавьте зависимости в формате TOML**. Здесь прописываются версии и координаты библиотек:


   ```toml
   [versions]
   kotlin = "1.5.31"
   appcompat = "1.3.1"
   material = "1.4.0"

   [libraries]
   kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
   androidx-appcompat = { module = "androidx.appcompat:appcompat", version.ref = "appcompat" }
   material-design = { module = "com.google.android.material:material", version.ref = "material" }
   ```

Формат добавления зависимостей в `libs.versions.toml` строится на разделении файла на блоки. Основные блоки — это `[versions]`, `[libraries]`, `[bundles]`, и `[plugins]`, каждый из которых отвечает за различные аспекты управления зависимостями.

### Основные разделы в `libs.versions.toml`

#### 1. **[versions]**
   Здесь указываются версии библиотек, которые могут быть переиспользованы в блоке `[libraries]`.

   ```toml
   [versions]
   kotlin = "1.5.31"
   coroutines = "1.5.2"
   appcompat = "1.3.1"
   material = "1.4.0"
   ```

#### 2. **[libraries]**
   В этом разделе указываются зависимости, где `module` обозначает библиотеку, а `version.ref` ссылается на версию из `[versions]`.

   ```toml
   [libraries]
   kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
   coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
   androidx-appcompat = { module = "androidx.appcompat:appcompat", version.ref = "appcompat" }
   material-design = { module = "com.google.android.material:material", version.ref = "material" }
   ```

   **Прямое указание версии**: Если версия используется только один раз, её можно указать непосредственно.

   ```toml
   coil = { module = "io.coil-kt:coil", version = "1.3.2" }
   ```

#### 3. **[bundles]**
   Этот блок группирует зависимости, которые часто используются вместе. Полезно для упрощения подключения нескольких библиотек одновременно.

   ```toml
   [bundles]
   ui-libs = ["androidx-appcompat", "material-design", "coil"]
   ```

#### 4. **[plugins]**
   Этот раздел используется для плагинов Gradle, таких как плагин Kotlin или Android.

   ```toml
   [plugins]
   android-application = { id = "com.android.application", version = "7.0.0" }
   kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
   ```

### Полный пример `libs.versions.toml`

```toml
[versions]
kotlin = "1.5.31"
coroutines = "1.5.2"
appcompat = "1.3.1"
material = "1.4.0"
coil = "1.3.2"

[libraries]
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
androidx-appcompat = { module = "androidx.appcompat:appcompat", version.ref = "appcompat" }
material-design = { module = "com.google.android.material:material", version.ref = "material" }
coil-image = { module = "io.coil-kt:coil", version.ref = "coil" }

[bundles]
ui-libs = ["androidx-appcompat", "material-design", "coil-image"]

[plugins]
android-application = { id = "com.android.application", version = "7.0.0" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

### Использование зависимостей в `build.gradle`

В `build.gradle` файле теперь можно ссылаться на зависимости из `libs.versions.toml` следующим образом:

```groovy
dependencies {
    implementation libs.kotlin.stdlib
    implementation libs.coroutines.core
    implementation libs.androidx.appcompat
    implementation libs.material.design
    implementation libs.coil.image

    // Подключение всех библиотек из bundles
    implementation libs.bundles.ui.libs
}
```

3. **Подключите файл TOML в проекте**. В `settings.gradle` добавьте строку:

   ```groovy
   dependencyResolutionManagement {
       versionCatalogs {
           libs {
               from(files("gradle/libs.versions.toml"))
           }
       }
   }
   ```

4. **Используйте зависимости из TOML** в `build.gradle` вашего модуля, обратившись к ним через `libs`:

   ```groovy
   dependencies {
       implementation libs.kotlin.stdlib
       implementation libs.androidx.appcompat
       implementation libs.material.design
   }
   ```

### Преимущества использования `.toml` файлов

- **Централизованное управление версиями**: Файл `libs.versions.toml` позволяет управлять версиями библиотек централизованно, вместо того чтобы прописывать их в каждом модуле. Это упрощает обновление библиотек.
- **Лучшее структурирование**: TOML-файл легко читается и поддерживается, так как все версии и библиотеки вынесены в отдельный файл.
- **Сокращение кода**: В `build.gradle` больше не нужно указывать версии библиотек, так как они подтягиваются из файла `libs.versions.toml`.

### Основные отличия от старого подхода

- **Гибкость**: В старом подходе версии зависимостей указываются в каждом `build.gradle`, что затрудняет их централизованное управление.
- **Читабельность**: TOML-файл читабельнее, чем `build.gradle` на Groovy, особенно для большого списка зависимостей.
- **Поддержка версий**: Через `.toml` легче контролировать и обновлять версии, а также использовать одни и те же версии библиотек в разных модулях проекта.