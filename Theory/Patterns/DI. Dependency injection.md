Классы зависят друг от друга.
Внедрение зависимостей-это паттерн проектирования, при котором все зависимости поставляются извне, а не создаются внутри классов

 - Внедрение в конструктор класса. 
`class Computer(val keyBoard:Keyboard, val mause:Mause)`

 - Внедрение через поля
а) класс сам запрашивает у кого-то зависимость`
`class Activity(){`
	`val computer = Component().getComputer() 
`}`

б) класс просит кого-то внедрить ему зависимость извне(лучше)

`class Activity(){`
	`lateinit var computer:Computer`
	`init{`
		`Component().inject(this)`
	`}`
`}`

`class Component(){`
	`fun inject(activity:Activity){`
		`activity.computer = Computer()`
	`}`
`}`


 
 


