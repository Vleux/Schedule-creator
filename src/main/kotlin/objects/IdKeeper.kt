package objects

/**
 * Keeps track of the given ID's and creates new ones
 */
object IdKeeper {
    private var nextPersonID: UInt = 0u
    private var nextTaskID: UInt = 0u
    private var nextScheduledTaskID: UInt = 0u

    fun getNextPersonId(): String{
        val cache =  "Person-${this.nextPersonID}"
        this.nextPersonID++
        return cache
    }

    fun getNextTaskId(): String {
        val cache =  "Task-${this.nextTaskID}"
        this.nextTaskID++
        return cache
    }

    fun getNextScheduledTaskId(): String {
        val cache = "ScheduledTask-${this.nextScheduledTaskID}"
        this.nextScheduledTaskID++
        return cache
    }
}