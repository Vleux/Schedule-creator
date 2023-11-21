package classes.data

import classes.time.Date
import classes.time.WorkDays
import objects.People

class PersonLists {

    private enum class Availability{
        ALL_DAY, ARRIVAL, LEAVE
    }

    // saves the people that are available on that day. The ones that are arriving/leaving and the ones that are there the whole day
    private lateinit var peopleAvailable: MutableMap< Date, MutableMap< Availability, ArrayList<String> > >
    // Saves the amount of general tasks in relation to the persons
    private lateinit var genTasksPerPerson: HashMap<Int, ArrayList<String>>
    // Saves the amount of high fairness tasks in relation to the persons
    private lateinit var maxFairnessPerPerson: HashMap<Int, ArrayList<String>>
    // Saves the amount of medFairness Tasks in relation to the persons
    private lateinit var medFairnessPerPerson: HashMap<Int, ArrayList<String>>

    // Saves the Persons with reference to their tasks
    private lateinit var peoplesTasks: HashMap<String, TaskCount>

    init {
        this.generateAmountOfTasks()
        this.generateAvailablePersons()
    }

    /**
     * USE ONLY IN INIT!
     * Assigns every person a value of 0 tasks
     */
    private fun generateAmountOfTasks(){
        val ids = People.getAllPeopleIDs().toCollection(ArrayList())

        this.genTasksPerPerson = hashMapOf(Pair(0, ids))
        this.maxFairnessPerPerson = hashMapOf(Pair(0, ids))
        this.medFairnessPerPerson = hashMapOf(Pair(0, ids))

        for (id in ids){
            this.peoplesTasks[id] = TaskCount()
        }
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
            this.peopleAvailable[date]!![availability]!!.add(id)

        }else if (this.peopleAvailable[date] != null){
            this.peopleAvailable[date]!![availability] = arrayListOf(id)

        }else{
            this.peopleAvailable[date] = mutableMapOf(Pair(availability, arrayListOf(id)))
        }
    }

    /**
     * Changes only the general amount of tasks. Does not work, if there would be less general tasks
     * than medium and maximum high fairness tasks.
     * returns true on success, false on failure
     */
    fun changePersonGeneralTasks(id: String, count: Int): Boolean{
        if (!People.doesPersonExist(id)){
            return false
        }

        // Just change the value, checks are done in TaskCount!

        val cache = this.peoplesTasks[id]!!.generalTasks
        //remove old
        this.genTasksPerPerson[this.peoplesTasks[id]!!.generalTasks]!!.remove(id)

        // change value
        this.peoplesTasks[id]!!.generalTasks = count

        // save again
        if (this.genTasksPerPerson[count] != null){
            this.genTasksPerPerson[count] = arrayListOf(id)
        }else{
            this.genTasksPerPerson[count]!!.add(id)
        }

        return (cache != count)
    }

    /**
     * Changes the amount of the maximum fairness tasks and the general tasks.
     * Returns false on failure
     */
    fun changePersonMaximumFairnessTask(id: String, count: Int): Boolean{
        if (!People.doesPersonExist(id)){
            return false
        }

        //Just changing the value, checks are done inside TaskCount!

        val cache = this.peoplesTasks[id]!!.maximalFairnessTasks
        this.maxFairnessPerPerson[cache]!!.remove(id)

        this.peoplesTasks[id]!!.maximalFairnessTasks = count

        // saving
        if (this.maxFairnessPerPerson[count] != null){
            this.maxFairnessPerPerson[count] = arrayListOf(id)
        }else{
            this.maxFairnessPerPerson[count]!!.add(id)
        }

        return (cache != count)

    }

    /**
     * Changes the amount of medium fairness tasks and the general tasks.
     * Returns false on failures
     */
    fun changePersonMediumFairnessTask(id: String, count: Int): Boolean{
        if (!People.doesPersonExist(id)){
            return false
        }

        //Just changing the value, checks are done inside TaskCount!

        val cache = this.peoplesTasks[id]!!.mediumFairnessTasks
        this.medFairnessPerPerson[cache]!!.remove(id)

        this.peoplesTasks[id]!!.mediumFairnessTasks= count

        // saving
        if (this.medFairnessPerPerson[count] != null){
            this.medFairnessPerPerson[count] = arrayListOf(id)
        }else{
            this.medFairnessPerPerson[count]!!.add(id)
        }

        return (cache != count)
    }

}