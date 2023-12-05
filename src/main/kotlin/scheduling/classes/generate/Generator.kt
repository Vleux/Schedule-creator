package scheduling.classes.generate

import exceptions.NoPersonAvailable
import scheduling.classes.data.Limit
import scheduling.classes.enums.Fairness
import scheduling.classes.task.PersonList
import scheduling.classes.time.Date
import scheduling.classes.time.Time
import scheduling.classes.time.WorkDays
import scheduling.objects.NationBackend
import scheduling.objects.People
import scheduling.objects.Schedule
import scheduling.objects.Tasks
import kotlin.properties.Delegates

class Generator {
    private lateinit var limits: Limit
    private var maxTime by Delegates.notNull<Int>()
    private var maximalFairnessTasks: Map<Int, Array<String>> = emptyMap()
    private var mediumFairnessTasks: Map<Int, Array<String>> = emptyMap()
    private var lowFairnessTasks: Map<Int, Array<String>> = emptyMap()
    private var scheduleLast: MutableList<String> = mutableListOf()

    // saves the people that are available on that day. The ones that are arriving/leaving and the ones that are there the whole day
    private var peopleAvailable: PersonList = PersonList()

    /**
     * Generate the lists
     */
    fun start(){
        // calculations previous to generatino
        this.calculateLimits()
        this.peopleAvailable.boot()
        this.sortTasks()


        // Generation
        this.scheduleTask(Fairness.MAXIMUM)
        this.checkCompleteSatisfiedTasks()

        this.scheduleTask(Fairness.MEDIUM)
        this.checkCompleteSatisfiedTasks()

        this.scheduleTask(Fairness.LOW)
        this.checkCompleteSatisfiedTasks()

        this.scheduleLast()
    }


    /**
     * Calculates the Limits while ensuring that the Limits are not too low
     */
    private fun calculateLimits(){
        // Calculate the Amount of Tasks.csv (appearance of a Task * needed People)
        var allTaskCount = 0
        var maxFairnessTaskCount = 0
        var medFairnessTaskCount = 0
        var lowFairnessTaskCount = 0
        val tasks = Tasks.getAllTasks()

        for (task in tasks){
            var count = 0

            val dateTime = task.dateTime
            for (key in dateTime.keys){
                count += dateTime[key]!!.size
            }

            count *= task.numberOfPeople
            allTaskCount += count


            when (task.requiredFairness){
                Fairness.LOW -> lowFairnessTaskCount += count
                Fairness.MEDIUM -> medFairnessTaskCount += count
                Fairness.MAXIMUM -> maxFairnessTaskCount += count
            }
        }

        // Security check
        if ((maxFairnessTaskCount + medFairnessTaskCount + lowFairnessTaskCount) != allTaskCount){
            println("VITAL PROBLEM IN CALCULATING TASKS. PLEASE FIX.")
            println("PROGRAM WILL BE HALTED IMMEDIATELY")
            throw Error("FATAL")
        }

        // Calculate the Limits (task / amountOfPeople)

        val amountOfPeople = this.getAmountOfPeople()

        var genLimit = (allTaskCount).toDouble() / (amountOfPeople).toDouble()
        var maxFairLimit = (maxFairnessTaskCount).toDouble() / (amountOfPeople).toDouble()
        var medFairLimit: Double = (medFairnessTaskCount).toDouble() / (amountOfPeople).toDouble()
        var lowFairLimit = (lowFairnessTaskCount).toDouble() / (amountOfPeople).toDouble()

        // Checks that the Limit is not too small - if it is it will be increased
        while (genLimit * amountOfPeople < allTaskCount){genLimit++}
        while (maxFairLimit * amountOfPeople < maxFairnessTaskCount){maxFairLimit++}
        while (medFairLimit * amountOfPeople < medFairnessTaskCount){medFairLimit++}
        while (lowFairLimit * amountOfPeople < lowFairnessTaskCount){lowFairLimit++}

        if (genLimit.toInt().toDouble() != genLimit){
            genLimit++
        }
        if (maxFairLimit.toInt().toDouble() != maxFairLimit){
            maxFairLimit++
        }
        if (medFairLimit.toInt().toDouble() != medFairLimit){
            medFairLimit++
        }
        if (lowFairLimit.toInt().toDouble() != lowFairLimit){
            lowFairLimit++
        }

        // Save the Limits
        this.limits = Limit(
            genLimit.toInt(),
            maxFairLimit.toInt(),
            medFairLimit.toInt(),
            lowFairLimit.toInt()
        )
    }

