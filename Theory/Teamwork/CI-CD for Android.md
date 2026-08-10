# CI/CD для Android

- **CI (Continuous Integration)** — на каждый push/PR автоматически: сборка, статический анализ, тесты. Цель — рано ловить поломки и держать `main` зелёным.
- **CD (Continuous Delivery/Deployment)** — автоматическая доставка сборки: в internal/beta-трек, затем в прод (Google Play).

Смысл в обратной связи: чем позже найдена поломка, тем дороже её чинить. CI переносит проверку с «через неделю у тестировщика» на «через 10 минут в PR».

## Типовой pipeline
1. **Checkout** + кэш Gradle и зависимостей.
2. **Статический анализ** — `ktlint`/`detekt`, Android Lint.
3. **Unit-тесты** — `./gradlew test`. См. [[Testing]].
4. **Instrumented/UI-тесты** — на эмуляторе (Gradle Managed Devices) или Firebase Test Lab. См. [[Test. UI]].
5. **Сборка** — `assembleRelease` / `bundleRelease` (**AAB**, не APK, для Play).
6. **Подпись** — release keystore из секретов CI, не из репозитория.
7. **Публикация** — загрузка AAB через **Gradle Play Publisher** / fastlane в трек, поэтапный rollout.

## Что гонять на PR, а что на merge
Это вопрос баланса: полный прогон на каждый коммит — дорого и медленно.

| Этап | На PR | На merge в `main` | По расписанию (ночью) |
| --- | --- | --- | --- |
| ktlint / detekt / lint | ✅ | ✅ | |
| Unit-тесты | ✅ | ✅ | |
| Сборка debug | ✅ | ✅ | |
| UI-тесты (смоук) | ✅ короткий набор | ✅ | |
| UI-тесты (полные, матрица устройств) | | | ✅ |
| Сборка release + публикация в internal | | ✅ | |

Ориентир: **проверки на PR должны укладываться в 10–15 минут**. Если дольше — разработчики начинают мержить не дождавшись, и смысл теряется.

## Пример: GitHub Actions
```yaml
name: CI

on:
  pull_request:
  push:
    branches: [ main ]

jobs:
  check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - uses: gradle/actions/setup-gradle@v3   # кэш Gradle и зависимостей из коробки

      - name: Static analysis
        run: ./gradlew detekt lintDebug

      - name: Unit tests
        run: ./gradlew testDebugUnitTest

      - name: Upload test report
        if: failure()                          # отчёт нужен именно когда упало
        uses: actions/upload-artifact@v4
        with:
          name: test-report
          path: '**/build/reports/tests/'
```

Релизная часть, срабатывающая по тегу:
```yaml
  release:
    if: startsWith(github.ref, 'refs/tags/v')
    runs-on: ubuntu-latest
    steps:
      - name: Decode keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > keystore.jks

      - name: Build signed bundle
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: ./gradlew bundleRelease
```
```kotlin
// app/build.gradle.kts — подпись читает переменные окружения, а не хардкод
signingConfigs {
    create("release") {
        storeFile = file("../keystore.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS")
        keyPassword = System.getenv("KEY_PASSWORD")
    }
}
```

## Инструменты
- **CI-системы**: GitHub Actions, GitLab CI, Bitrise (заточен под мобилку), Jenkins, TeamCity, CircleCI.
- **fastlane** — автоматизация сборки, подписи, публикации, скриншотов.
- **Gradle Play Publisher** — публикация в Play прямо из Gradle.
- **Firebase App Distribution** — раздача бета-сборок тестировщикам без Play.
- **Firebase Test Lab / Gradle Managed Devices** — прогон UI-тестов на матрице устройств.

## Время сборки как метрика
Это то, что отличает senior-ответ: время CI — не данность, а величина, которой управляют и которую измеряют.

