```kotlin
package com.example.shoppinglistbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ConcurrentHashMap

@SpringBootApplication
class ShoppingListBackendApplication

fun main(args: Array<String>) {
    runApplication<ShoppingListBackendApplication>(*args)
}

@RestController
@RequestMapping("/api/shopping-list")
class ShoppingListController {
    private val shoppingLists = ConcurrentHashMap<String, MutableList<String>>()

    @PostMapping("/create")
    fun createList(@RequestParam userId: String, @RequestParam listName: String): String {
        shoppingLists[userId] = shoppingLists.getOrDefault(userId, mutableListOf())
        return "Список '$listName' создан."
    }

    @PostMapping("/add")
    fun addItem(@RequestParam userId: String, @RequestParam item: String): String {
        val list = shoppingLists.getOrPut(userId) { mutableListOf() }
        list.add(item)
        return "Товар '$item' добавлен в список."
    }

    @DeleteMapping("/remove")
    fun removeItem(@RequestParam userId: String, @RequestParam item: String): String {
        val list = shoppingLists[userId] ?: return "Список не найден."
        if (list.remove(item)) {
            return "Товар '$item' удален."
        }
        return "Товар '$item' не найден в списке."
    }

    @GetMapping("/list")
    fun getList(@RequestParam userId: String): List<String> {
        return shoppingLists[userId] ?: emptyList()
    }
}

@RestController
@RequestMapping("/api/alice")
class AliceController(val shoppingListController: ShoppingListController) {
    @PostMapping("/voice")
    fun handleAliceRequest(@RequestBody request: AliceRequest): AliceResponse {
        val userId = request.session.userId
        val command = request.request.command.lowercase()
        val responseText = when {
            command.contains("создай список") -> shoppingListController.createList(userId, "Мой список")
            command.contains("добавь") -> {
                val item = command.removePrefix("добавь ").trim()
                shoppingListController.addItem(userId, item)
            }
            command.contains("удали") -> {
                val item = command.removePrefix("удали ").trim()
                shoppingListController.removeItem(userId, item)
            }
            command.contains("покажи список") -> {
                val list = shoppingListController.getList(userId).joinToString(", ")
                if (list.isEmpty()) "Ваш список пуст." else "Ваш список: $list"
            }
            else -> "Я не понимаю команду. Попробуйте снова."
        }
        return AliceResponse(AliceResponseBody(responseText))
    }
}

data class AliceRequest(val session: AliceSession, val request: AliceRequestData)
data class AliceSession(val userId: String)
data class AliceRequestData(val command: String)
data class AliceResponse(val response: AliceResponseBody)
data class AliceResponseBody(val text: String, val end_session: Boolean = false)

```