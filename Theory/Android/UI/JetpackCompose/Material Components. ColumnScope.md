чтобы использовать функции с weight, их нужно сделать функциями расширения определенного scope - можем вызывать функцию только внутри этого Scope
```kotlin
@Composable
fun ColumnScope.UseWeight(){

}
```
MaterialCompose - приложение с примерами использования Compose-элементов

Scaffold  - экран

remember переживает рекомпозицию но не поворот экрана
rememberSaveble - чтобы пережила рекомпозиция экрана

StateFullComposable - если сама меняет свой state
StateLess - если получает состояние извне и меняется

