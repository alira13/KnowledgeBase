# Перегрузка операторов (operator overloading)

Kotlin позволяет задать поведение операторов (`+`, `-`, `[]`, `==`, `in`, `()` и т.д.) для своих типов. Оператор — это «синтаксический сахар» над функцией с зарезервированным именем, помеченной `operator`.

## Как это работает
`a + b` компилятор превращает в `a.plus(b)`. Достаточно объявить функцию с нужным именем и модификатором `operator`:
```kotlin
data class Vec(val x: Int, val y: Int) {
    operator fun plus(o: Vec) = Vec(x + o.x, y + o.y)   // a + b
    operator fun times(k: Int) = Vec(x * k, y * k)      // a * k
}
val c = Vec(1, 2) + Vec(3, 4)   // Vec(4, 6)
```

## Основные соответствия
| Выражение | Функция |
|---|---|
| `a + b` / `a - b` / `a * b` | `plus` / `minus` / `times` |
| `a += b` | `plusAssign` (или `plus` + переприсваивание) |
| `+a` / `-a` / `!a` | `unaryPlus` / `unaryMinus` / `not` |
| `a++` / `a--` | `inc` / `dec` |
| `a[i]` / `a[i] = v` | `get` / `set` |
| `a in b` | `b.contains(a)` |
| `a()` | `invoke` (см. [[Functions. invoke operator]]) |
| `a == b` | `equals` (не требует `operator`) |
| `a > b`, `a <= b` | `compareTo` (из `Comparable`) |
| `a..b` | `rangeTo` |
| `a[i]`, деструктуризация `val (x,y)=p` | `component1()`, `component2()` |

## Примеры
```kotlin
class Matrix(val data: Array<IntArray>) {
    operator fun get(i: Int, j: Int) = data[i][j]        // m[i, j]
    operator fun set(i: Int, j: Int, v: Int) { data[i][j] = v }
}

data class Money(val cents: Int) : Comparable<Money> {
    override fun compareTo(other: Money) = cents.compareTo(other.cents)  // <, >, <=
}
Money(100) > Money(50)   // true — через compareTo
```

## Правила и грабли
- Имя функции фиксировано, число параметров фиксировано — «придумать» новый оператор нельзя.
- `operator` обязателен, иначе обычная функция.
- Не злоупотреблять: перегрузка должна быть **интуитивной** (`+` для сложения векторов — ок; для «отправить запрос» — плохо).
- `equals`/`compareTo` должны быть согласованы (если `a == b`, то `compareTo` == 0).

Связано: [[Functions. invoke operator]], [[Classes. toString, equals, hashCode, copy]], [[Functions. Infix]]
