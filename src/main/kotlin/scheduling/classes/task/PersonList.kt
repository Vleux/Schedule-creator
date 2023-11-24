package scheduling.classes.task

import scheduling.classes.data.Limit
import scheduling.classes.enums.Availability
import scheduling.classes.enums.Fairness
import scheduling.classes.time.Date
import scheduling.classes.time.Time
import scheduling.objects.NationBackend
import scheduling.objects.People
import scheduling.objects.Schedule
import scheduling.objects.Tasks
import kotlin.math.roundToInt

class PersonList {
    private var person: ArrayList<String> = arrayListOf()
    private var nation: ArrayList<String> = arrayListOf()
    private var counts: ArrayList<TaskCount> = arrayListOf()
    private var existentNations: ArrayList<String> = arrayListOf()

    fun boot(){
        for (personId in People.getAllPeopleIDs()){
            this.addPerson(personId)
        }
    }

    fun addPerson(personId: String): Boolean{
        val person = People.getPersonById(personId)
        if (this.person.contains(personId) || person == null){
            return false
        }
        this.person.add(personId)
        this.nation.add(person.nationality)
        this.counts.add(TaskCount())

        if (!existentNations.contains(person.nationality)){
            this.existentNations.add(person.nationality)
        }
        return true
    }

    fun deletePerson(personId: String): Boolean{
        val index = person.indexOf(personId)
        if (index == -1){
            return false
        }
        this.nation.removeAt(index)
        this.counts.removeAt(index)
        this.person.removeAt(index)
        return true
    }

    fun getNation(personId: String): String{
        return this.nation[
                this.person.indexOf(personId)
        ]
    }

    /**
     * Returns the counter for a person.
     * Also used to change counters.
     */
    fun getCounts(personId: String): TaskCount {
        return this.counts[
                this.person.indexOf(personId)
        ]
    }

    fun getPeopleFromNation(nation: String): Array<String>{
        val nations = NationBackend.parseNation(nation) ?: return emptyArray()
        val cache = mutableListOf<String>()
        for (i in nations.indices){
            if (this.nation[i] == nation){
                cache.add(this.person[i])
            }
        }
        return cache.toTypedArray()
    }

    /**
     * Gets every person, that is available the whole day, leaves after 13:00 o'clock and arrives before 13:00 o'clock
     */
    fun getAvailablePeople(date: Date): Map<String, Availability>{
        val availablePersons = mutableMapOf<String, Availability>()
        for (personId in this.person){
            val pers = People.getPersonById(personId)

            if (pers!!.visit.getFirstDay() == date && pers.visit.timeOfArrival <= Time("13:00")){
                availablePersons[personId] = Availability.ARRIVAL
            }else if (pers.visit.getLastDay() == date && pers.visit.timeOfLeave >= Time("13:00")){
                availablePersons[personId] = Availability.LEAVE
            }else{
                availablePersons[personId] = Availability.ALL_DAY
            }
        }
        return availablePersons.toMap()
    }

    /**
     * Returns all the Available People for a specified time.
     */
    fun getAvailablePeople(date: Date, begin: Time, end: Time): Array<String>{
        val people = getAvailablePeople(date).toMutableMap()
        for (entry in people) {
            if (entry.value == Availability.LEAVE){
                val person = People.getPersonById(entry.key)!!
                if (person.visit.timeOfLeave < end){
                    people.remove(entry.key)
                }else if (person.visit.timeOfArrival > begin){
                    people.remove(entry.key)
                }
            }
        }
        return people.keys.toTypedArray()
    }

