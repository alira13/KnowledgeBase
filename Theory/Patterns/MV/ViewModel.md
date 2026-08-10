# ViewModel (в паттернах MV*)

![](<../../images/Pasted image 20250328111023.png>)

**ViewModel** — «VM» в **MVVM**/**MVI**: слой между View и данными, который держит **состояние UI** и логику его подготовки, но **не знает про View**. View подписывается на состояние (StateFlow/LiveData) и отражает его; события идут от View к ViewModel.

## Роль в MVVM
- Готовит данные для отображения (UI-модели), обрабатывает действия пользователя, обращается к домену/репозиториям.
- Связь **View ↔ ViewModel** — через наблюдаемое состояние (data binding / `collectAsState`), а не прямые вызовы View. Это даёт слабую связанность и тестируемость.
- Отличие от **Presenter** (MVP): Presenter держит ссылку на View (интерфейс) и вызывает его методы; ViewModel — нет, View сам наблюдает за состоянием. См. [[Comparing MVC, MVP, MVVM, MVI]].

## Jetpack ViewModel (реализация)
Конкретный класс `androidx.lifecycle.ViewModel` реализует эту роль и вдобавок **переживает изменение конфигурации**. Подробности жизненного цикла, `ViewModelProvider`, `SavedStateHandle`, `viewModelScope` — в [[1 ViewModel, ViewModelProvider]].

## Состояние
- **StateFlow** (современно, KMP) или **LiveData** (lifecycle-aware). См. [[StateFlow]], [[LiveData vs StateFlow]].
- В MVI — единый immutable `State` + `Action`/`Intent` + reducer.

Связано: [[1 ViewModel, ViewModelProvider]], [[Comparing MVC, MVP, MVVM, MVI]], [[LiveData]], [[StateFlow]]
