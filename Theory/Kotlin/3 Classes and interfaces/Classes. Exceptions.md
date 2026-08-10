когда происходит внештатная ситуация, программа завершается некорректно, говорят, что выбрасывается исключение
По сути исключения - это обычные классы в kotlin, у которых есть своя иерархия 
наследования.
![](<../../images/Pasted image 20250211225334.png>)
Исключение можно
 - создать val exception = AriphmeticException()
 - бросить throw AriphmeticException()
 - впоймать 
 ```
 try{
 // попытайся что-то сделать
 }
 catch(arException:AriphmeticException){
 // впоймал исключение типа AriphmeticException, сделай что-то
 }
 catch(commonException:Throwable){
 // впоймал общее исключение, сделай что-то
 }
```

Самые популярные исключения
NotEmplementedError(TODO)
OutOfBoundsException
NullPointerException