package classes.generate

import classes.Exceptions.NoPersonAvailable
import classes.data.Limit
import classes.enums.Fairness
import classes.task.PersonList
import classes.time.Date
import classes.time.Time
import classes.time.WorkDays
import objects.NationBackend
import objects.People
import objects.Schedule
import objects.Tasks
import kotlin.properties.Delegates

class Generator {
    private lateinit var limits: Limit
    private var maxTime by Delegates.notNull<Int>()
    private var maximalFairnessTasks: Map<Int, Array<String>> = emptyMap()
    private var mediumFairnessTasks: Map<Int, Array<String>> = emptyMap()
    private var lowFairnessTasks: Map<Int, Array<String>> = emptyMap()

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
        this.scheduleMaximalFairnessTasks()
        this.checkCompleteSatisfiedTasks()

        this.scheduleMediumFairnessTasks()
        this.checkCompleteSatisfiedTasks()

        this.scheduleLowFairnessTasks()
        this.checkCompleteSatisfiedTasks()
    }


    /**
     * Calculates the Limits while ensuring that the Limits are not too low
     */
    private fun calculateLimits(){
        // Calculate the Amount of Tasks (appearance of a Task * needed People)
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
        var genLimit = allTaskCount / amountOfPeople
        var maxFairLimit = maxFairnessTaskCount / amountOfPeople
        var medFairLimit = medFairnessTaskCount / amountOfPeople

        // Checks that the Limit is not too small - if it is it will be increased
        while (genLimit * amountOfPeople < allTaskCount){genLimit++}
        while (maxFairLimit * amountOfPeople < maxFairnessTaskCount){maxFairLimit++}
        while (medFairLimit * amountOfPeople < medFairnessTaskCount){medFairLimit++}

        // Save the Limits
        this.limits = Limit(
            genLimit,
            maxFairLimit,
            medFairLimit
        )


    }

    /**
     * Calculates the working-time people (whole time = 1, half-time = 0.5 ...)
     * Adds it up in the end and rounds down (aka casts to an INT)
     *
     * saves the relative working-time in the People
     */
    private fun getAmountOfPeople(): Int{
        // Get the first arrival and the last leave of a person the "maxDays"
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
        this.maxTime = WorkDays(firstDay, Time("00:00"), lastDay, Time("00:00")).getWorkDays().size - 1

        /*
        Calculates the available Work time (the amount of people).
        If somebody is only there for 5 out of 10 days, they will be counted as 0.5 Persons to decrease the
        amount of tasks he has to fulfill.
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
            person.timePercentage = maxTime / amountOfDays
            people += person.timePercentage
        }

        return people.toInt()
    }

    /**
     * Sorts the abstract Tasks into fairness categories and saves the amount of tasks they exclude
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

            if (task.peopleNeeded() != 0){
                val amount = task.peopleNeeded()
                val fairness = Tasks.getTask(task.parentTask)!!.requiredFairness

                val nationTarget = mutableMapOf<String, Double>()
                for (nation in NationBackend.getAllNations()){
                    nationTarget[nation] = Double.MAX_VALUE
                }
                val limit = Limit(
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
     * Sorts the Tasks that require the maximal fairness.
     * Ensures that incompatible tasks & parallel tasks are avoided.
     */
    private fun scheduleMaximalFairnessTasks(){
        // starting with the tasks that exclude the most other tasks
        val keys = this.maximalFairnessTasks.keys.sorted().reversed()

        for (key in keys){
            val tasks = this.maximalFairnessTasks[key]!!

            for (taskId in tasks){
                // create the scheduled Tasks
                val absTask = Tasks.getTask(taskId)!!
                absTask.schedule()

                // get the id's of incompatible scheduled tasks
                val cache: MutableList<String> = mutableListOf()
                val incompatAbsTasks = absTask.excludedBy

                for (task in incompatAbsTasks){
                    cache.addAll(
                        Tasks.getTask(task)!!.children.toList()
                    )
                }

                val incompatibleScheduledTasks = cache.toTypedArray()


                // get the absolut ratio for every nation
                val dates = Schedule.getScheduledTasksOf(absTask.id)

                for (entry in dates){
                    val date = entry.value
                    val schedTask = Schedule.getScheduledTask(entry.key)!!
                    // get available people
                    val avPeople = this.peopleAvailable.getAvailablePeople(date)
                    if (avPeople.size == 0){
                        throw NoPersonAvailable("A task is scheduled on a date where no person is visiting.\nExiting program ...")
                    }
                    // Get the available people in reference to their Nations & calculate the ratio
                    val avNations = this.peopleAvailable.getPeopleWithNations(avPeople.keys.toTypedArray())
                    val ratio = mutableMapOf<String, Double>()
                    for (nation in avNations){
                        ratio[nation.key] = avPeople.size.toDouble() / nation.value.size.toDouble()
                    }

                    for (nation in ratio.keys){
                        ratio[nation] = ratio[nation]!! * schedTask.peopleNeeded()
                    }

                    //Receive all People. Checking (incompatible tasks, amount of chores & co) is done in PersonLists
                    val people = this.peopleAvailable.getPeopleWithMinimalTasks(
                        date,
                        schedTask.peopleNeeded(),
                        Fairness.MAXIMUM,
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

    /**
     * sorts the Tasks that require a medium fairness
     * Same structure as maximal fairness
     */
    private fun scheduleMediumFairnessTasks() {
        // starting with the tasks that exclude the most other tasks
        val keys = this.mediumFairnessTasks.keys.sorted().reversed()

        for (key in keys){
            val tasks = this.mediumFairnessTasks[key]!!

            for (taskId in tasks){
                // create the scheduled Tasks
                val absTask = Tasks.getTask(taskId)!!
                absTask.schedule()

                // get the id's of incompatible scheduled tasks
                val cache: MutableList<String> = mutableListOf()
                val incompatAbsTasks = absTask.excludedBy

                for (task in incompatAbsTasks){
                    cache.addAll(
                        Tasks.getTask(task)!!.children.toList()
                    )
                }


                // get the absolut ratio for every nation
                val dates = Schedule.getScheduledTasksOf(absTask.id)

                for (entry in dates){
                    val date = entry.value
                    val schedTask = Schedule.getScheduledTask(entry.key)!!
                    // get available people
                    val avPeople = this.peopleAvailable.getAvailablePeople(date)
                    if (avPeople.size == 0){
                        throw NoPersonAvailable("A task is scheduled on a date where no person is visiting.\nExiting program ...")
                    }
                    // Get the available people in reference to their Nations & calculate the ratio
                    val avNations = this.peopleAvailable.getPeopleWithNations(avPeople.keys.toTypedArray())
                    val ratio = mutableMapOf<String, Double>()
                    for (nation in avNations){
                        ratio[nation.key] = avPeople.size.toDouble() / nation.value.size.toDouble()
                    }

                    for (nation in ratio.keys){
                        ratio[nation] = ratio[nation]!! * schedTask.peopleNeeded()
                    }

                    //Receive all People. Checking (incompatible tasks, amount of chores & co) is done in PersonLists
                    val people = this.peopleAvailable.getPeopleWithMinimalTasks(
                        date,
                        schedTask.peopleNeeded(),
                        Fairness.MEDIUM,
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

    /**
     * Sorts the Tasks that require a low fairness
     * Same structure as maximal fairness
     */
    private fun scheduleLowFairnessTasks() {
        // starting with the tasks that exclude the most other tasks
        val keys = this.lowFairnessTasks.keys.sorted().reversed()

        for (key in keys){
            val tasks = this.lowFairnessTasks[key]!!

            for (taskId in tasks){
                // create the scheduled Tasks
                val absTask = Tasks.getTask(taskId)!!
                absTask.schedule()

                // get the id's of incompatible scheduled tasks
                val cache: MutableList<String> = mutableListOf()
                val incompatAbsTasks = absTask.excludedBy

                for (task in incompatAbsTasks){
                    cache.addAll(
                        Tasks.getTask(task)!!.children.toList()
                    )
                }


                // get the absolut ratio for every nation
                val dates = Schedule.getScheduledTasksOf(absTask.id)

                for (entry in dates){

                    val date = entry.value
                    val schedTask = Schedule.getScheduledTask(entry.key)!!

                    // get available people
                    val avPeople = this.peopleAvailable.getAvailablePeople(date)
                    if (avPeople.size == 0){
                        throw NoPersonAvailable("A task is scheduled on a date where no person is visiting.\nExiting program ...")
                    }
                    // Get the available people in reference to their Nations & calculate the ratio
                    val avNations = this.peopleAvailable.getPeopleWithNations(avPeople.keys.toTypedArray())
                    val ratio = mutableMapOf<String, Double>()
                    for (nation in avNations){
                        ratio[nation.key] = avPeople.size.toDouble() / nation.value.size.toDouble()
                    }

                    for (nation in ratio.keys){
                        ratio[nation] = ratio[nation]!! * schedTask.peopleNeeded()
                    }

                    //Receive all People. Checking (incompatible tasks, amount of chores & co) is done in PersonLists
                    val people = this.peopleAvailable.getPeopleWithMinimalTasks(
                        date,
                        schedTask.peopleNeeded(),
                        Fairness.LOW,
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

}