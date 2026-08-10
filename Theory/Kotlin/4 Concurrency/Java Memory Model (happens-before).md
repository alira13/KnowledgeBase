# Java Memory Model (happens-before)

**JMM** описывает, когда запись одного потока **гарантированно видна** другому потоку. Senior-вопрос: «почему без синхронизации один поток может не увидеть изменения другого».

## Проблема видимости
Каждый поток может кэшировать переменные (регистры/кэш ядра), а компилятор/процессор — **переупорядочивать** инструкции ради оптимизации. Поэтому без правил синхронизации:
```kotlin
var ready = false
var data = 0
// Поток A:
data = 42
ready = true
// Поток B:
while (!ready) { }
println(data)   // может напечатать 0 (!) — увидел ready=true, но не увидел data=42
```

## happens-before
Отношение «**A happens-before B**» гарантирует: всё, что сделал A **до** точки синхронизации, видно B **после**. Основные правила:
- **Программный порядок** внутри одного потока.
- **`synchronized`**: выход из блока (release) happens-before входа в тот же монитор (acquire).
- **`volatile`**: запись volatile-переменной happens-before её чтения.
- **`Thread.start()`** happens-before кода запущенного потока; код потока happens-before `join()`.
- Транзитивность: A hb B, B hb C ⇒ A hb C.

## volatile
Гарантирует **видимость** и **запрет переупорядочивания** вокруг переменной, но **не атомарность** составных операций:
```kotlin
@Volatile var ready = false   // чтение всегда «свежее», без кэша потока
// но:
@Volatile var counter = 0
counter++      // НЕ атомарно: read-modify-write, гонка. volatile не спасёт
```
Исправляет пример выше: пометить `ready` как `@Volatile` → data=42 станет видно (запись data до volatile-записи ready видна после volatile-чтения ready).

## Атомарность
Для «прочитать-изменить-записать» нужны атомарные типы или блокировки:
```kotlin
val counter = AtomicInteger(0)
counter.incrementAndGet()      // атомарно (CAS)
```
- **AtomicInteger/Long/Reference** — на CAS (compare-and-set), без блокировок.
- **`synchronized`** — взаимное исключение + happens-before. См. [[0 Threads 3 Synchronized]].

## Итог (что отвечать)
- Гонка данных = нет happens-before между записью и чтением.
- `volatile` = видимость + порядок, **не** атомарность.
- `synchronized`/`Atomic*` = видимость **и** атомарность.
- Инкремент и операции с `long/double` (на 32-бит) не атомарны без volatile/atomic.

Связано: [[0 Threads 3 Synchronized]], [[Thread safety]], [[thread, lock, mutex, deadlock]]
