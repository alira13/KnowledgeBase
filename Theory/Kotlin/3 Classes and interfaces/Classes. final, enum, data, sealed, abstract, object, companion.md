- [ ] final, 
- [x] enum
- [x] data
- [ ] sealed, 
- [x] abstract
- [x] object
- [ ] companion object

- Иерархии классов в Kotlin
- Как бывают конструкторы в Kotlin?
- Unit
- Nothing
- Блок init
- Ключевое слово object
- companion object
- Object expressions
- Может ли быть два экземпляра `object`?
- Nested class
- Inner class
- Data class
- Sealed class
- Разница между `inline` classes и `type aliases`?

Companion object
Статическая область внутри класса. Код внутри него относится не к объекту а к типу класса.

Singleton живет столько сколько живет контекст в котором он вызван

### Object 
Когда мы помечаем класс как object, он будет синглтоном. Под капотом у kotlin реализация singleton для этого класса
**Срок жизни Singleton в Android** зависит от способа реализации и контекста использования. Вот ключевые аспекты:

## **1. Стандартный Singleton (без привязки к Application Context)**

Если Singleton инициализируется **в Activity** или **другом компоненте**, его экземпляр может быть уничтожен при:

- **Перезапуске Activity** (например, при изменении ориентации экрана).
- **Сборке мусора**, если ссылки на экземпляр теряются.
    

