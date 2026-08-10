Когда нужно посчитать ЧТО-ТО внутри подпоследовательности

Пример 
Дан массив, найдите среднее значение каждого субмассива размером «K» смежных элементов в нем.
Простейшее решение
```kotlin
 public double[] findAverages(int K, int[] arr) {
    double[] result = new double[arr.length - K + 1];
    //first cycle
    for (int i = 0; i <= arr.length - K; i++) {
      // find sum of next 'K' elements
      double sum = 0;
      //second cycle
      for (int j = i; j < i + K; j++)
        sum += arr[j];
      result[i] = sum / K; // calculate average
    }
    return result;
  }
```
Делаем цикл по всем элементам
Для i-Делаем вложенный цикл размера k, суммируем там элементы и делим на k для нахождения среднего.
Временная сложность O(N\*K)
Проблема
Подход - мы уже посчитали сумму 0-5 элементов, зачем нам еще раз ее считать для элеменнтов с 1-6? Можем от той суммы просто отнять значения 0-элемента и прибавить значение 6-элемента

Оптимальное решение
Идея 
представить исходный массив в виде подмассивов которые сдвигаются и переиспользовать результат вычисления с прошлого шага

Алгоритм
Определить размер скользящего окна(как правило размер подмассива) -  у нас 5
Определить что мы можем переизспользовать -  сумма 4х элементв
Определить шаг скольжения - каждый подмассив, значит шаг 1
Определить что нужно сделать, чтобы используя предыдущий результат получить новый результат -  отнять значения первого элемента и прибавить значение последнего
![](<../../images/Pasted image 20250404142631.png>)

```kotlin
 public double[] findAverages(int K, int[] arr) {
    double[] result = new double[arr.length - K + 1];
    double windowSum = 0;
    int windowStart = 0;
    
    for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
      windowSum += arr[windowEnd]; // add the next element
      // slide the window, we don't need to slide if we've not hit the required
      // window size of 'k'
      if (windowEnd >= K - 1) {
        result[windowStart] = windowSum / K; // calculate the average
        windowSum -= arr[windowStart]; // subtract the element going out
        windowStart++; // slide the window ahead
      }
    }

    return result;
  }
```