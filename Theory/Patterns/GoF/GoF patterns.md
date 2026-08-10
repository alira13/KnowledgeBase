# Паттерны GoF

**Паттерн проектирования** — типовое решение часто встречающейся задачи проектирования. Это не библиотека и не код, который можно скопировать, а описание подхода: какая проблема, как её решают и чем за это платят.

Программисты по всему миру сталкивались с похожими проблемами и приходили к похожим решениям. В **1994** году четверо авторов («банда четырёх», **Gang of Four**) собрали 23 таких решения в книгу «Design Patterns».

![](<../../images/Pasted image 20250127165440.png>)

Зачем это знать на собеседовании: половина Android SDK — реализация этих паттернов. Ответ «Builder — это как `AlertDialog.Builder`» ценится выше пересказа определения.

## Три категории
- **Порождающие** — как создавать объекты, не привязываясь к конкретным классам.
- **Структурные** — как собирать объекты в более крупные структуры.
- **Поведенческие** — как распределять обязанности и организовывать взаимодействие.

## Порождающие
| Паттерн | Зачем | В Android/Kotlin SDK |
| --- | --- | --- |
| **Singleton** | один экземпляр на всю программу и глобальная точка доступа | `object` в Kotlin, `Room.databaseBuilder(...)` через синглтон, `WorkManager.getInstance()`. См. [[GoF. Creational. Singleton]] |
| **Builder** | пошагово собрать сложный объект с множеством опциональных параметров | `AlertDialog.Builder`, `NotificationCompat.Builder`, `OkHttpClient.Builder`, `Retrofit.Builder`, `OneTimeWorkRequestBuilder`. См. [[GoF. Creational. Builder]] |
| **Factory Method** | подкласс решает, объект какого класса создать | `Fragment.instantiate()`, `LayoutInflater.createView()`, `ViewModelProvider.Factory.create()` |
| **Abstract Factory** | создавать семейства связанных объектов | фабрики тем/стилей, `SSLSocketFactory` |
| **Prototype** | создать объект копированием существующего | `Intent(otherIntent)`, `Bundle.clone()`, `data class.copy()` |

## Структурные
| Паттерн | Зачем | В Android/Kotlin SDK |
| --- | --- | --- |
| **Adapter** | подружить несовместимые интерфейсы | `RecyclerView.Adapter` (данные → View), `ArrayAdapter` |
| **Decorator** | добавить поведение объекту, не меняя его класс | `ContextWrapper`, обёртки `InputStream`, интерцепторы OkHttp, делегирование через `by`. См. [[GoF. Structural. Decorator]] |
| **Facade** | простой интерфейс к сложной подсистеме | `Retrofit` поверх OkHttp, `WorkManager` поверх JobScheduler/AlarmManager, репозиторий поверх источников данных |
| **Proxy** | подменить объект, контролируя доступ к нему | динамические прокси Retrofit (интерфейс API → реализация), `ContentProvider` как прокси к данным |
| **Composite** | работать с деревом объектов единообразно | иерархия `View`/`ViewGroup`, дерево композиции в Compose |
| **Bridge** | развязать абстракцию и реализацию | `Drawable` и её конкретные виды, разделение UI и рендера |
| **Flyweight** | экономить память, разделяя общее состояние | пул `Message` в `Handler` (`Message.obtain()`), `RecyclerView.RecycledViewPool`, кэш строк |

## Поведенческие
| Паттерн | Зачем | В Android/Kotlin SDK |
| --- | --- | --- |
| **Observer** | уведомлять подписчиков об изменениях | `LiveData`, `StateFlow`/`SharedFlow`, `OnClickListener`, `ContentObserver`. См. [[GoF. Behavioral. Observer]] |
| **Iterator** | последовательный обход коллекции без раскрытия устройства | `Iterator`, `Sequence`, `for` по любой коллекции. См. [[GoF. Behavioral. Iterator]] |
| **Command** | превратить запрос в объект: очередь, отмена, лог | `Runnable`, `Message` в `Handler`, `WorkRequest`, `Intent` как команда системе. См. [[GoF. Behavioral. Command]] |
| **Strategy** | взаимозаменяемые алгоритмы за общим интерфейсом | `LayoutManager` у RecyclerView, `Interpolator` у анимаций, `Comparator` |
| **State** | менять поведение при смене внутреннего состояния | состояния `Lifecycle`, sealed-класс состояния экрана в MVI |
| **Chain of Responsibility** | передавать запрос по цепочке обработчиков | интерцепторы OkHttp, `dispatchTouchEvent` по иерархии View, `WindowInsets` |
| **Mediator** | централизовать общение компонентов | `ViewModel` между фрагментами, `NavController` |
| **Memento** | сохранить и восстановить состояние | `onSaveInstanceState`/`Bundle`, `SavedStateHandle` |
| **Template Method** | скелет алгоритма в базовом классе, шаги — в наследниках | жизненный цикл `Activity`, `AsyncTask` (устар.), базовый `Fragment` в проекте |
| **Visitor** | новая операция над структурой объектов без её изменения | обход AST в компиляторных плагинах, KSP |
| **Interpreter** | вычислять выражения по грамматике | разбор выражений, регулярки |

## Есть отдельные заметки
- [x] Строитель (Builder) → [[GoF. Creational. Builder]]
- [x] Одиночка (Singleton) → [[GoF. Creational. Singleton]]
- [x] Декоратор (Decorator) → [[GoF. Structural. Decorator]]
- [x] Итератор (Iterator) → [[GoF. Behavioral. Iterator]]
- [x] Команда (Command) → [[GoF. Behavioral. Command]]
- [x] Наблюдатель (Observer) → [[GoF. Behavioral. Observer]]
- [ ] Остальные — по таблицам выше; отдельные заметки по мере необходимости.

## Что помнить про паттерны в целом
- Паттерн — **ответ на проблему**, а не цель. Внедрять «чтобы было» — прямой путь к переусложнению.
- Многие GoF-паттерны в Kotlin вырождаются в языковые возможности: Singleton → `object`, Decorator → делегирование `by`, Strategy → функция как параметр, Command → лямбда, Prototype → `copy()`.
- Частый вопрос-ловушка: чем **Decorator** отличается от **Proxy**? Оба оборачивают объект, но декоратор **добавляет поведение**, а прокси **контролирует доступ** (ленивая инициализация, права, кэш, удалённый вызов).
- Второй частый: чем **Strategy** отличается от **State**? Стратегию выбирает клиент снаружи, состояние объект меняет сам изнутри.

Связано: [[Clean architecture]], [[Comparing MVC, MVP, MVVM, MVI]], [[Repository pattern]], [[DI. Dependency injection]]