- **Gradle build cache** (локальный + remote) — переиспользование результатов задач между запусками и между машинами.
- **Configuration cache** — пропуск фазы конфигурации.
- `--parallel`, `org.gradle.caching=true`, достаточный heap для демона.
- **Многомодульность** — пересобирается только изменённое. См. [[Multi-module architecture]].
- **KSP вместо KAPT** — часто самый быстрый выигрыш. См. [[KAPT vs KSP]].
- Профилирование: `./gradlew assembleDebug --scan` показывает, куда ушло время.
- Отдельная метрика — **время до обратной связи** в PR, а не общее время всех джоб: параллельные джобы дешевле последовательных.

## Безопасность
- Keystore, `google-services.json`, API-ключи, service account — **в секретах CI**, не в git. См. [[Security. SSL Pinning, KeyStore, secrets]].
- Секреты **не логировать**: `echo` переменной с ключом попадёт в лог сборки, а логи часто публичны.
- PR из форков **не должны получать доступ к секретам** — иначе чужой PR сможет их выкачать.
- Подпись release — только на защищённом раннере; лучше **Play App Signing**, когда ключ хранит Google, а ты — только upload key.
- Пины зависимостей и проверка на уязвимости (`dependency-check`, Renovate/Dependabot).

## Версионирование
- `versionCode` — целое, **строго возрастает**; Play не примет сборку с тем же или меньшим. Часто генерируют из номера сборки CI или количества коммитов.
- `versionName` — человекочитаемое, обычно semver (`1.4.2`).
- Тег в git (`v1.4.2`) как триггер релизной джобы — удобно и даёт трассируемость «сборка ↔ коммит».

## Публикация (senior-вопрос «опишите деплой»)
1. Поднять `versionCode`/`versionName`.
2. Собрать подписанный **AAB** с включённым R8/минификацией. См. [[R8 and ProGuard. Minification and obfuscation]].
3. Прогнать тесты, **залить mapping-файл** — без него стектрейсы крашей нечитаемы.
4. Загрузить в Play Console → трек: internal → closed (alpha) → open (beta) → production.
5. **Staged rollout**: 5% → 20% → 100%, мониторинг Android Vitals и Crashlytics; при регрессе — halt rollout.

Важная особенность мобильной разработки: **откатить релиз нельзя**. У пользователей уже установлена версия, а Play умеет только остановить раскатку и выпустить исправление поверх. Отсюда и осторожность со staged rollout, и ценность feature flags — выключить фичу удалённо быстрее, чем выпустить новую сборку.

## Типичные проблемы
- **Флакающие UI-тесты** делают CI недоверенным: «красное — просто перезапусти». Лечится карантином для нестабильных тестов и детерминированным окружением, а не отключением проверок.
- **Растущее время сборки** — если не следить, за год превращается в час.
- **Секреты в логах** после неаккуратного дебага.
- **«Работает у меня»** — разные версии JDK/SDK локально и в CI; фиксируй версии явно.

## Вопросы-ловушки
- Чем CI отличается от CD? → CI проверяет каждое изменение, CD доставляет проверенную сборку пользователям.
- Почему AAB, а не APK? → Play собирает из бандла оптимизированные APK под конкретное устройство; для новых приложений AAB обязателен.
- Зачем заливать mapping-файл? → иначе обфусцированные стектрейсы крашей нечитаемы.
- Можно ли откатить релиз в Play? → нет, только остановить раскатку и выпустить фикс поверх.
- Что делать с флакающими тестами в CI? → чинить или изолировать, но не игнорировать красный статус — иначе CI перестаёт работать как сигнал.
- Как ускорить CI? → кэш Gradle, configuration cache, многомодульность, KSP, разделение быстрых и тяжёлых проверок.

Связано: [[Git]], [[Testing]], [[Test. UI]], [[Multi-module architecture]], [[KAPT vs KSP]], [[R8 and ProGuard. Minification and obfuscation]], [[Code review]], [[Gradle]]
