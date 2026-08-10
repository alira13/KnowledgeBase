![](<images/Pasted image 20250327173802.png>)



Какой контекст когда использовать?
Сколько объект контекста должен жить и когда уничтожиться?

## [](https://vtsen.hashnode.dev/android-context-101-with-class-diagram#heading-context-class-diagram "Permalink")Context Class Diagram

![](https://cdn.hashnode.com/res/hashnode/image/upload/v1679136942997/e31d9795-97df-42fe-9f36-017c7a4cf16f.png?auto=compress,format&format=webp)

На приведенной выше диаграмме классов вы можете сказать, что активити, сервисы и application наследуются от контекста.

Как вы можете видеть, существует контекст, который расширяет контекст, который расширяет контекст. Вот краткие описания всех 3 классов. 

Контекст - это абстрактный класс, который позволяет приложению Android получать доступ к системным ресурсам и взаимодействовать с операционной системой 
ContextWrapper - это удобный способ изменить поведение объекта контекста, например, добавление дополнительных функций или переопределение существующего поведения, без изменения исходного контекста. Самая важная вещь, которую нужно понять здесь, - это то, что ConstrenceWrapper имеет переменную члена MBASE, которая содержит ссылку на контекст (называемый базовым контекстом) из системы Android, которая создает ее. Например, когда активити создается системой Android, вновь созданный контекст (из системы Android) передается в контекстуру (удерживаемый переменной члена MBASE). 

Таким образом, после создания деятельности теперь состоит из 3 объектов контекста: Контекст приложения - можно получить, вызывая GetApplicationContext (). Это тот же экземпляр объекта на протяжении всего приложения. 
Базовый контекст - можно получить, вызывая getBaseContext (). Это недавно созданный контекст каждый раз, когда создается активити. 
Контекст активити - сама деятельность - это контекст, который может быть извлечен этим в рамках деятельности. Это также недавно созданный контекст, и он отличается от базового контекста. 

Аналогично созданию сервисом, которое также состоит из 3 объектов контекста, за исключением контекста активити, заменяется контекстом сервиса. Существует также контекст представления и фрагмента, но они не относятся к JetPack Compose.


## [](https://vtsen.hashnode.dev/android-context-101-with-class-diagram#heading-which-context-to-use "Permalink")Which Context to Use?

Now you know there are 3 types of Context, Application Context, Activity Context and Service Context. The important concept of which context to use is to understand its lifecycle.

### [](https://vtsen.hashnode.dev/android-context-101-with-class-diagram#heading-application-context "Permalink")Application Context

Если у нас есть синглтон, который живет на протяжении жизненного цикла всего приложения, то нужно использовать AppContext - пример Room
### [](https://vtsen.hashnode.dev/android-context-101-with-class-diagram#heading-activity-context "Permalink")Activity Context
Живет на протяжении ЖЗ Activity.
### [](https://vtsen.hashnode.dev/android-context-101-with-class-diagram#heading-service-context "Permalink")Service Context

Живет пока у сервиса не вызван метод onDestroy
### [](https://vtsen.hashnode.dev/android-context-101-with-class-diagram#heading-base-context "Permalink")Base Context

Контекст от Android-системы. Хз зачем создан
## [](https://vtsen.hashnode.dev/android-context-101-with-class-diagram#heading-conclusion "Permalink")Conclusion
 Когда мы передаем ActivityContext в синглтон любой(который будет жить на протяжении жизни всего приложения), это приведет к утечкам памяти. Потому что В синглтоне будет использоваться этот контекст, а в активити каждый раз при создании заново будет создаваться новый контекст и никогда не уничтожаться
С другой стороны если мы наоборот паередадим AppContext в UI-объект, мы можем поймать проблемы связанные с тем, что некоторые компоненты или ресурсы еще не будут доступны.
Если неправильно передать в сервис контекст. То будут проблемы на этапе bind
## Процесс создания контекста

1. **Запуск приложения**: Когда приложение запускается, система создает процесс для приложения и запускает экземпляр класса **Application** который является реализацией абстрактного класса Context.
Класс **Application** в Android создается при запуске приложения, до создания любой активности или службы. Этот процесс происходит следующим образом:
2. **Запуск приложения**: Когда приложение запускается, система Android создает новый процесс для приложения.
3. **Создание экземпляра класса Application**: В этом процессе создается экземпляр класса **Application** (или его наследника, если он определен в манифесте). Это происходит до запуска любой активности.
4. **Вызов метода `onCreate()`**: После создания экземпляра класса **Application** вызывается метод `onCreate()`, который позволяет инициализировать глобальные ресурсы и настройки приложения.
5. **Создание Application Context**: В этот момент создается Application Context, который предоставляет глобальный контекст для приложения.
6. **Запуск первой активности**: После этого запускается первая активность, и создается Activity Context для этой активности.

Контекст - это абстрактный класс в Android, который предоставляет глобальную информацию о среде приложения, включая ресурсы и темы приложения. Проще говоря, он представляет текущее состояние приложения и его окружающую среду. Это ссылка на окружение, в которой в настоящее время работает ваше приложение. Контекст обеспечивает доступ к различным ресурсам, таким как базы данных, sharedPref и services службы.


**Context** в Android — это базовый абстрактный класс, который предоставляет доступ к базовым функциям приложения и служит для выполнения операций на уровне приложения. Он позволяет получить доступ к ресурсам, базам данных, настройкам и другим компонентам приложения. Контекст необходим для запуска активностей, отправки широковещательных сообщений и выполнения других действий, связанных с приложением[1](https://habr.com/ru/articles/421115/)[2](https://ievetrov.ru/%D1%81%D0%BE%D0%B1%D0%B5%D1%81%D0%B5%D0%B4%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-android/%D0%BA%D0%BE%D0%BD%D1%82%D0%B5%D0%BA%D1%81%D1%82-%D0%B8-%D0%B5%D0%B3%D0%BE-%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5/)[3](https://dolbodub.blogspot.com/2014/04/blog-post.html).

## Основные функции Context

1. **Доступ к ресурсам**: Контекст позволяет получить доступ к ресурсам приложения, таким как строки, изображения и макеты.
2. **Запуск активностей**: Используется для запуска новых активностей.
3. **Доступ к базам данных и настройкам**: Предоставляет доступ к SharedPreferences и другим хранилищам данных.
4. **Отправка широковещательных сообщений**: Может отправлять и получать широковещательные сообщения.
## Типы Context

1. **ApplicationContext**:
    - **Описание**: Это singleton-экземпляр, привязанный к жизненному циклу приложения. Используется для доступа к глобальным ресурсам и настройкам.
    - **Применение**: Подходит для создания долгоживущих объектов или служб, чтобы избежать утечек памяти[1](https://habr.com/ru/articles/421115/)[5](https://sky.pro/wiki/javascript/application-context-osnovy-ispolzovanie-i-luchshie-praktiki/).
2. **ActivityContext**:
    - **Описание**: Привязан к жизненному циклу конкретной активности.
    - **Применение**: Используется внутри активности для доступа к ресурсам и информации, связанной с этой активностью[2](https://ievetrov.ru/%D1%81%D0%BE%D0%B1%D0%B5%D1%81%D0%B5%D0%B4%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-android/%D0%BA%D0%BE%D0%BD%D1%82%D0%B5%D0%BA%D1%81%D1%82-%D0%B8-%D0%B5%D0%B3%D0%BE-%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5/)[4](https://www.fandroid.info/context-kontekst-v-android-chto-eto-kak-poluchit-i-zachem-ispolzovat/).

## Получение Context

Контекст можно получить с помощью методов:

- `getApplicationContext()`
- `getContext()`
- `getBaseContext()`
- `this` (внутри активности или сервиса)
- `getActivity()` (внутри фрагмента)[4](https://www.fandroid.info/context-kontekst-v-android-chto-eto-kak-poluchit-i-zachem-ispolzovat/).

## Важность Context

Контекст является важнейшим компонентом Android-приложений, поскольку он обеспечивает взаимодействие между различными частями приложения и системой. Правильное использование контекста помогает избежать утечек памяти и улучшает стабильность приложения[1](https://habr.com/ru/articles/421115/)[5](https://sky.pro/wiki/javascript/application-context-osnovy-ispolzovanie-i-luchshie-praktiki/).

**Класс Application** в Android — это базовый класс, который представляет приложение в целом. Он наследует **Context**, предоставляя доступ к глобальной информации об окружении приложения. Этот класс создается при запуске приложения и живет до завершения процесса приложения.

## Основные характеристики класса Application

1. **Единый экземпляр**:
    
    - Класс Application создается один раз на весь жизненный цикл приложения.
        
    - Это Singleton, который предоставляет глобальный контекст для всех компонентов приложения.
        
2. **Инициализация глобального состояния**:
    
    - Application используется для настройки глобальных данных, таких как объекты Singleton, менеджеры ресурсов или библиотеки.
        
    - Метод `onCreate()` вызывается перед запуском первой активности и может быть переопределен для выполнения начальной конфигурации.
        
3. **Контекст**:
    
    - Класс Application является типом **Context** (точнее, **Application Context**), который предоставляет доступ к ресурсам, настройкам и службам системы.
        
    - В отличие от **Activity Context**, Application Context живет на протяжении всего жизненного цикла приложения и не привязан к конкретной активности.
        

## Пример использования

## Создание собственного класса Application

kotlin

`import android.app.Application class MyApplication : Application() {     override fun onCreate() {        super.onCreate()        // Инициализация глобальных объектов или библиотек    } }`

## Регистрация в `AndroidManifest.xml`

xml

`<application     android:name=".MyApplication"    android:icon="@drawable/icon"    android:label="@string/app_name">    <!-- Другие настройки --> </application>`

## Связь с Context

1. **Контекст приложения**:
    
    - Application предоставляет **Application Context**, который используется для долгоживущих операций, таких как доступ к базам данных, настройкам или менеджерам ресурсов.
        
    - Например, можно использовать `getApplicationContext()` для получения контекста приложения из любой активности или службы.
        
2. **Отличие от Activity Context**:
    
    - Activity Context привязан к жизненному циклу конкретной активности и может быть уничтожен вместе с ней.
        
    - Application Context подходит для операций, которые должны продолжаться независимо от жизненного цикла активности.
        

## Преимущества использования класса Application

- **Глобальная доступность**: Позволяет хранить данные или объекты, доступные всем компонентам приложения.
    
- **Инициализация до запуска активностей**: Можно выполнить настройки до отображения первого экрана.
    
- **Устойчивость к утечкам памяти**: Использование Application Context вместо Activity Context помогает избежать утечек памяти.
    

Класс Application полезен для управления состоянием приложения на уровне всего процесса, но его использование должно быть ограничено задачами, требующими глобального контекста.

### Citations:

1. [https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class](https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class)
2. [https://stackoverflow.com/questions/13400455/what-is-the-purpose-of-application-class-in-android](https://stackoverflow.com/questions/13400455/what-is-the-purpose-of-application-class-in-android)
3. [https://stackoverflow.com/questions/3572463/what-is-context-on-android](https://stackoverflow.com/questions/3572463/what-is-context-on-android)
4. [https://stackoverflow.com/questions/7144177/getting-the-application-context](https://stackoverflow.com/questions/7144177/getting-the-application-context)
5. [https://learn.microsoft.com/en-us/dotnet/api/android.app.application?view=net-android-34.0](https://learn.microsoft.com/en-us/dotnet/api/android.app.application?view=net-android-34.0)
6. [https://dev.to/vtsen/android-context-101-with-class-diagram-207n](https://dev.to/vtsen/android-context-101-with-class-diagram-207n)
7. [https://www.linkedin.com/pulse/what-context-android-which-one-should-you-use-ban-markovic](https://www.linkedin.com/pulse/what-context-android-which-one-should-you-use-ban-markovic)
8. [https://www.tutorialspoint.com/difference-between-android-activity-context-and-application-context](https://www.tutorialspoint.com/difference-between-android-activity-context-and-application-context)
9. [https://stackoverflow.com/questions/11408262/purpose-of-object-class-in-android-application](https://stackoverflow.com/questions/11408262/purpose-of-object-class-in-android-application)
10. [https://stackoverflow.com/questions/14197800/when-to-use-and-not-to-use-the-android-application-class](https://stackoverflow.com/questions/14197800/when-to-use-and-not-to-use-the-android-application-class)
11. [https://developer.android.com/codelabs/basic-android-kotlin-compose-classes-and-objects](https://developer.android.com/codelabs/basic-android-kotlin-compose-classes-and-objects)
12. [https://www.linkedin.com/pulse/cracking-context-sakhawat-hossain](https://www.linkedin.com/pulse/cracking-context-sakhawat-hossain)
13. [https://www.tutorialspoint.com/getapplication-vs-getapplicationcontext-in-android](https://www.tutorialspoint.com/getapplication-vs-getapplicationcontext-in-android)
14. [https://developer.android.com/reference/kotlin/android/app/Application?hl=en](https://developer.android.com/reference/kotlin/android/app/Application?hl=en)
15. [https://subscription.packtpub.com/book/mobile/9781788473699/1/ch01lvl1sec15/main-application-class](https://subscription.packtpub.com/book/mobile/9781788473699/1/ch01lvl1sec15/main-application-class)
16. [https://www.tutorialspoint.com/what-is-context-on-android](https://www.tutorialspoint.com/what-is-context-on-android)
17. [https://stackoverflow.com/questions/18002227/why-extend-the-android-application-class](https://stackoverflow.com/questions/18002227/why-extend-the-android-application-class)
18. [https://examples.javacodegeeks.com/android/core/android-application-class-example/](https://examples.javacodegeeks.com/android/core/android-application-class-example/)
19. [https://guides.codepath.com/android/Understanding-the-Android-Application-Class](https://guides.codepath.com/android/Understanding-the-Android-Application-Class)
20. [https://www.codecademy.com/learn/learn-the-basics-of-android/modules/android-app-fundamentals/cheatsheet](https://www.codecademy.com/learn/learn-the-basics-of-android/modules/android-app-fundamentals/cheatsheet)
21. [https://developer.android.com/guide/components/fundamentals?hl=en](https://developer.android.com/guide/components/fundamentals?hl=en)

---

Answer from Perplexity: [pplx.ai/share](https://www.perplexity.ai/search/pplx.ai/share)