    /**
     * Calculates the working-time people (whole time = 1, half-time = 0.5 ...)
     * Adds it up in the end and rounds down (aka casts to an INT)
     *
     * saves the relative working-time in the People
     */
    private fun getAmountOfPeople(): Int{
        // Get the first arrival, and the last leave of a person; the "maxDays"
        // Could also be done with the tasks - but people are more important!

        val ids = People.getAllPeopleIDs()
        var firstDay: Date = People.getPersonById(ids.first())!!.visit.getFirstDay()
        var lastDay: Date = People.getPersonById(ids.first())!!.visit.getLastDay()

        for (id in ids){
            if (People.getPersonById(id)!!.visit.getFirstDay() < firstDay){
                firstDay = People.getPersonById(id)!!.visit.getFirstDay()
            }
            if (People.getPersonById(id)!!.visit.getLastDay() > lastDay){
                lastDay = People.getPersonById(id)!!.visit.getLastDay()
            }
        }

        this.maxTime = WorkDays(firstDay, Time("01:00"), lastDay, Time("23:00")).getWorkDays().size

        /*
        Calculates the available Work time (the number of people).
        If somebody is only there for 5 out of 10 days, they will be counted as 0.5 Persons to decrease the
        number of tasks he has to fulfill.
         */
        var people = 0.0

        for (id in ids){
            val person = People.getPersonById(id)!!
            var amountOfDays = person.visit.getWorkDays().size.toDouble()

            if (person.visit.timeOfArrival > Time("12:00")){
                amountOfDays -= 0.5
            }
            if (person.visit.timeOfLeave < Time("14:00")){
                amountOfDays -= 0.5
            }
            person.timePercentage = amountOfDays / maxTime
            people += person.timePercentage
        }

        return people.toInt()
    }

    /**
     * Sorts the abstract Tasks.csv into fairness categories and saves the number of tasks they exclude
     */
    private fun sortTasks(){
        val tasks = Tasks.getAllTasks()
        val maxF: MutableMap<Int, Array<String>> = mutableMapOf()
        val medF: MutableMap<Int, Array<String>> = mutableMapOf()
        val lowF: MutableMap<Int, Array<String>> = mutableMapOf()

        for (task in tasks){

            when (task.requiredFairness){
                Fairness.MAXIMUM -> {
                    if (maxF[task.excludesTasks.size] == null){
                        maxF[task.excludesTasks.size] = arrayOf(task.id)
                    }else {
                        maxF[task.excludesTasks.size] = maxF[task.excludesTasks.size]!!.plus(task.id)
                    }
                }
                Fairness.MEDIUM -> {
                    if (medF[task.excludesTasks.size] == null){
                        medF[task.excludesTasks.size] = arrayOf(task.id)
                    }else{
                        medF[task.excludesTasks.size] = medF[task.excludesTasks.size]!!.plus(task.id)
                    }
                }
                Fairness.LOW -> {
                    if (lowF[task.excludesTasks.size] == null){
                        lowF[task.excludesTasks.size] = arrayOf(task.id)
                    }else{
                        lowF[task.excludesTasks.size] = lowF[task.excludesTasks.size]!!.plus(task.id)
                    }
                }
            }
        }

        this.lowFairnessTasks = lowF.toMap()
        this.mediumFairnessTasks = medF.toMap()
        this.maximalFairnessTasks = maxF.toMap()
    }

