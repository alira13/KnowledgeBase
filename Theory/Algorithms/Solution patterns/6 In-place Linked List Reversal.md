Когда нужно инвертировать связный список без использования доп памяти

```kotlin
public ListNode reverse(ListNode head) {

    ListNode current = head; // current node that we will be processing
    ListNode previous = null; // previous node that we have processed
    ListNode next = null; // will be used to temporarily store the next node 

    while (current != null) {
      next = current.next; // temporarily store the next node
      current.next = previous; // reverse the current node
      // before we move to the next node, point previous to the current node
      previous = current;
      current = next; // move on the next node
    }

    // after the loop current will be pointing to 'null' and 'previous' will be the
    // new head
    return previous;
  }
```

Инициализируйте пустой стек. Итерация над цепочкой скобок. Если текущий символ является открытием скобки, нажмите его на стек. Если текущий символ является заключительной скобкой, проверьте верхнюю часть стека. Если стек пуст, то строка не сбалансирована (есть закрывающая скобка без соответствующей скобок открытия), поэтому верните ложь. Если верхняя часть стека является соответствующей скобкой скобкой открытия, выпейте его из стека. Если верхняя часть стека не является соответствующей скобкой скобкой открытия, то строка не сбалансирована, поэтому верните ложь. После проверки всех скобок, если стек пуст, то строка сбалансирована, поэтому верните True. Если стек не является пустым, то есть непревзойденные открытые скобки, поэтому строка не сбалансирована, верните ложь.

В Kotlin работаем с `Deque meaning "double-ended queue"`

```kotlin
val stack = ArrayDeque(listOf(1, 2, 3)) // stack: [1, 2, 3]
stack.addLast(0)                        // stack: [1, 2, 3, 0]         (push)
val value = stack.removeLast()          // value: 0, stack: [1, 2, 3]  (pop)
```
