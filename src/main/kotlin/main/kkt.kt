package main


import files.ReadAssignedPeople
import files.ReadParticipants
import files.ReadTasks
import files.WriteSchedule
import scheduling.classes.generate.Generator
import scheduling.objects.People
import scheduling.objects.Schedule
import scheduling.objects.Tasks

/**
 * NOTIZEN
 * - nachdem die Tasks alle mit Personen verbunden wurden nocheinmal sicherstellen
 *  dass es fair ist (ein kontroll durchlauf auf den aufgaben)
 *      -> Wenn etwas nicht gerecht ist, änderungen durchführen!
 */
fun main() {
    tryFiles()
}

fun tryFiles(){
    val taskReader = ReadTasks("/home/manvel/Dateien/Dokumente/02 Mazedonien/02 MK/Weihnachtsmarkt/Listen/Tasks.csv")
    val peopleReader = ReadParticipants("/home/manvel/Dateien/Dokumente/02 Mazedonien/02 MK/Weihnachtsmarkt/Listen/participants.csv")
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

    println("reading already defined people")
    val read = ReadAssignedPeople("/home/manvel/Dateien/Dokumente/02 Mazedonien/02 MK/Weihnachtsmarkt/Listen/scheduledPeople.csv")
    read.readFile()

    println("##################")
    println("printing the Schedule for the first time")
    println("##################")

    printSchedule()

    val gen = Generator()
    println("Generating ...")
    gen.start()

    println("Done.")

    val save = WriteSchedule("/home/manvel/Dateien/Dokumente/02 Mazedonien/02 MK/Weihnachtsmarkt/Listen/Schedule.csv")
    save.writeFile()

    println("-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#")
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
        println(
            """
                --------------------------------------------
                
                ID:     $id
                Task:   ${Tasks.getTask(task.parentTask)!!.name}
                DATE:   ${Schedule.getDateOfScheduledTask(id)}
                TIME:   ${task.time}
                PERSONS:${persons}
                
                ---------------------------------------------
            """.trimIndent())
    }
}


