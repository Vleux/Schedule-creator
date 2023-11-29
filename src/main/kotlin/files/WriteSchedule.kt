package files

import scheduling.objects.People
import scheduling.objects.Schedule
import scheduling.objects.Tasks

class WriteSchedule(path: String): WriteFile(path) {

    /**
     * prepares the data (fills it in the dataTable and prepares the days)
     * This is necessary to parse the Data into strings and use them in a good way
     */
    override fun prepareData() {
        val allDays = Schedule.getAllDays()

        for (entry in allDays){
            this.days.add(entry.key.toString())
            this.days.sort()

            for (task in entry.value){
                val scheduledTask = Schedule.getScheduledTask(task)!!
                val time = scheduledTask.time.first

                if (dataTable[time] != null){
                    dataTable[time]!!.add(task)
                }else{
                    dataTable[time] = mutableListOf(task)
                }
            }
        }
    }

    /**
     * Parses the data into the lines and immediately writes these lines.
     */
    override fun parseData() {
        val times = dataTable.keys.toMutableList().sorted()
        this.days.sort()

        this.writeLine(this.generateFirstLine(),";")

        for (time in times){

            // Initializing the Lines (time, name and people)
            val firstLine = Array(this.days.size + 1){""}
            firstLine[0] = time.toString()

            val secondLine = Array(this.days.size + 1){""}

            for (task in this.dataTable[time]!!){
                val i = this.days.indexOf(
                    Schedule.getDateOfScheduledTask(task).toString()
                )
                val scheduledTask = Schedule.getScheduledTask(task)!!
                firstLine[i] = Tasks.getTask(
                    scheduledTask.parentTask
                )!!.name
                var people = ""
                for (id in scheduledTask.takenPeople){
                    val person = People.getPersonById(id)!!
                    people += "${person.firstname} ${person.lastname},"
                }
                secondLine[i] = people
            }

            this.writeLine(firstLine, ";")
            this.writeLine(secondLine, ";")
        }
    }

    private fun generateFirstLine(): Array<String>{
        val result = Array(this.days.size + 1){""}
        for (i in 1 until result.size){
            result[i] = this.days[i - 1]
        }
        return result
    }


}