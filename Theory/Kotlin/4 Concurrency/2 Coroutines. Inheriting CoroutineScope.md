функции coroutineScope и supervisorScope

```kotlin
scope.launch {   
// дочерняя корутина, использует scope-наследник  
    launch {   
          
    }  
}  
  
scope.launch {   
// не дочерняя корутина  
    scope.launch {  }  
}
```

Если нужно в какой-то функции работать на дочерней корутине, нужно оборачивать код в 
 `coroutineScope` или `supervisorScope`).