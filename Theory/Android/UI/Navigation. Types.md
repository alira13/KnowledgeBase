
| Способ навигации               | Описание                                                                        | Когда использовать                                        |
| ------------------------------ | ------------------------------------------------------------------------------- | --------------------------------------------------------- |
| **Activity**                   | Каждое окно — отдельная `Activity`, переход через `Intent`.                     | Маленькие приложения, отдельные независимые экраны.       |
| **Fragment**                   | Управление экранами внутри одной `Activity`, навигация через `FragmentManager`. | Средние и крупные приложения, экономия ресурсов.          |
| **Navigation Component**       | Jetpack-компонент, использует `NavController` и `nav_graph.xml`.                | Современные приложения, поддержка Deep Links и Safe Args. |
| **Bottom Navigation**          | Нижнее меню (`BottomNavigationView`) для быстрого переключения между разделами. | Приложения с 3-5 основными разделами (например, соцсети). |
| **Drawer Navigation**          | Боковое меню (`DrawerLayout` + `NavigationView`).                               | Приложения с большим количеством разделов.                |
| **Tabs (Вкладки)**             | `TabLayout` + `ViewPager2` для переключения внутри одной `Activity`.            | Новости, магазины, профили, разделенные на подкатегории.  |
| **Deep Linking**               | Открытие определенных экранов через внешние ссылки.                             | Интеграция с веб-сайтами, маркетинг, пуш-уведомления.     |
| **Back Stack и Up Navigation** | Кнопка "Назад" (`onBackPressed`), "Вверх" (стрелка в `ActionBar`).              | Навигация внутри приложения, удобство для пользователей.  |

## Три направления навигации
Прежде чем выбирать инструмент, полезно понять, куда именно ведёт переход:
- **Наружу** — из приложения в другое приложение: открыть ссылку в браузере, поделиться, вызвать камеру. Механизм — неявный `Intent`. См. [[0 App components. Intent]].
- **Снаружи внутрь** — переход из другого приложения или уведомления на конкретный экран. Механизм — deep links и `PendingIntent`. См. [[DeepLinks]].
- **Внутри приложения** — переходы между своими экранами. Здесь и работают все способы ниже.

В Android-приложениях существует несколько способов навигации, которые помогают пользователям перемещаться между экранами и фрагментами. Основные из них:

### 1. **Навигация с помощью Intent в Activity**

- Каждая новая экранная форма представлена отдельной `Activity`.
- Переход осуществляется через `Intent`, например:
    
    ```java
    Intent intent = new Intent(this, SecondActivity.class);
    startActivity(intent);
    ```
    
- Используется в простых приложениях, но в крупных проектах предпочтительнее `Fragments`.

### 2. **Навигация с помощью Fragment**

- `Fragment` – это часть пользовательского интерфейса внутри `Activity`.
- Управление фрагментами через `FragmentManager`:
    
    ```kotlin
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_container, ExampleFragment());
    transaction.addToBackStack(null);
    transaction.commit();
    ```
    
- Экономит ресурсы, так как не создает новую `Activity`.
- Если хотим передать параметр при навигации, используем ExampleFragment.newInstance(paramName) и создаем фрагмент не через конструктор а через newInstance

### 3. **Навигация с использованием Jetpack Navigation Component**

- Компонент из `Android Jetpack`, который упрощает навигацию.
- Позволяет описывать маршруты в `nav_graph.xml`.
- Использует `NavController`:
    
    ```kotlin
    findNavController().navigate(R.id.action_firstFragment_to_secondFragment)
    ```
    
- Поддерживает `Deep Links`, `Safe Args`, анимации.

### 4. **Bottom Navigation (Нижнее меню)**

- Используется для переключения между основными разделами приложения.
- Реализуется через `BottomNavigationView`:
    
    ```xml
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        app:menu="@menu/bottom_nav_menu" />
    ```
    
- Обрабатывается через `OnNavigationItemSelectedListener`.

### 5. **Drawer Navigation (Боковое меню)**

- Выдвижное меню для навигации по основным разделам.
- Используется `DrawerLayout` и `NavigationView`:
    
    ```xml
    <androidx.drawerlayout.widget.DrawerLayout>
        <com.google.android.material.navigation.NavigationView />
    </androidx.drawerlayout.widget.DrawerLayout>
    ```
    
- Обрабатывается через `setNavigationItemSelectedListener`.

### 6. **Tabs (Вкладки) ViewPager2** 

- Для переключения между разделами внутри одного экрана.
- Реализуется через `TabLayout` и `ViewPager`/`ViewPager2`:
    
    ```xml
    <com.google.android.material.tabs.TabLayout />
    <androidx.viewpager2.widget.ViewPager2 />
    ```

### 7. **Deep Linking (Глубокие ссылки)**

- Позволяет открывать определенные экраны из внешних источников (браузер, другой апп).
- Реализуется через `<intent-filter>` в `AndroidManifest.xml`:
    
    ```xml
    <intent-filter>
        <data android:scheme="https" android:host="example.com" android:pathPrefix="/profile" />
    </intent-filter>
    ```
    

### 8. **Back Stack и Up Navigation**

- Навигация "назад" (`onBackPressed`) и "вверх" (стрелка в `ActionBar`).
- `onBackPressedDispatcher` используется для обработки "назад" в `Fragment`.

### Итог:

Выбор подходящего способа навигации зависит от структуры приложения. `Navigation Component` – наиболее гибкий и рекомендуемый подход, но в некоторых случаях могут подойти `BottomNavigationView`, `DrawerLayout` или `Tabs`.