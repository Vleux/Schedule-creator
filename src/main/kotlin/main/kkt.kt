package main


import files.ReadParticipants
import files.ReadTasks
import files.WriteSchedule
import scheduling.classes.data.Person
import scheduling.classes.enums.Fairness
import scheduling.classes.generate.Generator
import scheduling.classes.task.Task
import scheduling.classes.time.Date
import scheduling.classes.time.Time
import scheduling.classes.time.WorkDays
import scheduling.objects.People
import scheduling.objects.Schedule
import scheduling.objects.Tasks

fun main() {
    tryFiles()

    println("printing")
    printSchedule()
}

fun tryFiles(){
    val taskReader = ReadTasks("/home/manvel/Dateien/Downloads/tasks.csv")
    val peopleReader = ReadParticipants("/home/manvel/Dateien/Downloads/participants.csv")
    println("reading tasks ...")
    taskReader.readFile()
    println("Done.")
    println("""
        
        -----------------------
        PRINTING THE READ TASKS
        -----------------------
                
    """.trimIndent())
    val allTasks = Tasks.getAllTasks()
    for (task in allTasks){
        println(
            """
                #################################
                
                Name:       ${task.name}
                ID:         ${task.id}
                People:     ${task.numberOfPeople}
                Datum&Zeit  ${task.dateTime.toList()}
                Excludes    ${task.excludesTasks.toList()}
                Fairness    ${task.requiredFairness}
                
            """.trimIndent()
        )
    }
    println("Reading Participants ...")
    peopleReader.readFile()
    println("Done ...")

    println("""
        
        ######################################
        PRINTING ALL THE RECEIVED PARTICIPANTS
        ######################################
        
    """.trimIndent())

    val allPart = People.getAllPeopleIDs()

    for (id in allPart){
        val pers = People.getPersonById(id)!!
        println("""
            
            -------------------------------
            
            ID          ${pers.id}
            Vorname     ${pers.firstname}
            Nachname    ${pers.lastname}
            Nation      ${pers.nationality}
        """.trimIndent())
    }

    println("""
        
        -------------------------------
        
    """.trimIndent())

    val gen = Generator()
    println("Generating ...")
    gen.start()

    println("Done.")

    val save = WriteSchedule("/home/manvel/Dateien/Downloads/Schedule.csv")
    save.writeFile()

    for (personId in People.getAllPeopleIDs()){
        val person = People.getPersonById(personId)!!
        println("""
            ------------------------------
            
            Name:       ${person.firstname}
            ID:         ${person.id}
            GenTasks:   ${person.myTasks}
        """.trimIndent())
    }

}

fun tryProgram(){
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
            arrayOf(),
            Fairness.MEDIUM
        )
    )
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
    println("Starting ...")
    gen.start()

    println("Generated")

    printSchedule()
}

fun printSchedule(){
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
                Task:   ${Tasks.getTask(task.parentTask)!!.name}
                DATE:   ${Schedule.getDateOfScheduledTask(id)}
                TIME:   ${task.time}
                PERSONS:${persons}
                
                ---------------------------------------------
            """.trimIndent())
    }
}


