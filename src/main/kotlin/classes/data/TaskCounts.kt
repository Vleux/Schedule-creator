package classes.data

import objects.People

class TaskCounts {

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