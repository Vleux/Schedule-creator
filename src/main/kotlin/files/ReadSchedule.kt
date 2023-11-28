package files

import scheduling.classes.enums.Fairness
import scheduling.classes.task.Task
import scheduling.classes.time.Date
import scheduling.classes.time.Time
import scheduling.objects.Tasks

class ReadTasks(path: String): ReadFile(path) {

    // Needed to add more than one date to a certain task
    private var previousTask: String = ""
    // Needed for the cleanup (adding the excluded Tasks to a task)
    private val mapNamesToId = mutableMapOf<String, String>()
    private val taskExcludes = mutableMapOf<String, ArrayList<String>>()

    /**
     * Gathers the information about Tasks and invokes them into a new Task
     */
    override fun processData(content: List<String>): Boolean{
        /**
         * parses the Pair<Time, Time> array for the dateTime map
         */
        fun getDateTime():Array<Pair<Time, Time>> {
            val time = arrayListOf<Pair<Time, Time>>()
            for (i in 5 until content.size) {
                val time1 = content[i].split("-")[0]
                val time2 = content[i].split("-")[1]
                time.add(Pair(Time(time1), Time(time2)))
            }
            return time.toTypedArray<Pair<Time, Time>>()
        }

        if (content.size < 4){
            return false
        }else if (content[0] != ""){

            // calculate new Task
            val newTask =  Task(
                content[0],
                content[1].toInt(),
                mapOf(Pair(Date(content[3]), getDateTime())),
                emptyArray(),
                emptyArray(),
                when (content[3].lowercase()) {
                    "maximum" -> {
                        Fairness.MAXIMUM
                    }

                    "medium" -> Fairness.MEDIUM
                    "low" -> Fairness.LOW
                    else -> Fairness.LOW
                },
                when (content[4].lowercase()){
                    "true" -> true
                    "false" -> false
                    else -> false
                }
            )

            // save incompatible Task names to restore them lateron
            this.previousTask = newTask.id
            mapNamesToId[newTask.name] = newTask.id

            if (taskExcludes[newTask.id] == null){
                taskExcludes[newTask.id] = content[2].split("&").toCollection(ArrayList())
            }else{
                taskExcludes[newTask.id]!!.addAll(content[2].split("&").toCollection(ArrayList()))
            }

            // save the task in the Tasks object
            scheduling.objects.Tasks.addTask(newTask)

        }else{
            // In this case, the given date-time data belongs to the previous task, due to the name not existing Name!
            val newDateTime = Tasks.getTask(this.previousTask)!!.dateTime.toMutableMap()
            newDateTime[Date(content[5])] = getDateTime()
            Tasks.getTask(this.previousTask)!!.dateTime = newDateTime.toMap()
        }
        return true
    }

    /**
     * Saves the excluded Tasks into the corresponding Tasks
     */
    override fun cleanUp(){
        // Save the excluded Tasks as excluded Tasks in the specified Task
        for (entry in taskExcludes){
            val task = Tasks.getTask(
                mapNamesToId[entry.key]!!
            )!!
            for (taskName in entry.value){
                task.addExcludedTask(
                    mapNamesToId[taskName]!!
                )
            }
        }
    }
}