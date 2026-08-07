package com.example.dagger2.di_full_project.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dagger2.R
import com.example.dagger2.di_full_project.app.ExampleApp
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[ExampleViewModel::class.java]
    }

    private val anotherViewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[AnotherExampleViewModel::class.java]
    }


    /* используем свой Builder
    // создаём dataModule через builder,
    // модули с пустыми конструкторами dagger создаёт сам
    // ленивая инициализация: контекст ещё не готов
    val component by lazy {
        DaggerAppComponent.builder()
            .context(application)
            .logTagName("MY_MY_LOG")
            .build()
    }
    */

    // используем свою Factory
    val component by lazy {
        (application as ExampleApp).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // нельзя: у dataModule непустой конструктор
        //DaggerAppComponent.create().inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //инжектим, когда контекст готов
        component.inject(this)

        viewModel.method()
        Log.d("MY_PRES_LOG", "$this, $viewModel")

        anotherViewModel.method()
        Log.d("MY_PRES_LOG", "$this, $anotherViewModel")

        findViewById<TextView>(R.id.tv_text).setOnClickListener {
            Intent(this, AnotherMainActivity::class.java).apply { startActivity(this) }
        }
    }
}