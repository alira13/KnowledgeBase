# ANR. Application Not Responding

**ANR** — система показывает диалог «приложение не отвечает», когда **главный (UI) поток** заблокирован слишком долго.

## Пороги срабатывания
- **Input dispatching** (касание/клавиша не обработаны) — **5 секунд**.
- **BroadcastReceiver.onReceive** — **10 секунд** (foreground) / больше для background.
- **Service** (start/bind в main thread) — **~20 секунд** (foreground).
- **ContentProvider** — таймаут publish.

## Причины
- Тяжёлая работа на main thread: сеть, дисковый I/O, БД, парсинг больших JSON, декодирование bitmap.
- Долгие блокировки/`synchronized`, ожидание lock, deadlock. См. [[Multithreading]].
- Бесконечные/тяжёлые циклы в UI-колбэках, `onDraw`.
- Медленный `BroadcastReceiver`.
- Блокирующий вызов `runBlocking` на main thread.

## Как избегать
- Всю тяжёлую работу — в фон: **корутины** с `Dispatchers.IO/Default`, `WorkManager` для отложенного. См. [[Coroutines]], [[2 Services and WorkManager]].
- Не блокировать main thread ожиданием (`Thread.sleep`, `.get()`, `runBlocking`).
- БД/сеть — асинхронно (`suspend`, `Flow`); Room возвращает `Flow`/`suspend`.
- В `BroadcastReceiver` — `goAsync()` или делегировать в WorkManager, не делать долгую работу в `onReceive`.

## Как диагностировать
- **StrictMode** — ловит disk/network на main thread в дебаге.
- **traces.txt** / `/data/anr/` — дамп стеков потоков в момент ANR (видно, что делал main thread).
- **Perfetto / Systrace** — долгие кадры.
- **Play Console → Android Vitals** — статистика ANR/крашей в проде (порог «плохого поведения»).

## Связь с производительностью
ANR — крайняя форма jank: если пропуск кадра это 16 мс, то ANR — секунды блокировки. Профилактика та же. См. [[Performance. Profiling and UI optimization]].

Связано: [[Performance. Profiling and UI optimization]], [[Coroutines]], [[1 Activity]], [[2 Services and WorkManager]]
