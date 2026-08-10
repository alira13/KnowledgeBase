# Ресурсы (Resources)

Ресурсы — статические данные приложения, вынесенные из кода в папку **`res/`**: строки, изображения, цвета, размеры, темы, layout, анимации. Позволяют менять контент без правки кода и адаптироваться под устройства.

## Типы ресурсов (`res/`)
| Папка | Что |
|---|---|
| `drawable/` | изображения, векторы, shape/selector |
| `mipmap/` | иконки лаунчера (разные плотности) |
| `layout/` | XML-разметка экранов |
| `values/` | `strings.xml`, `colors.xml`, `dimens.xml`, `styles.xml`, `themes.xml` |
| `font/` | шрифты |
| `raw/` | произвольные файлы (аудио и т.п.) |
| `xml/` | конфиги (network security config, prefs) |
| `menu/`, `anim/`, `animator/` | меню, анимации |

## Класс R
При сборке генерируется класс **`R`** со `static final int`-ссылками на каждый ресурс (`R.string.app_name`, `R.drawable.ic_logo`). Даёт **проверку на этапе компиляции**: опечатка в имени ресурса → ошибка компиляции, а не падение в рантайме. Доступ: `getString(R.string.x)`, `ContextCompat.getColor(...)`, в Compose — `stringResource(R.string.x)`, `painterResource(...)`.

## Квалификаторы (адаптация под конфигурацию)
Ресурсы можно дублировать в папках с суффиксами — система выбирает нужный по конфигурации устройства:
- **Плотность**: `drawable-mdpi/hdpi/xhdpi/xxhdpi/xxxhdpi` (mdpi=1x базовая).
- **Язык**: `values-ru/`, `values-en/` (локализация).
- **Ориентация/размер**: `layout-land/`, `layout-sw600dp/` (планшеты).
- **Ночная тема**: `values-night/`.
- **Версия API**: `values-v31/`.

Единицы: **dp** (density-independent pixels) для размеров, **sp** для шрифтов (учитывает настройку размера шрифта пользователя).

Связано: [[Android app architecture]], [[Compat classes]], [[App startup process]]
