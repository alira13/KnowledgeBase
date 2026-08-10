Бинарный поиск - это алгоритм, который принимает на вход отсортированный список элементов. Если элемент присутствует в списке, то поиск возвращает позицию, иначе возвращает null.

**Принцип**:

1. Находим элемент, находящийся в середине списка
2. Сравниваем средний элемент с искомым
3. Если искомое значение меньше среднего элемента, анализируем левую часть списка, иначе правую.
4. Повторяем, пока левый индекс+1 меньше правого.

Таким образом каждый раз исключается половина массива.

```Kotlin
fun main(){
    val array1 = arrayOf(1,2,3,4,5)
    val array2 = arrayOf(1,2,2,4,5)
    val array3 = arrayOf(1,1,3,4,5)
    val array4 = arrayOf(1,2,3,4,4)
    val array5 = arrayOf(1,1,1,1,1)
    val array6 = arrayOf(6,6,6,6,6)
    val array7 = arrayOf(1,2,2,3)

    val nArray = arrayOf(1,2,3,4,5)
    val array = array1
    binarySearch(array, nArray[0])
    array.binarySearch(nArray[0])
}

fun binarySearch(sortedArray: Array<Int>, num:Int, start: Int =0, end: Int =sortedArray.size):Int{
    var startIndex=start
    var endIndex=end+1
    var currentIndex = 0;
    var i=0;
    while (startIndex+1<endIndex){
        i++
        currentIndex = (startIndex+endIndex)/2
        if(num<sortedArray[currentIndex]){
            endIndex = currentIndex
        }
        else{
            startIndex = currentIndex
        }
        println("Всего итераций $i")
        return endIndex
    }
}
```