package scheduling.objects

/**
 * Keeps track of the given ID's and creates new ones
 */
object IdKeeper {
    private var nextPersonID: UInt = 0u
    private var nextTaskID: UInt = 0u
    private var nextScheduledTaskID: UInt = 0u

    private var deletedPersonId: ArrayList<String> = arrayListOf()
    private var deletedTaskId: ArrayList<String> = arrayListOf()
    private var deletedScheduledTaskId: ArrayList<String> = arrayListOf()

    fun getNextPersonId(): String {
        val cache = "Person-$nextPersonID"
        nextPersonID++
        return cache
    }

    fun getNextTaskId(): String {
        val cache =  "Task-$nextTaskID"
        nextTaskID++
        return cache
    }

    fun getNextScheduledTaskId(): String {
        val cache = "ScheduledTask-$nextScheduledTaskID"
        nextScheduledTaskID++
        return cache
    }

    /**
     * Checks if an id is valid.
     * First: Checks that the specific ID is not deleted.
     * Second: Checks that the ID is smaller than the nextId
     */
    fun isIdValid(id: String): Boolean{
        when {
            id.contains("Person-") -> {
                return if (deletedPersonId.contains(id)){
                    false
                }else{
                    val cache = id.subSequence(6, id.length - 1).toString().toUInt()
                    cache < nextPersonID
                }
            }

            id.contains("ScheduledTask-") -> {
                return if (deletedScheduledTaskId.contains(id)){
                    false
                }else{
                    val cache = id.subSequence(13, id.length - 1).toString().toUInt()
                    cache < nextScheduledTaskID
                }

            }

            id.contains("Task-") -> {
                return if (deletedTaskId.contains(id)){
                    false
                }else{
                    val cache = id.subSequence(4, id.length -1).toString().toUInt()
                    cache < nextTaskID
                }

            }
            else -> return false
        }
    }

    fun deleteId(id: String){

        when{
            id.contains("Person-") -> {
                deletedPersonId.add(id)
            }
            id.contains("ScheduledTask-") -> {
                deletedScheduledTaskId.add(id)
            }
            id.contains("Task-") -> {
                deletedTaskId.add(id)
            }
        }
    }

    fun recreateId(id: String){
        when{
            id.contains("Person-") -> {
                deletedPersonId.remove(id)
            }
            id.contains("ScheduledTask-") -> {
                deletedScheduledTaskId.remove(id)
            }
            id.contains("Task-") -> {
                deletedTaskId.remove(id)
            }
        }
    }
}