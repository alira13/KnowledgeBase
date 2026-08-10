# Memory leaks. Утечки памяти и их поиск

**Утечка памяти** — объект больше не нужен, но на него сохраняется strong-ссылка от долгоживущего корня, поэтому [[Garbage collector|GC]] не может его собрать. Со временем — рост потребления памяти и `OutOfMemoryError`.

## Типичные причины в Android
- **Ссылка на `Activity`/`Context`/`View` из долгоживущего объекта**: static-поле, синглтон, объект уровня `Application`, кэш.
- **Нестатический внутренний класс / анонимный** (Handler, Runnable, listener) держит неявную ссылку на внешний класс (Activity). См. [[Classes. Inner and Nested]].
- **`Handler` с отложенными сообщениями** — сообщение в очереди держит Handler → Activity, пока не выполнится.
- **Корутина/подписка не отменена** при уничтожении экрана (нет `viewModelScope`/`lifecycleScope`).
- **`ViewModel` держит View/Context** — грубая ошибка. См. [[1 ViewModel, ViewModelProvider]].
- **Незакрытые ресурсы**: `Cursor`, `InputStream`, `BroadcastReceiver` (не `unregister`), listeners, `WebView`.
- **Bitmap** — крупные, не освобождённые.
- Ссылка на `View` во фрагменте после `onDestroyView` (binding не обнулён).

## Как искать (инструменты — частый вопрос)
- **LeakCanary** — автоматически детектит утечки Activity/Fragment/ViewModel, строит **leak trace** (цепочку ссылок от GC root до утёкшего объекта). Главный инструмент.
- **Android Studio Memory Profiler** — снимок кучи (heap dump), поиск объектов, которых не должно быть, dominators, аллокации.
- **StrictMode** (`detectLeakedClosableObjects`, `detectActivityLeaks`).
- Анализ **heap dump** в MAT (Memory Analyzer): найти по классу все живые Activity, проследить путь до GC root.

## Как искать «руками» (алгоритм на собесе)
1. Воспроизвести (открыть/закрыть экран N раз, повернуть).
2. Форсировать GC, снять heap dump.
3. Найти живые экземпляры Activity/Fragment, которых быть не должно.
4. По **shortest path to GC root** понять, кто держит.
5. Разорвать ссылку.

## Как предотвращать
- Использовать **applicationContext**, где не нужен UI-контекст.
- Корутины — только в scope, привязанном к жизненному циклу (`viewModelScope`, `lifecycleScope`, `repeatOnLifecycle`).
- Отписываться/освобождать в парном колбэке (`onDestroy`/`onDestroyView`/`DisposableEffect.onDispose`).
- Обнулять `binding` в `onDestroyView`.
- `WeakReference` для необязательных ссылок на Context/View.
- Статические Handler + `WeakReference`, снимать колбэки `removeCallbacksAndMessages`.

Связано: [[Garbage collector]], [[Performance. Profiling and UI optimization]], [[Context]], [[Stack and heap]]
