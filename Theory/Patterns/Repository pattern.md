# Паттерн Repository (Репозиторий)

**Repository** — абстракция над источниками данных. Скрывает от домена/презентации, *откуда* берутся данные (сеть, БД, кэш, память), и отдаёт доменные модели. Единая точка входа к данным конкретной сущности.

## Зачем
- **Разделение ответственности**: presentation/domain не знает про Retrofit/Room/DataStore.
- **Single Source of Truth (SSOT)** — обычно локальная БД: сеть пишет в БД, UI читает из БД (`Flow`). Оффлайн-режим «из коробки».
- **Тестируемость**: репозиторий за интерфейсом легко подменить фейком. См. [[Testing]].
- Место для стратегии кэширования, объединения источников, мапперов DTO↔domain.

## Где живёт в Clean Architecture
- **Интерфейс** репозитория — в `domain` (домен диктует контракт).
- **Реализация** — в `data`, зависит от data sources. Это соблюдает Dependency Inversion. См. [[Clean architecture]].

```kotlin
// domain
interface RecipeRepository {
    fun observeRecipes(): Flow<List<Recipe>>
    suspend fun refresh(): Result<Unit, DataError>
}

// data
class RecipeRepositoryImpl(
    private val api: RecipeApi,        // remote data source
    private val dao: RecipeDao,        // local data source
    private val mapper: RecipeMapper,
) : RecipeRepository {

    override fun observeRecipes() =            // SSOT — читаем из БД
        dao.observeAll().map { it.map(mapper::toDomain) }

    override suspend fun refresh(): Result<Unit, DataError> =
        safeCall { api.getRecipes() }
            .onSuccess { dtos -> dao.upsert(dtos.map(mapper::toEntity)) }
            .map { }
}
```

## Частые вопросы
- **Должен ли быть синглтоном?** Обычно да — один экземпляр на приложение (`@Singleton`/`single`), чтобы делить кэш/in-memory состояние и не плодить подключения. Но синглтон не потому что «репозиторий», а потому что состояние/ресурсы общие; сам паттерн этого не требует.
- **Repository vs DAO?** DAO — низкоуровневый доступ к одной таблице; репозиторий агрегирует несколько источников (DAO + API + кэш) и возвращает доменные модели.
- **Не делать репозиторий «богом»**: один репозиторий — одна доменная область, а не все данные приложения.

Связано: [[Clean architecture]], [[Domain layer and technologies]], [[Databases]], [[Testing]]
