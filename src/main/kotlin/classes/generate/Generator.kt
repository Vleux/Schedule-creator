package classes.generate

import classes.data.Limit
import classes.data.NationList
import classes.data.TaskCounts
import classes.enums.Availability
import classes.enums.Fairness
import classes.time.Date
import classes.time.Time
import classes.time.WorkDays
import objects.People
import objects.Tasks
import kotlin.properties.Delegates

class Generator {
    private lateinit var taskCounts: TaskCounts
    private lateinit var limits: Limit
    private var maxTime by Delegates.notNull<Int>()
    private var maximalFairnessTasks: Map<Int, Array<String>> = emptyMap()
    private var mediumFairnessTasks: Map<Int, Array<String>> = emptyMap()
    private var lowFairnessTasks: Map<Int, Array<String>> = emptyMap()

    // saves the people that are available on that day. The ones that are arriving/leaving and the ones that are there the whole day
    private lateinit var peopleAvailable: MutableMap< Date, MutableMap<Availability, NationList > >

    /**
     * Generate the lists
     */
    fun start(){
        this.calculateLimits()
        this.taskCounts = TaskCounts()
        this.generateAvailablePersons()P
        this.sortTasks()
        this.scheduleMaximalFairnessTasks()
    }

    /**
     * USE ONLY IN INIT!
     * reads through all persons and saves their availabilities
     */
    private fun generateAvailablePersons(){
        this.peopleAvailable = hashMapOf()
        val ids = People.getAllPeopleIDs()

        for (id in ids){

            val cache = People.getPersonById(id)!!.visit
            val firstDay = cache.getFirstDay()
            val lastDay = cache.getLastDay()
            val dates = cache.getWorkDays().toMutableList(); dates.remove(firstDay); dates.remove(lastDay)

            for (date in dates){
                this.addPersonToDay(id, date, Availability.ALL_DAY)
            }

            this.addPersonToDay(id, firstDay, Availability.ARRIVAL)
            this.addPersonToDay(id, lastDay, Availability.LEAVE)

        }
    }

    /**
     * USE ONLY IN INIT!
     * Adds a Person to one day (beeing available) with the correct ENUM-Identifier
     */
    private fun addPersonToDay(id: String, date: Date, availability: Availability){
        if (this.peopleAvailable[date]?.get(availability) != null){
            this.peopleAvailable[date]!![availability]!!.addPerson(People.getPersonById(id)!!.nationality, id)

        }else if (this.peopleAvailable[date] != null){
            this.peopleAvailable[date]!![availability] = NationList()
            addPersonToDay(id, date, availability)

        }else{
            this.peopleAvailable[date] = mutableMapOf(Pair(availability, NationList()))
            addPersonToDay(id, date, availability)
        }
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
        If somebody is only there for 5 out of 10 days, he will be counted as 0.5 Persons in order to decrease the
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
     * sorts the Tasks that require the maximal fairness
     */
    private fun scheduleMaximalFairnessTasks(){
        val keys = this.maximalFairnessTasks.keys.sorted().reversed()

        for (key in keys){
            val tasks = this.maximalFairnessTasks[key]!!

            for (task in tasks){

            }
        }
    }
}