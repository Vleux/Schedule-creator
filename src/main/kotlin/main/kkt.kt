package main

import classes.data.ScheduledTask
import classes.data.Task
import classes.time.Date
import classes.time.Dates
import classes.time.Time

fun main() {
    val t = Task(
        "Versuch",
        12,
        mapOf(Date("02-03-2020") to arrayOf(Time("12:00")))
    )

    var s = ScheduledTask(
        Time("12:00"),
        t.id,
        arrayOf()
    )

    val s2 = ScheduledTask(
        Time("12:00"),
        t.id,
        arrayOf()
    )


    var a = arrayListOf<ScheduledTask>(s)
    println(a.toList())
    a.remove(s2)

    println(a.toList())

    var date = Dates(
        Date("01-03-2023"),
        Date("04-05-2023")
    )

    println(date.getFirstDay())

}

fun g(hello: ArrayList<String>){
    hello.remove("hel")
    print(hello)
}

