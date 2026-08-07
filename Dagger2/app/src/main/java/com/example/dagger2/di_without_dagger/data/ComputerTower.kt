package com.example.dagger2.di_without_dagger.data

// внедрение зависимости в конструкторе
class ComputerTower(
    val storage: Storage,
    val memory: Memory,
    val processor: Processor
)
