package com.example.dagger2.di_without_dagger.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dagger2.R
import com.example.dagger2.di_without_dagger.data.Computer
import com.example.dagger2.di_without_dagger.data.ComputerTower
import com.example.dagger2.di_without_dagger.data.Keyboard
import com.example.dagger2.di_without_dagger.data.Memory
import com.example.dagger2.di_without_dagger.data.Monitor
import com.example.dagger2.di_without_dagger.data.Mouse
import com.example.dagger2.di_without_dagger.data.Processor
import com.example.dagger2.di_without_dagger.data.Storage
import com.example.dagger2.di_without_dagger.di.Component

class MainActivity : AppCompatActivity() {

    // not di (Не доставляются извне)
    var monitor = Monitor()
    var keyboard = Keyboard()
    var mouse = Mouse()

    var computerTower = ComputerTower(
        Storage(),
        Memory(),
        Processor()
    )

    //var computer = Computer(monitor, computerTower, keyboard, mouse)
    //

    //di via provide
    private var diComputerViaProvide = Component().provideComputer()
    lateinit var diComputerViaInject: Computer

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

        //test
        findViewById<TextView>(R.id.tv_text).text = (diComputerViaInject::class).simpleName
    }
}