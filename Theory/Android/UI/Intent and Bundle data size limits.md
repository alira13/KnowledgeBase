# Ограничения размера Intent и Bundle

Короткий ответ: **около 1 МБ**. Длинный — ниже, потому что лимит не там, где кажется.

## Откуда берётся лимит
Любая передача данных между компонентами (`Intent`, `Bundle`, вызов системного сервиса) идёт через **Binder**, а у binder-драйвера фиксированный буфер транзакций — **1 МБ на процесс**, общий для **всех активных транзакций** сразу.

Отсюда два неочевидных следствия:
1. Лимит не «на один Intent»: несколько параллельных передач делят один буфер. Объект в 700 КБ пройдёт в одиночку и упадёт, если рядом идёт другая транзакция.
2. Краш прилетает не в момент `putExtra`, а позже — при фактической передаче. Стек-трейс указывает не туда, где ошибка.

Превышение → **`TransactionTooLargeException`** (в логах — `!!! FAILED BINDER TRANSACTION !!!`).

```kotlin
val intent = Intent(this, DetailsActivity::class.java)
intent.putExtra("data", largeByteArray)   // риск TransactionTooLargeException
```

## onSaveInstanceState — лимит ещё жёстче
| Ориентир | Значение |
| --- | --- |
| Рекомендация Google | **50 КБ** |
| Фактический потолок (Android 7.0+) | **~500 КБ**, дальше исключение |
| До Android 7.0 | только предупреждение в логе |

Причина строгости: система хранит эти данные всё время, пока пользователь может вернуться к экрану, — по всем экранам всех приложений сразу.

## Что делать с большими данными
| Ситуация | Решение |
| --- | --- |
| Передача между экранами | передавать **id**, данные брать из Room/репозитория |
| Файл, картинка | сохранить в `cacheDir`, передать **путь** или `content://` URI |
| Состояние экрана | `ViewModel` (переживает поворот) + `SavedStateHandle` для минимума |
| Обмен между приложениями | `ContentProvider`, `FileProvider` — см. [[IPC. How two apps communicate]] |

```kotlin
// вместо самих данных передаём путь
val file = File(cacheDir, "data.bin").apply { writeBytes(largeByteArray) }
intent.putExtra("file_path", file.absolutePath)
```

## Вопросы-ловушки
- Лимит на один Intent или на процесс? → на процесс, буфер общий для всех активных транзакций.
- Почему приложение падает не всегда, а «иногда»? → буфер делится с другими транзакциями; при пустом буфере тот же объект проходит.
- Сколько можно сохранить в `onSaveInstanceState`? → технически до ~500 КБ, практически — держаться в пределах 50 КБ.

Источники: [SO: limit of Bundle in Android](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android), [Habr — TransactionTooLargeException](https://habr.com/ru/companies/dododev/articles/694746/)

Связано: [[Bundle]], [[0 Serialization. Serializable vs Parcelable]], [[0 App components. Intent]], [[IPC. How two apps communicate]]
