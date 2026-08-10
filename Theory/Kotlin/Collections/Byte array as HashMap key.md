# Массив байтов (byte[]) как ключ HashMap

Каверзный вопрос: «можно ли использовать `byte[]` ключом в `HashMap`?»

## Нельзя напрямую — почему
У массивов **не переопределены `equals()` и `hashCode()`** — они наследуют реализацию от `Object`:
- `equals()` сравнивает **ссылки** (`==`), а не содержимое → `byte[]{1,2,3}` и другой `byte[]{1,2,3}` считаются **разными** ключами.
- `hashCode()` зависит от идентичности объекта (ссылки), а не от данных.

Итог: положив значение по одному массиву, вы не достанете его по другому массиву с тем же содержимым. То же для `HashSet`, ключей любых хеш-структур. Это следствие [[Classes. toString, equals, hashCode, copy|контракта equals/hashCode]].

## Как правильно
**1. Обернуть в `ByteBuffer`** — у него `equals()`/`hashCode()` по содержимому:
```java
Map<ByteBuffer, String> map = new HashMap<>();
map.put(ByteBuffer.wrap(new byte[]{1,2,3}), "Value");
map.get(ByteBuffer.wrap(new byte[]{1,2,3})); // "Value"
```

**2. Своя обёртка на `Arrays.equals()` / `Arrays.hashCode()`**:
```java
class ByteArrayWrapper {
    private final byte[] data;
    ByteArrayWrapper(byte[] data) { this.data = data; }
    @Override public boolean equals(Object o) {
        return o instanceof ByteArrayWrapper w && Arrays.equals(data, w.data);
    }
    @Override public int hashCode() { return Arrays.hashCode(data); }
}
```

**В Kotlin** — обёрткой удобно сделать `data class` с полем `List<Byte>` (у `List` корректные equals/hashCode) или использовать `String`/`ByteBuffer`. Обычный `data class` с полем `ByteArray` **не** сработает: у `ByteArray` те же проблемы, поэтому у data class с массивом нужно вручную переопределять equals/hashCode.

Связано: [[Classes. toString, equals, hashCode, copy]], [[Collections. Overview]], [[ArrayList]]
