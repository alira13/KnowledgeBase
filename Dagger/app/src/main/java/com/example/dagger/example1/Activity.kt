package com.example.dagger.example1

import com.example.dependencyinjectionstart.example1.Keyboard
import com.example.dependencyinjectionstart.example1.Memory
import com.example.dependencyinjectionstart.example1.Monitor
import com.example.dependencyinjectionstart.example1.Mouse

class Activity {
    // внедрение зависимостей через поля
    val monitor = Monitor()
    val keyboard = Keyboard()
    val mouse = Mouse()
    val computerTower = ComputerTower(
        Storage(),
        Memory(),
        Processor()
    )
    val computer = Computer(monitor, computerTower, keyboard, mouse)
}