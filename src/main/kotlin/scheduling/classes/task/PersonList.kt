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
    private fun getCounts(personId: String): TaskCount {
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
     * Gets every person, that is available the whole day, is leaving or will arrive
     */
    fun getAvailablePeople(date: Date): Map<String, Availability>{
        val availablePersons = mutableMapOf<String, Availability>()

        for (personId in this.person){
            val visit = People.getPersonById(personId)!!.visit
            val firstDay = visit.getFirstDay()
            val lastDay = visit.getLastDay()

            if (firstDay == date){          // Arriving
                availablePersons[personId] = Availability.ARRIVAL

            }else if (lastDay == date){     // Leaving
                availablePersons[personId] = Availability.LEAVE

            }else if (firstDay < date && lastDay > date){   //Available
                availablePersons[personId] = Availability.ALL_DAY
            }
        }
        return availablePersons.toMap()
    }

    /**
     * Returns all the Available People for a specified time.
     */
    private fun getAvailablePeople(date: Date, begin: Time, end: Time): Array<String>{
        val people = getAvailablePeople(date).toMutableMap()

        for (entry in people.toMap()) {

            // If a person leaves before the task ends, the person will be removed
            if (entry.value == Availability.LEAVE){

                val person = People.getPersonById(entry.key)!!
                if (person.visit.timeOfLeave < end){
                    people.remove(entry.key)
                }

            }else if (entry.value == Availability.ARRIVAL){
                //If a person arrives after the task begins, the person will be removed

                val person = People.getPersonById(entry.key)!!
                if (person.visit.timeOfArrival > begin){
                    people.remove(entry.key)
                }
            }
        }
        return people.keys.toTypedArray()
    }

    /**
     * Receives the People in relation to their tasks at a certain day and time.
     * If a person is already a part of that scheduledTask, he won't be added
     */
    fun getPeopleWithMinimalTasks(
        date: Date,
        pAmount: Int,
        fairness: Fairness,
        scheduledTaskId: String,
        nationTarget: MutableMap<String, Double>,
        limit: Limit
    ): Array<String>?{
        var amount = pAmount
        // some values for the function
        val schedTask = Schedule.getScheduledTask(scheduledTaskId) ?: return null
        val begin = schedTask.time.first
        val end = schedTask.time.second

        val people = this.getAvailablePeople(date, begin, end)
        val finPeople = mutableMapOf<Int, ArrayList<String>>()  // Collects the people in reference of their amount of tasks
        val nationAchieved = mutableMapOf<String, Int>()
        val result = mutableListOf<String>()


        //checking that the amount of people fits the nationTarget
        var cache = 0.0
        for (nation in nationTarget){
            cache += nation.value
        }
        if (cache.roundToInt() < amount){
            throw Exception("The nationTargets have serious problems")
        }
        // The different behaviour for different fairness-level. Could have written the Code in the other direction(when inside)
        // but in this way, the "when" is only executed once

        var counter = 0
        // saves the people in relation to their amount of maximalFairnessTasks
        for (personId in people){

            if (schedTask.takenPeople.contains(personId)) {
                amount--
                continue
            }

            val tasks = when(fairness){
                Fairness.MAXIMUM -> this.getCounts(personId).maximalFairnessTasks
                Fairness.MEDIUM -> this.getCounts(personId).mediumFairnessTasks
                Fairness.LOW -> this.getCounts(personId).generalTasks
            }

            // Continue if there are parallel tasks or excluded tasks within 24 hours
            if (!this.freeForTask(
                    personId,
                    scheduledTaskId,
                    date,
                )) continue

            // Saves the person with its number of tasks
            if (finPeople[tasks] == null){
                finPeople[tasks] = arrayListOf(personId)
            }else{
                finPeople[tasks]!!.add(personId)
            }
            counter++
        }

        if (counter < amount) throw kotlin.Exception("Not enough people. The program has probably screwed up.")

        counter = 0

        var resultSize = 0

        // gets the smallest number of chores
        var smallestCount = finPeople.keys.min()
        // gets the first people, that are free and have the lowest number of maximal fairness tasks

        while (resultSize < amount && smallestCount <= finPeople.keys.max()){

            if (!finPeople.keys.contains(smallestCount)){
                smallestCount++
                continue
            }

            val arr = finPeople[smallestCount]!!.toTypedArray()

            for (personId in arr){
                val person = People.getPersonById(personId)!!


                /* Ensure that the data is valid & people aren't overwhelmed */
                if (fairness == Fairness.MAXIMUM && getCounts(personId).maximalFairnessTasks == limit.getMaximalFairnessLimit()){
                    println("Fatal error with limits going on")
                    throw Error("")
                }else if (fairness == Fairness.MEDIUM && getCounts(personId).mediumFairnessTasks == limit.getMediumFairnessLimit()){
                    println("Fatal error with limits going on")
                    throw Error("")
                }else if (fairness == Fairness.LOW && getCounts(personId).generalTasks == limit.getGeneralLimit()){
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

                    when (fairness){
                        Fairness.MAXIMUM -> this.getCounts(personId).maximalFairnessTasks++
                        Fairness.MEDIUM -> this.getCounts(personId).mediumFairnessTasks++
                        Fairness.LOW -> this.getCounts(personId).lowFairnessTasks++
                    }

                    finPeople[smallestCount]!!.remove(personId)
                    if (finPeople[smallestCount]!!.isEmpty()){
                        finPeople.remove(smallestCount)
                    }

                    if (finPeople[smallestCount + 1] == null){
                        finPeople[smallestCount + 1] = arrayListOf(personId)
                    }else{
                        finPeople[smallestCount + 1]!!.add(personId)
                    }
                }
                if (amount <= resultSize) break
            }
            counter++
            if (counter == 1000) throw Error("Infinite Loop detected")

            smallestCount++

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

        //Ensuring that there are no tasks in parallel
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