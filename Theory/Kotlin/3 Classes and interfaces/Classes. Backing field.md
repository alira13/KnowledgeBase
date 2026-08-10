У нас есть коллекция. 
Внутри одного класса, например ViewModel мы хотим добавлять и удалять элементы.
Но в другом классе Activity мы хотим только считывать значения и запретить модифицировать коллекцию. Для такого случая существует подход с внутренними полями backingField. Как правило используются в случае изменяемых коллекций.

- Внешний код должен иметь доступ только для чтения к коллекциям `books` и `users`.
- Добавление и удаление книг и пользователей должны выполняться только через методы `addBook`, `removeBookByTitle`, `addUser`, и `removeUserById`.
- После внесения исправлений любые попытки изменить коллекции напрямую должны быть невозможны.

```kotlin
// Book.kt  
data class Book(val title: String, val author: String)  
  
// User.kt  
data class User(val name: String, val id: Int)  
  
// Library.kt  
class Library(val libraryName: String) {  
    // Backing field  
    private val _books = mutableListOf<Book>()  
    val books: List<Book>  
        get() {  
            return _books.toList()  
        }  
  
    // Backing field  
    private val _users = mutableListOf<User>()  
    val users: List<User>  
        get() {  
            return _users.toList()  
        }  
  
    fun addBook(book: Book) {  
        _books.add(book)  
        println("Книга \"${book.title}\" добавлена в библиотеку.")  
    }  
  
    fun addUser(user: User) {  
        _users.add(user)  
        println("Пользователь ${user.name} добавлен в библиотеку.")  
    }  
  
    // Удаление книги по названию  
    fun removeBookByTitle(title: String): Boolean {  
        val removed = _books.removeIf { it.title == title }  
        if (removed) {  
            println("Книга \"$title\" удалена из библиотеки.")  
        } else {  
            println("Книга \"$title\" не найдена в библиотеке.")  
        }  
        return removed  
    }  
  
    // Удаление пользователя по ID  
    fun removeUserById(id: Int): Boolean {  
        val removed = _users.removeIf { it.id == id }  
        if (removed) {  
            println("Пользователь с ID $id удален из библиотеки.")  
        } else {  
            println("Пользователь с ID $id не найден в библиотеке.")  
        }  
        return removed  
    }  
  
    // Вывод списка всех книг  
    fun printAllBooks() {  
        println("Список книг в библиотеке $libraryName:")  
        books.forEach { println("- ${it.title} by ${it.author}") }  
    }  
  
    // Вывод списка всех пользователей  
    fun printAllUsers() {  
        println("Список пользователей библиотеки $libraryName:")  
        _users.forEach { println("- ${it.name}, ID: ${it.id}") }  
    }  
}
```

В классах все поля должны быть val, иначе когда мы создаем коллекции классов, ссылки на эти коллекции может и будут разные, но ссылки на элементы будут те же. И мы можем случайно изменить эти элементы, думая, что это разные коллекции.