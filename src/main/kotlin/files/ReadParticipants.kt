package files

import scheduling.classes.data.Person
import scheduling.classes.time.Date
import scheduling.classes.time.Time
import scheduling.classes.time.WorkDays
import scheduling.objects.People

class ReadParticipants(path: String): ReadFile(path) {
    override fun processData(content: List<String>): Boolean {
        fun parseBoolean(input: String): Boolean{
            return when (input.lowercase()){
                "true" -> true
                "false" -> false
                else -> false
            }
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
                    parseBoolean(content[5])
                )


            )
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