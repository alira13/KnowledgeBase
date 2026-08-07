package com.example.dagger2.di_dagger_inject

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dagger2.R
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var notebook: Notebook

    private lateinit var notebookViaGet: Notebook
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
        //DaggerComponent.create().inject(this);

        //внедрение через get-функцию
        notebookViaGet = DaggerComponent.create().getNotebook()

        //test
        findViewById<TextView>(R.id.tv_text).text = (notebookViaGet::class).simpleName
    }
}