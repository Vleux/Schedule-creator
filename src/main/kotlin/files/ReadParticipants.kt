package files

import scheduling.classes.data.Person
import scheduling.classes.time.Date
import scheduling.classes.time.Time
import scheduling.classes.time.WorkDays
import scheduling.objects.People
import scheduling.objects.Tasks

class ReadParticipants(path: String): ReadFile(path) {
    override fun processData(content: List<String>): Boolean {
        fun parseBoolean(input: String): Boolean{
            return when (input.lowercase()){
                "true" -> true
                "false" -> false
                else -> false
            }
        }
        fun parseIncompatibleTasks(): MutableList<String>{
            if (content.size <= 10) return mutableListOf()
            val result = mutableListOf<String>()
            val allTasks = Tasks.getAllTasks()
            outer@for (taskName in content.subList(10, content.lastIndex)){
                for (task in allTasks){
                    if (task.name == taskName){
                        result.add(task.id)
                        continue@outer
                    }
                }
                println("Task $taskName does not exist!")
            }
            return result
        }
        try {
            People.addPerson(
                Person(
                    content[0],
                    content[1],
                    Date(content[2]),
                    content[3],
                    parseBoolean(content[4]),
                    WorkDays(
                        Date(content[5]),
                        Time(content[6]),
                        Date(content[7]),
                        Time(content[8])
                    ),
                    parseIncompatibleTasks(),
                    parseBoolean(content[9])
                )


            )
            println("""
                Name:   ${content[0]}
                Bool:   -${content[9]}-
                parsed: ${parseBoolean(content[9])}
            """.trimIndent())
            return true
        }catch(e: Exception){
            println(e)
            return false
        }
    }

    override fun cleanUp() {
        return
    }

}