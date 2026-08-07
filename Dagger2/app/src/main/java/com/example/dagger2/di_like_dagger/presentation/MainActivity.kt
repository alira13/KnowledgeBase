package com.example.dagger2.di_like_dagger.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dagger2.R
import com.example.dagger2.di_like_dagger.data.Notebook
import com.example.dagger2.di_like_dagger.di.Component

class MainActivity : AppCompatActivity() {

   lateinit var notebook: Notebook
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //внедрение через inject
        Component().inject(this);

        //di via provide
        notebook = Component().provideNotebook()

        //test
        findViewById<TextView>(R.id.tv_text).text = (notebook::class).simpleName
    }
}