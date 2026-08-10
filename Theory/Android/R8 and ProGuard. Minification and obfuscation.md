# R8 и ProGuard. Минификация и обфускация

Инструменты сжатия и оптимизации release-сборки. **R8** — современный компилятор Google (заменил ProGuard, включён по умолчанию в Android Gradle Plugin), делает то же за один проход и эффективнее.

## Что делает R8
1. **Shrinking (tree shaking)** — удаляет неиспользуемый код (классы, методы, поля) и ресурсы → меньше размер APK/AAB.
2. **Optimization** — инлайнинг, упрощение кода, удаление мёртвых веток.
3. **Obfuscation** — переименовывает классы/методы/поля в короткие имена (`a`, `b`, `c`) → меньше размер + усложняет реверс-инжиниринг.
4. **Desugaring** — поддержка новых Java API/синтаксиса на старых Android.

## Включение
```kotlin
// build.gradle (release)
buildTypes {
    release {
        isMinifyEnabled = true        // R8: shrink + obfuscate
        isShrinkResources = true      // удалить неиспользуемые ресурсы
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

## Правила `proguard-rules.pro` (keep rules)
R8 может ошибочно удалить/переименовать код, к которому обращаются через **рефлексию**, JNI, из манифеста, через имена (Gson, сериализация). Такие места надо «защитить»:
```proguard
-keep class com.example.model.** { *; }      # не трогать модели (JSON-рефлексия)
-keepclassmembers class * { @com.google.gson.annotations.SerializedName <fields>; }
-keepattributes Signature, *Annotation*      # generic-типы, аннотации
```
Библиотеки (Retrofit, Gson, Moshi, Room) поставляют свои consumer-правила автоматически.

## Mapping-файл (важно!)
Обфускация ломает читаемость стектрейсов крашей. R8 генерирует **`mapping.txt`** (соответствие оригинал↔обфусцированные имена). Его нужно:
- сохранять для каждой release-сборки,
- загружать в Play Console / Crashlytics для **деобфускации** крашей.

## Типичные проблемы на собесе
- «После включения minify приложение падает в release, но не в debug» → не хватает keep-правил для рефлексии/сериализации.
- Как отлаживать обфусцированный краш → по mapping.txt (`retrace`).
- Разница R8 vs ProGuard → R8 быстрее, встроен, лучше оптимизирует, конфиг совместим.

Связано: [[CI-CD for Android]], [[Security. SSL Pinning, KeyStore, secrets]], [[Build.gradle]]
