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
                val time = Schedule.getScheduledTask(task)!!.time
                val timeString = "${time.first} - ${time.second}"

                if (dataTable[timeString] != null){
                    dataTable[timeString]!!.add(task)
                }else{
                    dataTable[timeString] = mutableListOf(task)
                }
            }
        }

        println(dataTable)
    }

    /**
     * Parses the data into the lines and immediately writes these lines.
     */
    override fun parseData() {
        val times = dataTable.keys.toMutableList().sorted()
        this.days.sort()

        this.writeLine(this.generateFirstLine(),";")

        for (time in times){

            val lines = mutableListOf<Array<String>>()

            tasks@ for (i in this.dataTable[time]!!.indices){
                val task = Schedule.getScheduledTask(
                    this.dataTable[time]!![i]
                )!!
                val taskName = Tasks.getTask(task.parentTask)!!.name
                var participants = ""
                for (personId in task.takenPeople){
                    val person = People.getPersonById(personId)!!
                    val lastname = if (person.lastname.length <= 3){
                        person.lastname
                    }else {person.lastname.subSequence(0, 3)}
                    participants += "${person.firstname} ${lastname}., "
                }
                val targIndex = this.days.indexOf(Schedule.getDateOfScheduledTask(task.id).toString())
                if (i == 0){
                    val array = Array(this.days.size) { "" }
                    array[0] = time

                    array[targIndex] = taskName
                    array[targIndex + 1] = participants
                    lines.add(array)
                }else{
                    for (k in lines.indices){
                        if (lines[k][targIndex] == ""){
                            println(lines[k][targIndex])
                            lines[k][targIndex] = taskName
                            lines[k][targIndex + 1] = participants
                            continue@tasks
                        }
                    }
                    val newArray = Array(this.days.size){ ""}
                    newArray[targIndex] = taskName
                    newArray[targIndex + 1] = participants
                    lines.add(newArray)
                }

            }

            lines.forEach{ this.writeLine(it) }

            /*
            // Initializing the Lines (time, name and people)
            val firstLine = Array(this.days.size + 1){""}
            firstLine[0] = time

            val secondLine = Array(this.days.size + 1){""}

            for (task in this.dataTable[time]!!){
                val i = this.days.indexOf(
                    Schedule.getDateOfScheduledTask(task).toString()
                ) + 1

                val scheduledTask = Schedule.getScheduledTask(task)!!
                firstLine[i] = Tasks.getTask(
                    scheduledTask.parentTask
                )!!.name
                //firstLine[0] = "${firstLine[0]} - ${scheduledTask.time.second}"

                var people = ""
                for (id in scheduledTask.takenPeople){
                    val person = People.getPersonById(id)!!
                    people += "${person.firstname} ${person.lastname},"
                }
                secondLine[i] = people
            }

            this.writeLine(firstLine, ";")
            this.writeLine(secondLine, ";")*/
        }
    }

    private fun generateFirstLine(): Array<String>{
        val result = mutableListOf<String>("")
        for (day in this.days){
            result.add(day)
            result.add("")
        }
        this.days = result.copy()
        return this.days.toTypedArray()
    }

    fun MutableList<String>.copy(): MutableList<String>{
        val newMutableList = mutableListOf<String>()
        for (item in this){
            newMutableList.add(item)
        }
        return newMutableList
    }


}