**Пример реализации** (из[1](https://ru.stackoverflow.com/questions/488660/singleton-%D0%B2-android-%D0%B7%D0%BB%D0%BE)):

java

`public class Singleton {     private static Singleton instance;    public static Singleton getInstance() {        if (instance == null) instance = new Singleton();        return instance;    } }`

**Проблема**: Если Singleton создан в Activity, его экземпляр может быть удалён при завершении Activity, а при следующем вызове `getInstance()` будет создан новый экземпляр[1](https://ru.stackoverflow.com/questions/488660/singleton-%D0%B2-android-%D0%B7%D0%BB%D0%BE).

## **2. Singleton, привязанный к Application Context**

Если экземпляр Singleton инициализируется **в подклассе Application** (например, `MyApp`), он живёт **столько, сколько приложение**:

java

`public class MyApp extends Application {     @Override    public void onCreate() {        MySingleton.initInstance();    } }`

**Особенности**:

- Экземпляр сохраняется до завершения приложения.
    
- Не зависит от жизненного цикла Activity[1](https://ru.stackoverflow.com/questions/488660/singleton-%D0%B2-android-%D0%B7%D0%BB%D0%BE)[2](https://sky.pro/media/singleton-vs-application-context-v-android/).
    

## **3. Потокобезопасность и ленивая инициализация**

Для корректной работы в многопоточной среде используйте **двойную проверку блокировки** или **volatile**:

java

`public class Singleton {     private static volatile Singleton instance;    public static Singleton getInstance() {        if (instance == null) {            synchronized (Singleton.class) {                if (instance == null) instance = new Singleton();            }        }        return instance;    } }`

**Важно**: Даже с синхронизацией экземпляр может быть удалён, если ссылки на него потеряны[6](https://habr.com/ru/articles/335006/).

## **4. Сравнение с Application Context**

|**Критерий**|**Singleton**|**Application Context**|
|---|---|---|
|**Срок жизни**|Зависит от реализации|Живёт столько, сколько приложение|
|**Управление**|Самостоятельно контролирует экземпляр|Автоматически управляется системой|
|**Риски**|Утечки памяти, сложности тестирования|Утечки контекста, сложность управления|

## **Рекомендации**

1. **Используйте Application Context** для глобальных объектов, если требуется сохранение данных между Activity[2](https://sky.pro/media/singleton-vs-application-context-v-android/).
    
2. **Избегайте Singleton** для компонентов с сложным жизненным циклом (например, баз данных). Предпочтительнее **Dependency Injection** или **ViewModel**.
    
3. **Тестируемость**: Singleton усложняет тестирование из-за глобального состояния[2](https://sky.pro/media/singleton-vs-application-context-v-android/)[6](https://habr.com/ru/articles/335006/).
    

**Итог**: Singleton в Android может жить **от нескольких секунд (если привязан к Activity)** до **всего времени работы приложения (если инициализирован в Application)**. Для надёжности предпочтительнее использовать **Application Context** или современные подходы (Jetpack, DI).

### Citations:

1. [https://ru.stackoverflow.com/questions/488660/singleton-%D0%B2-android-%D0%B7%D0%BB%D0%BE](https://ru.stackoverflow.com/questions/488660/singleton-%D0%B2-android-%D0%B7%D0%BB%D0%BE)
2. [https://sky.pro/media/singleton-vs-application-context-v-android/](https://sky.pro/media/singleton-vs-application-context-v-android/)
3. [https://javarush.com/groups/posts/589-patternih-i-singleton--dlja-vsekh-kto-vpervihe-s-nimi-stolknulsja](https://javarush.com/groups/posts/589-patternih-i-singleton--dlja-vsekh-kto-vpervihe-s-nimi-stolknulsja)
4. [https://swiftbook.org/pages/1468/](https://swiftbook.org/pages/1468/)
5. [https://ru.wikipedia.org/wiki/%D0%9E%D0%B4%D0%B8%D0%BD%D0%BE%D1%87%D0%BA%D0%B0_(%D1%88%D0%B0%D0%B1%D0%BB%D0%BE%D0%BD_%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F)](https://ru.wikipedia.org/wiki/%D0%9E%D0%B4%D0%B8%D0%BD%D0%BE%D1%87%D0%BA%D0%B0_\(%D1%88%D0%B0%D0%B1%D0%BB%D0%BE%D0%BD_%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F\))
6. [https://habr.com/ru/articles/335006/](https://habr.com/ru/articles/335006/)
7. [https://habr.com/ru/companies/otus/articles/493802/](https://habr.com/ru/companies/otus/articles/493802/)
8. [https://javarush.com/groups/posts/4085-kofe-breyk-214-kak-sproektirovatjh-klass-singleton--tochka-zrenija-intervjhjuera-poljhzovateljh](https://javarush.com/groups/posts/4085-kofe-breyk-214-kak-sproektirovatjh-klass-singleton--tochka-zrenija-intervjhjuera-poljhzovateljh)

---

Answer from Perplexity: [pplx.ai/share](https://www.perplexity.ai/search/pplx.ai/share)

### Enum - класс-перечисление

`enum`  - это класс, который содержит в себе все свои возможные экземпляры. Пример дни недели. Это классы, которые предназначены для работы с ограниченными наборами значений одинакового типа. Внутри enum создаются все экземпляры значений большими буквами.
Удобно использовать с конструкцией when.
Также как и другие классы могут иметь поля и методы
От enum нельзя наследоваться. Ведь мы сразу перечисляем все экземпляры класса.

```kotlin
//объявить класс enum
enum class Colors(val rusName:String){  
    RED("Красный"), GREED("Зеленый"), BLUE("Желтый")  
}  

// получить все значения класса
val colors = Colors.values()

// Обратиться к объекту enum и его полю
val redName = Colors.RED.rusName
```

- **`enum`**:
    - Используется для представления **фиксированного набора однотипных значений**.
    - Например, дни недели, времена года, направления.
- **`sealed class`**:
    - Используется для создания **иерархии типов**, где все возможные подклассы известны на этапе компиляции. То есть sealed - это класс-родитель. А внутри него описываются все его возможные потомки разных типов
    - Например, модель состояний, ошибок или событий.
Пример 
в java есть класс Month. Он enum

### Abstract class
2. Когда нужен шаблон класса как родитель, но объектов этот класс иметь не может. Он существует только чтобы вынести все общие черты классов-наследников в единую сущность. Нужно чтобы создание объекта родителя было запрещено
3. Так как класс абстрактный, то он нужен только для наследования, значит ключевое слово open не нужно. **Все абстрактные классы открыты для наследования**

Методы абстрактных классов
 - Не обязательно все абстрактные
 - НО если нужна абстрактная функция, то класс тоже должен быть абстрактным
 - Если помечаем абстрактным, то этот метод ОБЯЗАТЕЛЕН к переопределению в дочернем классе
 - Абстрактные методы должны быть ТОЛЬКО внутри абстрактного класса

### Data class
 - Класс для хранения данных, dto или models
 - Автоматически генерятся методы equals, hashCode, copy, toString
 - для каждого свойства, указанного в первичном конструкторе автоматически генерятся методы Component1 Component2 ComponentN  которые позволяют вызывать деструктурирующий оператор который представлячет объект  в виде набора полей первичного конструктора. из-за этого нельзя наследоваться, непонятно кто будет Component1 Component2
 Вызов конструктора - создание объекта из параметров
 Деструктуризация - получение набора параметров из объекта
```kotlin
while(var8.hasNext()) {  
   MyPair var3 = (MyPair)var8.next();  
   String en = var3.component1();  
   String fr = var3.component2();  
   String var6 = en + '-' + fr;  
   System.out.println(var6);  
}
```
 - Необходимо наличие первичного конструктора с полями, так как именно на основе этих полей генерятся методы equals, hashCode, toString, copy(в аргументах все поля первичного конструктора)
 - от data-классов нельзя наследоваться, потому что на основании полей в первичном конструкторе в них генерится много кода и в наследниках может возникнуть путаница
 - абстрактные классы не могут быть data-классами
![](<images/Pasted image 20250318144847.png>)

![](<images/Pasted image 20250318154805.png>)![](<images/Pasted image 20250318154854.png>)


Sealed class
**Sealed класс** в Kotlin — это специальный тип класса, который позволяет создавать ограниченную иерархию подклассов. Он используется для определения замкнутого набора возможных типов, что обеспечивает безопасность и предсказуемость кода.

## Основные характеристики

1. **Ограниченное наследование**  
    Sealed классы могут быть унаследованы только в том же файле, где они определены. Это ограничивает возможность добавления новых подклассов из других частей программы[1](https://apptractor.ru/info/techhype/chto-takoe-sealed-klass-voprosy-s-sobesedovaniy.html)[3](https://foxminded.ua/ru/kotlin-sealed/).
    
2. **Известные подклассы**  
    Все возможные подклассы sealed класса должны быть определены явно, что позволяет компилятору проверять полноту обработки всех возможных вариантов[1](https://apptractor.ru/info/techhype/chto-takoe-sealed-klass-voprosy-s-sobesedovaniy.html)[3](https://foxminded.ua/ru/kotlin-sealed/).
    
3. **Использование с when**  
    Sealed классы часто используются с оператором `when`, чтобы гарантировать обработку всех возможных типов. Компилятор не требует дополнительного блока `else`, так как все случаи уже учтены[1](https://apptractor.ru/info/techhype/chto-takoe-sealed-klass-voprosy-s-sobesedovaniy.html)[5](https://dzen.ru/a/ZK6VI8vkSn3T9NCW).
    

## Пример использования

kotlin

`sealed class Result {     data class Success(val data: String) : Result()    data class Error(val error: Throwable) : Result()    object Loading : Result() } fun handleResult(result: Result) {     when (result) {        is Result.Success -> println(result.data)        is Result.Error -> println(result.error.message)        is Result.Loading -> println("Loading...")    } }`

В этом примере `Result` — sealed класс с тремя возможными состояниями: `Success`, `Error` и `Loading`. Когда используется `when`, компилятор проверяет, что все возможные типы обработаны.

## Преимущества

- **Безопасность типов**: гарантирует, что все возможные случаи учтены.
    
- **Предсказуемость**: ограничивает набор возможных подклассов.
    
- **Оптимизация кода**: компилятор может оптимизировать код, зная все возможные подклассы[3](https://foxminded.ua/ru/kotlin-sealed/)[6](https://easyoffer.ru/question/2841).
    

## Области применения

- **Управление состояниями**: для представления различных состояний в приложении (например, загрузка, успех, ошибка)[1](https://apptractor.ru/info/techhype/chto-takoe-sealed-klass-voprosy-s-sobesedovaniy.html).
    
- **Обработка результатов**: для представления результатов операций (успех или ошибка)[1](https://apptractor.ru/info/techhype/chto-takoe-sealed-klass-voprosy-s-sobesedovaniy.html).
    
- **Событийная обработка**: для типизированной обработки событий в системах[1](https://apptractor.ru/info/techhype/chto-takoe-sealed-klass-voprosy-s-sobesedovaniy.html).
    

### Citations:

1. [https://apptractor.ru/info/techhype/chto-takoe-sealed-klass-voprosy-s-sobesedovaniy.html](https://apptractor.ru/info/techhype/chto-takoe-sealed-klass-voprosy-s-sobesedovaniy.html)
2. [https://itproger.com/course/kotlin/10](https://itproger.com/course/kotlin/10)
3. [https://foxminded.ua/ru/kotlin-sealed/](https://foxminded.ua/ru/kotlin-sealed/)
4. [https://habr.com/ru/articles/728742/](https://habr.com/ru/articles/728742/)
5. [https://dzen.ru/a/ZK6VI8vkSn3T9NCW](https://dzen.ru/a/ZK6VI8vkSn3T9NCW)
6. [https://easyoffer.ru/question/2841](https://easyoffer.ru/question/2841)
7. [https://kotlinlang.ru/docs/sealed-classes.html](https://kotlinlang.ru/docs/sealed-classes.html)
8. [https://developer.alexanderklimov.ru/android/kotlin/sealed.php](https://developer.alexanderklimov.ru/android/kotlin/sealed.php)

---

Answer from Perplexity: [pplx.ai/share](https://www.perplexity.ai/search/pplx.ai/share)