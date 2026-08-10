
### toString(Any)
Представление объектов в виде строки
Для классов по умолчанию возвращает строку Имяпакета.Имякласса@Hashcode. Это метод объекта Any, а там есть инфа только об этом.
Этот метод открыт для переопределения.

### equals(Any) ==
Сравнение объектов по ссылке(по умолчанию) или пользовательское при переопределении(например по полям)
 - по умолчанию мы сравниваем ссылки на 2 объекта. Если ссылки равны, то и объекты равны. При этом никакие свойства объекта не учитываются. 
 - Чтобы сравнивались не ссылки, а именно поля объектов, необходимо переопределить метод equals. Когда у переопределенного хотим все-таки сравнить по ссылкам, то используем ===

### hashCode(Any)
представление объекта в виде числа.
Когда мы используем коллекцию Set, то там уже сравнение идет по hashCode, а не по Equals. У Int hashCode совпадает с их значением   
 - по умолчанию hashCode равен у объектов с одинаковыми ссылками
 - чтобы hashCode был одинаков для объектов с одинаковыми полями, а не ссылками, необъодимо его также переопределить
В Android studio можно сгенерить свой hashCode. При умножении на 31 меньше вероятность, что hashCode совпадет. Дает нормальное распределение.
 
!!! Если переопределили equals, то нужно переопределять и hashCode, чтобы не было двойственности
Поиск в Set одинаковых элементов идет намного быстрее, потому что доступ идет по hashCode как в словаре.


### 🔍 **Как `contains` сравнивает элементы в Kotlin и влияние `equals` и `hashCode`**

Метод `contains` в Kotlin использует **разные стратегии сравнения** в зависимости от типа коллекции. Это влияет на его поведение, особенно если переопределены `equals` и `hashCode`.

---

## **1. List (`ArrayList`, `LinkedList`) – Использует `equals` (O(n))**

В `List` поиск выполняется **последовательным перебором** элементов, вызывая `equals` для каждого сравнения.

🔹 **Код `contains` для `ArrayList`:**

```kotlin
override fun contains(element: E): Boolean {
    return indexOf(element) >= 0
}
```

🔹 **Код `indexOf`:**

```kotlin
override fun indexOf(element: E): Int {
    for (i in indices) {
        if (element == this[i]) { // Использует equals()
            return i
        }
    }
    return -1
}
```

➡ **Вывод:** `contains` использует `equals()`, поэтому важно его корректно переопределить.

🔹 **Пример с кастомным классом:**

```kotlin
data class Person(val name: String, val age: Int)

val list = listOf(
    Person("Alice", 25),
    Person("Bob", 30)
)

println(list.contains(Person("Alice", 25))) // true, т.к. data class переопределяет equals
```

📌 `data class` автоматически создает `equals()`, поэтому поиск работает корректно.

---

## **2. HashSet – Использует `hashCode` + `equals` (O(1) в среднем)**

В `HashSet` метод `contains` сначала вычисляет **`hashCode()`**, чтобы найти элемент в хеш-таблице. Если найдены кандидаты (коллизия), выполняется `equals()`.

🔹 **Код `HashSet.contains()`:**

```kotlin
override fun contains(element: E): Boolean {
    return map.containsKey(element) // HashMap ищет ключ по hashCode()
}
```

🔹 **Алгоритм работы:**

1. **Берется `hashCode()` элемента** и вычисляется индекс в массиве бакетов.
2. **Если бакет пустой** → элемент точно отсутствует (`false`).
3. **Если бакет содержит элементы** → вызывается `equals()` для сравнения.
4. **Если `equals` возвращает `true`** → найден (`true`), иначе элемент отсутствует (`false`).

➡ **Вывод:**

- **Если `hashCode()` не переопределен корректно, поиск может работать некорректно!**
- **Если `equals()` определен неправильно, могут быть ложные совпадения или пропуски.**

🔹 **Пример с кастомным классом без `hashCode`:**

