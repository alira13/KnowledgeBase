## Объект Bundle

Объект `Bundle` в Android — это класс, реализующий ассоциативный массив, который позволяет хранить и передавать данные в виде пар "ключ-значение". Он используется для передачи данных между различными компонентами приложения, такими как Activity, Fragment и Service.

## Основные особенности Bundle

1. **Хранение данных**: Bundle может хранить различные типы данных, включая примитивные типы (например, `int`, `boolean`), строки и объекты, реализующие интерфейсы `Parcelable` или `Serializable`.
2. **Передача данных**: Bundle часто используется для передачи данных между Activity или Fragment. Например, при создании Fragment можно передать ему аргументы через Bundle с помощью метода `setArguments()`.
3. **Сохранение состояния**: Bundle используется в методах `onSaveInstanceState()` и `onRestoreInstanceState()` для сохранения и восстановления состояния Activity при изменении конфигурации, например, при повороте экрана.

## Пример использования Bundle

java

`// Создание Bundle и добавление данных Bundle args = new Bundle(); args.putInt("num", 10); args.putString("name", "John"); // Передача Bundle в Fragment PageFragment fragment = new PageFragment(); fragment.setArguments(args); // Восстановление данных в Fragment @Override public void onCreate(Bundle savedInstanceState) {     super.onCreate(savedInstanceState);    Bundle args = getArguments();    if (args != null) {        int num = args.getInt("num");        String name = args.getString("name");        // Использование данных    } }`

## Различия с другими механизмами хранения данных

- **SharedPreferences**: Используется для долгосрочного хранения небольших объемов данных, таких как настройки приложения.
- **Intent**: Используется для передачи данных между компонентами приложения, но не является ассоциативным массивом, а скорее контейнером для операций, которые необходимо выполнить.
- **Parcelable и Serializable**: Интерфейсы, которые объекты должны реализовывать, чтобы быть сохраненными в Bundle.

Bundle является гибким и удобным инструментом для передачи и сохранения данных в Android, особенно в контексте жизненного цикла Activity и Fragment.
