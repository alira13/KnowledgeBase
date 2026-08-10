# Обобщённые (параметризованные) функции

Функция, у которой тип параметра/возврата задаётся **параметром типа** `<T>`. Логика одна — типы разные, с сохранением типобезопасности. См. основы дженериков: [[Generics. Basics]].

## Синтаксис
Параметр типа объявляется **перед** именем функции:
```kotlin
fun <T> firstOrNull(list: List<T>): T? = if (list.isEmpty()) null else list[0]

val n: Int?    = firstOrNull(listOf(1, 2, 3))     // T = Int
val s: String? = firstOrNull(listOf("a", "b"))    // T = String
```
`T` может быть nullable (`T?`), несколько параметров — `<K, V>`.

## Ограничения (bounds)
```kotlin
fun <T : Comparable<T>> maxOf(a: T, b: T): T = if (a > b) a else b   // T обязан быть Comparable
maxOf(3, 7)          // 7
maxOf("a", "b")      // "b"
```
Несколько границ — через `where`:
```kotlin
fun <T> copyWhenReady(src: T, dst: T) where T : CharSequence, T : Appendable { /* ... */ }
```

## reified — тип в рантайме (обход стирания)
Из-за [[Generics. Type erasure|стирания типов]] внутри обычной обобщённой функции `T` в рантайме недоступен. В **inline**-функции можно пометить `reified` и получить реальный тип:
```kotlin
inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, T::class.java)          // T::class доступен благодаря reified

inline fun <reified T> List<*>.filterIsInstance(): List<T> =
    filter { it is T } as List<T>          // проверка is T возможна
```
Подробнее — [[Functions. Inline, noinline, crossinline, reified]].

## Extension-функции тоже бывают обобщёнными
```kotlin
fun <T> T.also(block: (T) -> Unit): T { block(this); return this }   // так устроен also
```

## Зачем (на собесе)
- Переиспользование без дублирования и без приведения типов.
- Bounds дают доступ к методам ограничивающего типа (`Comparable`, `Number`).
- `reified` — единственный способ узнать тип-параметр в рантайме (только в inline).

Связано: [[Generics. Basics]], [[Generics. Variance (in, out)]], [[Generics. Type erasure]], [[Functions. Inline, noinline, crossinline, reified]]
