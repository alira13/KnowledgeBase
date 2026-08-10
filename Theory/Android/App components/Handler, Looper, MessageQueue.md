# Handler, Looper, MessageQueue

Механизм **HaMeR** (Handler-Message-Runnable) — как устроен главный (UI) поток и как передать работу в другой поток. Частый вопрос: «как работает main thread».

## Как это работает
Обычный поток выполнил `run()` и умер. Чтобы поток **жил и обрабатывал задачи в цикле**, ему нужны:
- **Looper** — «мотор»: бесконечный цикл, который достаёт сообщения из очереди и раздаёт их обработчикам. Один Looper на поток (хранится в `ThreadLocal`).
- **MessageQueue** — очередь сообщений/задач, упорядоченная по времени выполнения.
- **Handler** — «отправитель/получатель»: кладёт `Message`/`Runnable` в очередь **своего** Looper и обрабатывает их (`handleMessage`).
- **Message** — единица работы (или `Runnable`).

```
Handler.post(task) ──▶ MessageQueue ──▶ Looper.loop() достаёт ──▶ выполняет в потоке Looper'а
```

## Главный поток
У `main` потока Looper создан системой при старте приложения (`Looper.prepareMainLooper()` + `Looper.loop()` в `ActivityThread.main`). Именно поэтому UI-поток «живёт» и обрабатывает события ввода, отрисовку, коллбэки. **Обновлять UI можно только из main-потока**, потому что там крутится его Looper.

## Передать работу на main-поток из фонового
```kotlin
val mainHandler = Handler(Looper.getMainLooper())
thread {
    val result = heavyWork()          // в фоне
    mainHandler.post { textView.text = result }   // на main-потоке
}
// отложенно:
mainHandler.postDelayed({ /* ... */ }, 2000)
```

## Свой рабочий поток с очередью — HandlerThread
```kotlin
val ht = HandlerThread("worker").apply { start() }   // создаёт поток + Looper
val bg = Handler(ht.looper)
bg.post { /* последовательные фоновые задачи */ }
// ht.quitSafely() когда не нужен — иначе поток живёт
```

## Зачем это знать (связь с современным)
- `Dispatchers.Main` в корутинах и `runOnUiThread`/`View.post` — обёртки над этим механизмом.
- **Утечка**: `Handler` (нестатический) держит ссылку на Activity; отложенное сообщение в очереди → Activity не соберётся. Решение: static + `WeakReference`, снимать колбэки `removeCallbacksAndMessages(null)` в `onDestroy`. См. [[Memory leaks. Detection]].
- Долгая работа в `handleMessage`/`post` на main → фриз/ANR. См. [[ANR. Application Not Responding]].

## Вопрос-ловушка
«Что будет, если вызвать `Looper.loop()` в обычном потоке?» → поток войдёт в бесконечный цикл обработки очереди (нужен `Looper.prepare()` до этого; выйти — `quit()`).

Связано: [[1 Activity]], [[Coroutines]], [[Memory leaks. Detection]], [[ANR. Application Not Responding]]
