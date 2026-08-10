Когда нужно найти пересекающиеся интервалы или объединить интервалы, которые пересекаются

Задача
Дан список интервалов, объединить все перекрывающиеся интервалы и создайте результирующий список в котором нет пересечений

Решение:
1. Отсортировать интервалы на основе их начального элемента(например с помощью компаратора)
2. Установите значения start и end из значений первого интервала. 
3. Создаем цикл по количеству интервалов. В цикле
Конечное значение первого интервала больше первого значения второго интервала, значит объединяем(устанавливаем новые start и end)
Если конечное значение меньше, значит объединения не требуется, добавляем значения start end в результат
Переходим на следующую итерацию
4. Добавить последний интервал в список
Сложность
nLog(n) - быстрая сортировка
прохождение по циклу O(n)
Наихудшая nLog(n)
```kotlin
/*class Interval {
  int start;
  int end;

  public Interval(int start, int end) {
    this.start = start;
    this.end = end;
  }

};*/

  
  public List<Interval> merge(List<Interval> intervals) {

    if (intervals.size() < 2)
      return intervals;

    // sort the intervals by start time
    Collections.sort(intervals, (a, b) -> Integer.compare(a.start, b.start));
    List<Interval> mergedIntervals = new LinkedList<Interval>();
    Iterator<Interval> intervalItr = intervals.iterator();
    Interval interval = intervalItr.next();
    int start = interval.start;
    int end = interval.end;

    while (intervalItr.hasNext()) {
      interval = intervalItr.next();
      if (interval.start <= end) { // overlapping intervals, adjust the 'end'
        end = Math.max(interval.end, end);
      } else { // non-overlapping interval, add the previous interval and reset
        mergedIntervals.add(new Interval(start, end));
        start = interval.start;
        end = interval.end;
      }
    }

    // add the last interval
    mergedIntervals.add(new Interval(start, end));

    return mergedIntervals;
  }
```

![](<images/Pasted image 20250404150427.png>)