package files

import scheduling.classes.time.Date
import scheduling.classes.time.Time
import scheduling.objects.People
import scheduling.objects.Tasks

class ReadAssignedPeople(path: String): ReadFile(path) {
    private val tasks = mutableMapOf<String, MutableMap<String, MutableMap<String, List<String>>>>()
    private var previousDate = ""
    private var currTask = ""

    override fun processData(content: List<String>): Boolean {

        if (content.size == 1 || content[0] == content.joinToString("")){
            tasks[content[0]] = mutableMapOf()
            currTask = content[0]

        }else if (
            content[0] == "" &&
            content[1] != "" &&
            currTask != "" &&
            content.size >= 3
        ){
            this.tasks[this.currTask]!![this.previousDate]!![content[1]] = content.subList(2,content.size)

        }else if (
            currTask != "" &&
            content.size >= 3 &&
            content[0] != "" &&
            content[1] != ""
        ){
            if (this.tasks[this.currTask]!![content[0]] != null){
                this.tasks[this.currTask]!![content[0]]!![content[1]] = content.subList(2, content.size)
            }else{
                this.tasks[this.currTask]!![content[0]] = mutableMapOf(Pair(content[1], content.subList(2, content.size)))
            }
            this.previousDate = content[0]

        }else{
            return false
        }
        return true
    }

    /**
     * If you are also reading other files, this function should be executed
     * AFTER all the other files are successfully processed!
     */
    private fun evaluateData(){

        // Map the names of tasks to their ids
        val taskNames = mutableMapOf<String, String>()
        for (task in Tasks.getAllTasks()){
            taskNames[task.name] = task.id
        }

        // Map the names of people to their ids
        val people = mutableMapOf<String, String>()
        for (personId in People.getAllPeopleIDs()){
            val person = People.getPersonById(personId)!!
            people["${person.firstname} ${person.lastname}"] = personId
        }

        // iterate over every task
        for (newTask in this.tasks){
            val parentTaskId = taskNames[newTask.key]
            if (parentTaskId == null) {
                println("${newTask.key} does not exist. The system is case sensitive!")
                continue
            }
            val parentTask = Tasks.getTask(parentTaskId)!!

            for (date in newTask.value){
                val day: Date

                // ensures that the day exists and that the task will schedule a task on that day
                try{
                    day = Date(date.key)
                }catch(e: Exception){
                    println("Date $date is incompatible.")
                    continue
                }
                parentTask.dateTime.get(day) ?: continue

                // Iterates over the times
                for (entry in date.value){

                    // Makes sure that the data is valid
                    // And that the abstract Task is going to schedule a task on that day
                    val timeString = entry.key.split("-")
                    val time: Pair<Time, Time>
                    try{
                        time = Pair(
                            Time(timeString[0]),
                            Time(timeString[1])
                        )
                    }catch (e: Exception){
                        println("$e arouse while parsing time ${entry.key}")
                        continue
                    }

                    // Gets the ID's of the people that are assigned to the task and do exist
                    val personIds = mutableListOf<String>()

                    for (personName in entry.value){
                        if (people[personName] != null){
                            personIds.add(people[personName]!!)
                        }else{
                            println("Person $personName does not exist!")
                            println("This program is case-sensitive!!")
                        }
                    }

                    parentTask.schedule(day, time, personIds.toTypedArray())
                }


            }
        }
    }

    override fun cleanUp() {
        evaluateData()
    }
}