    /**
     * Returns the people that are available during that time with the least tasks.
     *
     */
    fun getPeopleWithMinimalTasks(
        date: Date,
        amount: Int,
        pTasks: Fairness,
        scheduledTaskId: String,
        nationTarget: MutableMap<String, Double>,
        limit: Limit
    ): Array<String>?{

        println("""
            #####################
            #####################
            #####################
            ${Tasks.getTask(Schedule.getScheduledTask(scheduledTaskId)!!.parentTask)!!.name}
        """.trimIndent())

        // some values for the function
        val schedTask = Schedule.getScheduledTask(scheduledTaskId) ?: return null
        val begin = schedTask.time.first
        val end = schedTask.time.second

        val people = this.getAvailablePeople(date, begin, end)
        val finPeople = mutableMapOf<Int, ArrayList<String>>()  // Collects the people in reference of their amount of tasks
        val nationAchieved = mutableMapOf<String, Int>()
        val result = mutableListOf<String>()


        //checking that the amount of people fits the nationTarget
        var cache: Double = 0.0
        for (nation in nationTarget){
            cache += nation.value
        }
        if (cache.roundToInt() < amount){
            return null
        }
        // The different behaviour for different fairness-level. Could have written the Code in the other direction(when inside)
        // but in this way, the "when" is only executed once
        when (pTasks){
            Fairness.MAXIMUM -> {
                // saves the people in relation to their amount of maximalFairnessTasks
                for (personId in people){

                    val tasks = this.getCounts(personId).maximalFairnessTasks

                    // Continues if there are parallel, excluded tasks within 24 hours
                    if (!this.freeForTask(
                            personId,
                            scheduledTaskId,
                            date,
                        )) continue

                    // Saves the person with it's amount of task
                    if (finPeople[tasks] == null){
                        finPeople[tasks] = arrayListOf(personId)
                    }else{
                        finPeople[tasks]!!.add(personId)
                    }
                }

                var resultSize = result.size
                // gets the first people, that are free and have the lowest amout of maximal fairness tasks
                while (resultSize < amount){

                    // gets the smallest amount of chores
                    val smallestCount: Int = finPeople.keys.min()
                    val arr = finPeople[smallestCount]!!.toTypedArray()

                    for (personId in arr){
                        val person = People.getPersonById(personId)!!

                        // Ensure that the data is valid & people aren't overwhelmed
                        if (getCounts(personId).maximalFairnessTasks == limit.getMaximalFairnessLimit()){
                            println("Fatal error with limits going on")
                            throw Error("")
                        }
                        if (!nationTarget.keys.contains(person.nationality)) {
                            println("wrong nation")
                            continue
                        }
                        if (nationAchieved[person.nationality] == null){
                            nationAchieved[person.nationality] = 0
                        }

                        // add person to the list and increase it's work-count if more people of his nationality are needed
                        if (nationAchieved[person.nationality]!! < nationTarget[person.nationality]!!.roundToInt()){
                            result.add(personId)
                            resultSize++

                            this.getCounts(personId).maximalFairnessTasks++
                            finPeople[smallestCount]!!.remove(personId)

                            if (finPeople[smallestCount + 1] == null){
                                finPeople[smallestCount + 1] = arrayListOf(personId)
                            }else{
                                finPeople[smallestCount + 1]!!.add(personId)
                            }
                        }
                        if (amount <= resultSize) break
                    }
                }
            }
            Fairness.MEDIUM -> {
                // saves the people in relation to their amount of maximalFairnessTasks
                for (personId in people){
                    val tasks = this.getCounts(personId).mediumFairnessTasks

                    // Continues if there are parallel, excluded tasks within 24 hours
                    if (!this.freeForTask(
                            personId,
                            scheduledTaskId,
                            date,
                        )){continue}

                    // Saves the person with its amount of task
                    if (finPeople[tasks] == null){
                        finPeople[tasks] = arrayListOf(personId)
                    }else{
                        finPeople[tasks]!!.add(personId)
                    }
                }


                var resultSize = result.size
                // gets the first people, that are free and have the lowest amout of maximal fairness tasks
                while (amount > resultSize){

                    // gets the smallest amount of chores
                    val smallestCount: Int = finPeople.keys.min()
                    val arr = finPeople[smallestCount]!!.toTypedArray()

                    for (personId in arr) {
                        val person = People.getPersonById(personId)!!

                        // Ensure that the data is valid & people aren't overwhelmed
                        if (getCounts(personId).mediumFairnessTasks == limit.getMediumFairnessLimit()) {
                            println("Fatal error with limits going on")
                            throw Error("")
                        }
                        if (!nationTarget.keys.contains(person.nationality)) {
                            println("wrong nation")
                            continue
                        }
                        if (nationAchieved[person.nationality] == null) {
                            nationAchieved[person.nationality] = 0
                        }

                        // add person to the list and increase it's work-count if more people of his nationality are needed
                        if (nationAchieved[person.nationality]!! < nationTarget[person.nationality]!!.roundToInt()) {
                            result.add(personId)
                            resultSize++

                            this.getCounts(personId).mediumFairnessTasks++
                            finPeople[smallestCount]!!.remove(personId)

                            if (finPeople[smallestCount + 1] == null) {
                                finPeople[smallestCount + 1] = arrayListOf(personId)
                            } else {
                                finPeople[smallestCount + 1]!!.add(personId)
                            }
                        }
                        if (amount <= resultSize) break
                    }
                }
            }
            Fairness.LOW -> {
                // saves the people in relation to their amount of maximalFairnessTasks
                for (personId in people) {
                    val tasks = this.getCounts(personId).lowFairnessTasks

                    // Continues if there are parallel, excluded tasks within 24 hours
                    if (!this.freeForTask(
                            personId,
                            scheduledTaskId,
                            date,
                        )) continue


                    // Saves the person with its amount of task
                    if (finPeople[tasks] == null) {
                        finPeople[tasks] = arrayListOf(personId)
                    } else {
                        finPeople[tasks]!!.add(personId)
                    }
                }

                var resultSize = result.size
                // gets the first people, that are free and have the lowest amout of maximal fairness tasks
                while (amount > resultSize) {

                    // gets the smallest number of chores
                    val smallestCount: Int = finPeople.keys.min()
                    val arr = finPeople[smallestCount]!!.toTypedArray()

                    for (personId in arr) {
                        val person = People.getPersonById(personId)!!

                        // Ensure that the data is valid & people aren't overwhelmed
                        if (getCounts(personId).lowFairnessTasks == limit.getLowFairnessLimit()) {
                            println("Fatal error with limits going on")
                            throw Error("")
                        }
                        if (!nationTarget.keys.contains(person.nationality)) {
                            println("wrong nation")
                            continue
                        }
                        if (nationAchieved[person.nationality] == null) {
                            nationAchieved[person.nationality] = 0
                        }

                        println("""
                            -------------------------
                            
                            Previous finPeople: $finPeople
                        
                        """.trimIndent())

                        // add person to the list and increase it's work-count if more people of his nationality are needed
                        if (nationAchieved[person.nationality]!! < nationTarget[person.nationality]!!.roundToInt()) {
                            result.add(personId)
                            resultSize++

                            this.getCounts(personId).lowFairnessTasks++
                            finPeople[smallestCount]!!.remove(personId)

                            if (finPeople[smallestCount + 1] == null) {
                                finPeople[smallestCount + 1] = arrayListOf(personId)
                            } else {
                                finPeople[smallestCount + 1]!!.add(personId)
                            }
                        }
                        println("Afterwards finPeople: $finPeople")
                        println()
                        println("-------------------------")
                        if (amount <= resultSize) break
                    }
                }
            }
        }
        return result.toTypedArray()

    }