```kotlin
class Person(val name: String, val age: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false
        return name == other.name && age == other.age
    }
}

val set = hashSetOf(Person("Alice", 25))
println(set.contains(Person("Alice", 25))) // false! (т.к. hashCode() разный)
```

❌ Ошибка: объекты с одинаковыми данными попадают в **разные бакеты**.

🔹 **Правильная реализация (equals + hashCode):**

```kotlin
class Person(val name: String, val age: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false
        return name == other.name && age == other.age
    }

    override fun hashCode(): Int {
        return name.hashCode() * 31 + age
    }
}

val set = hashSetOf(Person("Alice", 25))
println(set.contains(Person("Alice", 25))) // true ✅
```

📌 `data class` автоматически генерирует правильные `equals` и `hashCode`, поэтому лучше использовать их, если логика сравнения стандартная.

---

## **3. TreeSet – Использует `compareTo` (O(log n))**

В `TreeSet` используется **красно-черное дерево**, где поиск идет через **`compareTo()`** (из `Comparable` или `Comparator`).

🔹 **Код `TreeSet.contains()`:**

```kotlin
override fun contains(element: E): Boolean {
    return getEntry(element) != null // Бинарный поиск в дереве
}
```

🔹 **Как работает?**

1. `compareTo()` определяет порядок сортировки.
2. Поиск выполняется по дереву **(O(log n))**.

➡ **Вывод:**

- Если **`compareTo()` неправильно определен**, `contains` может вести себя некорректно.

🔹 **Пример с `compareTo()`:**

```kotlin
class Person(val name: String, val age: Int) : Comparable<Person> {
    override fun compareTo(other: Person): Int {
        return age - other.age // Сортируем по возрасту
    }
}

val treeSet = sortedSetOf(
    Person("Alice", 25),
    Person("Bob", 30)
)

println(treeSet.contains(Person("Alice", 25))) // true
println(treeSet.contains(Person("Charlie", 28))) // false
```

📌 Если `compareTo` не использует все поля, могут быть ошибки. Например, если сравнивать только по `age`, люди с одинаковым возрастом будут считаться одинаковыми.

---

## **🔎 Итог: что влияет на `contains`?**

| Коллекция | Как сравнивает элементы      | Влияет `equals()`? | Влияет `hashCode()`? | Влияет `compareTo()`? |
| --------- | ---------------------------- | ------------------ | -------------------- | --------------------- |
| `List`    | Линейный поиск (`equals`)    | ✅ Да               | ❌ Нет                | ❌ Нет                 |
| `HashSet` | `hashCode()` + `equals()`    | ✅ Да               | ✅ Да                 | ❌ Нет                 |
| `TreeSet` | Бинарный поиск (`compareTo`) | ❌ Нет              | ❌ Нет                | ✅ Да                  |

📌 **Выводы:**

- **В `List` важен только `equals()`.**
- **В `HashSet` важно правильно переопределять `hashCode()` и `equals()`.**
- **В `TreeSet` важен `compareTo()`, `equals()` и `hashCode()` не используются.**
- **`data class` автоматически генерирует `equals()`, `hashCode()` и `toString()`, что упрощает использование.**

| Оператор / Метод           | Как работает?                                                                                                 | Проверяет                    | Безопасен для `null`?  | Можно переопределить? |
| -------------------------- | ------------------------------------------------------------------------------------------------------------- | ---------------------------- | ---------------------- | --------------------- |
| `==`                       | `equals()` под капотом<br>Не переопределеннй метод equals работает аналогично === То есть сравнивает ссылки.  | **Значения**                 | ✅ Да                   | ✅ Да                  |
| `equals()`<br>метод из Any | Метод<br>Не переопределеннй метод equals работает аналогично === То есть сравнивает ссылки.                   | **Значения**                 | ❌ Нет (может быть NPE) | ✅ Да                  |
| `===`                      | Проверка ссылки. Сравнивает, объект тот же или нет                                                            | **Ссылку (объект в памяти)** | ✅ Да                   | ❌ Нет                 |

### copy

![](<../../images/Pasted image 20250318145859.png>)