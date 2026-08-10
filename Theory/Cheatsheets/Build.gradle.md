### ViewBinding

```Kotlin
android {
	buildFeatures {
        viewBinding = true
  }
}
```

### Jetpack Navigation Component

```Kotlin
dependencies {
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.fragment:fragment-ktx:1.5.6")
}
```

### BottomNavigationView

```Kotlin
dependencies {
	implementation "com.google.android.material:material:1.8.0"
}
```

### RxJava

- для управления потоками данных и организации многопоточного или асинхронного кода.

```Kotlin
dependencies {
 // Зависимость на RxJava
	implementation "io.reactivex.rxjava2:rxjava:2.2.21"

// Зависимость на RxAndroid
	implementation "io.reactivex.rxjava2:rxandroid:2.1.1"
}
```

### Корутины

```Kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
} 
```

### Room

```Kotlin
 plugins {
    id 'kotlin-kapt'
}
 
 def room_version = "2.5.1" // текущая стабильная версия 
 implementation("androidx.room:room-runtime:$room_version") // библиотека Room
 kapt("androidx.room:room-compiler:$room_version") // Kotlin-кодогенератор
 implementation("androidx.room:room-ktx:$room_version") // поддержка корутин
```

### Json