    private fun freeForTask(personId: String, scheduledTId: String, date: Date): Boolean{
        val person = People.getPersonById(personId)!!
        val schedTasks = person.myTasks
        val scheduledTask = Schedule.getScheduledTask(scheduledTId)!!
        val parentTask = Tasks.getTask(scheduledTask.parentTask)!!
        val incompatibleTasks: ArrayList<String> = arrayListOf()

        for (taskId in (parentTask.excludesTasks + parentTask.excludedBy)){
            val task = Tasks.getTask(taskId)!!
            incompatibleTasks.addAll(task.children.toCollection(ArrayList()))
        }
        val inspectTasks = incompatibleTasks.intersect(schedTasks.keys)

        // Inspecting. If a concurrency is found, false is returned

        // Inspecting the excluded Tasks.csv.
        for (taskId in inspectTasks){
            val tryDate = date.copy()
            val task = Schedule.getScheduledTask(taskId)
            val taskDate = Schedule.getDateOfScheduledTask(scheduledTId)
            if (Schedule.getDateOfScheduledTask(scheduledTId) == tryDate){
                return false
            }
            tryDate.changeDate(1)
            //following day
            if (taskDate == tryDate){
                if (task!!.time.first < scheduledTask.time.second) return false
            }
            tryDate.changeDate(-2)
            // previous day
            if (taskDate == tryDate){
                if (task!!.time.second > scheduledTask.time.first) return false
            }
        }

        //Ensuring that there are no tasks in paralell
        for (entry in schedTasks){
            if (entry.value == date){
                val time = Schedule.getScheduledTask(entry.key)!!.time
                if (time.first < scheduledTask.time.second &&
                    time.first > scheduledTask.time.first){
                    return false
                }else if (time.second > scheduledTask.time.first &&
                    time.second < scheduledTask.time.second){
                    return false
                }
            }
        }

        return true
    }

    /**
     * Return the same list of People, but in dependence of their nation
     */
    fun getPeopleWithNations(persons: Array<String>): Map<String, ArrayList<String>>{
        val result = mutableMapOf<String, ArrayList<String>>()
        for (personId in persons){
            val nation = this.nation[ this.person.indexOf(personId) ]
            if (result[nation] == null){
                result[nation] = arrayListOf(personId)
            }else{
                result[nation]!!.add(personId)
            }
        }
        return result.toMap()
    }
}