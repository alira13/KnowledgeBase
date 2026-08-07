package com.example.dagger.example1

import com.example.dependencyinjectionstart.example1.Memory

//внедрение зависимости через конструктор
class ComputerTower(
    val storage: Storage,
    val memory: Memory,
    val processor: Processor
)