    /**
     * This function iterates over the currently scheduled tasks and ensures that they have enough people.
     * It does not care about limits or nations. It just gets those people with the lease tasks in the
     * fitting category.
     */
    private fun checkCompleteSatisfiedTasks(){
        for (taskId in Schedule.getAllTasks()){
            val task = Schedule.getScheduledTask(taskId)!!

            if (task.peopleNeeded() > 0){
                val amount = task.peopleNeeded()
                val fairness = Tasks.getTask(task.parentTask)!!.requiredFairness

                val nationTarget = mutableMapOf<String, Double>()
                for (nation in NationBackend.getAllNations()){
                    nationTarget[nation] = Double.MAX_VALUE
                }
                val limit = Limit(
                    Int.MAX_VALUE,
                    Int.MAX_VALUE,
                    Int.MAX_VALUE,
                    Int.MAX_VALUE
                )

                val people = this.peopleAvailable.getPeopleWithMinimalTasks(
                    Schedule.getDateOfScheduledTask(taskId)!!,
                    amount,
                    fairness,
                    taskId,
                    nationTarget,
                    limit
                ) ?: throw NoPersonAvailable("The person available has generated bullshit. (PersonList.getPeopleWithMinimalTasks()")

                for (person in people){
                    task.addPerson(person)
                }
            }
        }
    }

    /**
     * Sorts the Tasks
     * Ensures that incompatible tasks & parallel tasks are avoided.
     */
    private fun scheduleTask(fairness: Fairness){

        // initialize values with right fairness
        var taskMap: Map<Int, Array<String>> = emptyMap()
        var keys = listOf<Int>()

        when (fairness){
            Fairness.MAXIMUM    -> {
                keys = this.maximalFairnessTasks.keys.sorted().reversed()
                taskMap = this.maximalFairnessTasks
            }
            Fairness.MEDIUM     -> {
                keys = this.mediumFairnessTasks.keys.sorted().reversed()
                taskMap = this.mediumFairnessTasks
            }
            Fairness.LOW        -> {
                keys = this.lowFairnessTasks.keys.sorted().reversed()
                taskMap = this.lowFairnessTasks
            }
        }

        // start with the tasks that exclude the most others
        for (key in keys){
            val tasks = taskMap[key]!!

            for (taskId in tasks){

                // schedule the abstract task & get those tasks
                val absTask = Tasks.getTask(taskId)!!
                absTask.scheduleAll()
                if (absTask.numberOfPeople < 0){
                    this.scheduleLast.add(absTask.id)
                    continue
                }
                val scheduledTasks = Schedule.getScheduledTasksOf(absTask.id)

                //iterate over the scheduledTasks
                for (entry in scheduledTasks){                    val date = entry.value
                    val schedTask = Schedule.getScheduledTask(entry.key)!!

                    // get available people
                    val avPeople = this.peopleAvailable.getAvailablePeople(date)

                    if (avPeople.isEmpty()) {
                        println(date)
                        throw NoPersonAvailable("A task is scheduled on a date where no person is visiting.\nExiting program ...")
                    }

                    // Get the available people in reference to their Nations & calculate the ratio
                    val avNations = this.peopleAvailable.getPeopleWithNations(avPeople.keys.toTypedArray())
                    val ratio = mutableMapOf<String, Double>()
                    for (nation in avNations){
                        ratio[nation.key] =  nation.value.size.toDouble() / avPeople.size.toDouble()
                    }

                    for (nation in ratio.keys){
                        ratio[nation] = ratio[nation]!! * schedTask.peopleNeeded()
                    }

                    //Receive all People. Checking (incompatible tasks, number of chores & co) is done in PersonLists
                    val people = this.peopleAvailable.getPeopleWithMinimalTasks(
                        date,
                        schedTask.peopleNeeded(),
                        fairness,
                        schedTask.id,
                        ratio,
                        this.limits
                    ) ?: throw NoPersonAvailable("Something went wrong while searching for available persons. Please fix")
                    for (person in people){
                        schedTask.addPerson(person)
                    }
                }
            }
        }
    }

    private fun scheduleLast(){
        for (taskId in this.scheduleLast){

            // schedule the abstract task & get those tasks
            val absTask = Tasks.getTask(taskId)!!
            absTask.scheduleAll()
            val scheduledTasks = Schedule.getScheduledTasksOf(absTask.id)

            //iterate over the scheduledTasks
            function@for (entry in scheduledTasks){
                println(entry)
                val date = entry.value
                val schedTask = Schedule.getScheduledTask(entry.key)!!

                //Receive all People. Checking only for incompatible and parallel tasks
                val people = this.peopleAvailable.getAllAvailablePeople(
                    date,
                    schedTask.id,
                ) ?: continue

                for (person in people){
                    schedTask.addPerson(person)
                }
            }
        }
    }

}