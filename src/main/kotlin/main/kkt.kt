package main


import classes.data.Person
import classes.generate.Generator
import classes.task.Task
import classes.time.Date
import classes.time.Time
import classes.time.WorkDays
import objects.People
import objects.Schedule
import objects.Tasks

fun main() {
    People.addPerson(
        Person(
            "Fritz",
            "Frech",
            Date("01-01-2000"),
            "germany",
            true,
            WorkDays(
                Date("01-01-2024"),
                Time("09:00"),
                Date("09-01-2024"),
                Time("15:00")
            )
        )
    )
    People.addPerson(
        Person(
            "David",
            "Zdravev",
            Date("01-01-2000"),
            "macedonia",
            false,
            WorkDays(
                Date("02-01-2024"),
                Time("18:00"),
                Date("09-01-2024"),
                Time("01:00")
            )
        )
    )

    Tasks.addTask(
        Task(
            "Dinner",
            1,
            mapOf(
                Pair(
                    Date("02-01-2024"),
                    arrayOf(Pair(Time("17:00"), Time("19:00")))
                ),
                Pair(
                    Date("03-01-2024"),
                    arrayOf(Pair(Time("17:00"), Time("19:00")))
                ),
                Pair(
                    Date("06-01-2024"),
                    arrayOf(Pair(Time("16:00"), Time("18:00")))
            ),

        ),
            arrayOf(),
            arrayOf()
    ))
    Tasks.addTask(
        Task(
            "OtherTask",
            2,
            mapOf(
                Pair(
                    Date("03-01-2024"),
                    arrayOf(Pair(Time("12:00"), Time("18:00")))
                    ),
                Pair(
                    Date("04-01-2024"),
                    arrayOf(Pair(Time("13:00"), Time("20:00")))
                )
            ),
            arrayOf(),
            arrayOf()
        )
    )
    Tasks.addTask(
        Task(
            "not Dinner",
            1,
            mapOf(
                Pair(
                    Date("06-01-2024"),
                    arrayOf(Pair(Time("17:00"), Time("19:00")))
                )
            ),
            arrayOf(),
            arrayOf()
        )
    )
    Tasks.addTask(
        Task(
            "inc1",
            1,
            mapOf(
                Pair(
                    Date("07-01-2024"),
                    arrayOf(Pair(Time("12:00"), Time("13:00")))
                )
            ),
            arrayOf(),
            arrayOf()
        )
    )

    Tasks.addTask(
        Task(
            "inc2",
            1,
            mapOf(
                Pair(
                    Date("08-01-2024"),
                    arrayOf(Pair(Time("05:00"), Time("07:00")))
                )
            )
        )
    )

    println("Created")

    val gen = Generator()
    gen.start()

    println("Generated")

    val schedTasks = Schedule.getAllTasks()

    for (id in schedTasks){
        val task = Schedule.getScheduledTask(id)!!
        var persons = ""
        for (personID in task.takenPeople){
            persons += "${People.getPersonById(personID)!!.firstname} : "
        }
        print(
            """
                --------------------------------------------
                
                ID:     ${id}
                DATE:   ${Schedule.getDateOfScheduledTask(id)}
                TIME:   ${task.time}
                PERSONS:${persons}
                
                ---------------------------------------------
            """.trimIndent())
    }
}


