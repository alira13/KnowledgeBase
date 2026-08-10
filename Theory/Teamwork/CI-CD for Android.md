# CI/CD для Android

- **CI (Continuous Integration)** — на каждый push/PR автоматически: сборка, статический анализ, тесты. Цель — рано ловить поломки, держать `main` зелёным.
- **CD (Continuous Delivery/Deployment)** — автоматическая доставка сборки: в internal/beta-трек, затем в прод (Google Play).

## Типовой pipeline
1. **Checkout** + кэш Gradle/зависимостей.
2. **Lint / детект стиля** — `ktlint`/`detekt`, Android Lint.
3. **Unit-тесты** — `./gradlew test`. См. [[Testing]].
4. **Instrumented/UI-тесты** — на эмуляторе (Gradle Managed Devices) или Firebase Test Lab.
5. **Сборка** — `assembleRelease` / `bundleRelease` (**AAB**, не APK, для Play).
6. **Подпись** — release keystore из секретов CI (не в репозитории!).
7. **Публикация** — загрузка AAB в Play через **Gradle Play Publisher** / fastlane в трек (internal → closed → open → production), поэтапный rollout.

## Инструменты
- **CI-системы**: GitHub Actions, GitLab CI, Bitrise (заточен под мобилку), Jenkins, TeamCity, CircleCI.
- **fastlane** — автоматизация сборки/подписи/публикации/скриншотов.
- **Gradle Play Publisher** — публикация в Play из Gradle.
- **Firebase App Distribution** — раздача бета-сборок тестировщикам.
- **Firebase Test Lab / Gradle Managed Devices** — прогон UI-тестов на матрице устройств.

## Что ускоряет CI
- **Gradle build cache** + **remote cache**, `--parallel`, конфигурационный кэш.
- **Многомодульность** — пересборка только изменённого. См. [[Multi-module architecture]].
- Кэш зависимостей между запусками.
- Разделение: быстрые проверки (lint+unit) на каждый PR, тяжёлые UI-тесты — по расписанию/на merge.

## Безопасность в CI
- Keystore, `google-services`, API-ключи, service account — **в секретах CI**, не в git. См. [[Security. SSL Pinning, KeyStore, secrets]].
- Подпись release только на защищённом раннере.

## Публикация приложения (senior-вопрос «опишите деплой»)
1. Поднять `versionCode`/`versionName`.
2. Собрать подписанный **AAB** с включённым R8/минификацией. См. [[R8 and ProGuard. Minification and obfuscation]].
3. Прогнать тесты, залить mapping-файл (для деобфускации крашей).
4. Загрузить в Play Console → трек (internal/testing → production).
5. **Staged rollout** (5% → 20% → 100%), мониторинг Android Vitals/Crashlytics, при регрессе — halt/rollback.

Связано: [[Git]], [[Testing]], [[Multi-module architecture]], [[R8 and ProGuard. Minification and obfuscation]]
