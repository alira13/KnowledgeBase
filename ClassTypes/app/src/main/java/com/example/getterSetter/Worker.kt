package getterSetter

class Worker {
    private var salary: Int = 100000

    //сеттер
    fun setSalary(salary: Int) {

        if (salary < 100000)
            println("Salary is too small")
        else
            this.salary = salary
    }

    //геттер
    fun getSalary(): Int {
        return this.salary
    }

    var name: String = ""
        set(value) {
            if (value.isNotBlank())
                field = value
            else {
                println("Name must not be empty")
            }
        }

    var age: Int = 0
        set(value) {
            if (value < field) {
                println("New age must be more then previous $field")
            } else field = value
        }
        get() {
            println("Dont ask age")
            return field
        }
}

fun main() {
    val worker = Worker()

    println("Enter new worker name")
    val name = readln()
    worker.name = name

    println("Enter worker age")
    val age = readln().toInt()
    worker.age = age

    if (worker.name.isNotBlank()) {
        println("Enter new salary")
        val salary = readln().toInt()
        worker.setSalary(salary)
        println("${worker.name} is ${worker.age} years old and his/her salary is ${worker.getSalary()}")
    }
}