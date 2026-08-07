package com.example.dagger2.di_full_project.app

import android.app.Application
import com.example.dagger2.di_full_project.di.DaggerAppComponent

class ExampleApp: Application() {
    // используем свою Factory
    val component by lazy {
        DaggerAppComponent.factory()
            .create(this, "MY_MY_LOG")
    }
}