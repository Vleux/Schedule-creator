package scheduling.objects

import scheduling.classes.task.ScheduledTask
import scheduling.classes.time.Date
import scheduling.classes.time.Time

object Schedule {
    private val days: MutableMap<Date, ArrayList<String>> = mutableMapOf()
    private var ids: MutableMap<String, ScheduledTask> = mutableMapOf()

    private lateinit var firstDay: Date
    private lateinit var lastDay: Date

    init{
        lastDay()
        firstDay()
    }

    private fun lastDay(){
        for (date in days){
            if (lastDay < date.key){
                lastDay = date.key
            }
        }
    }

    private fun firstDay(){
        for (date in days){
            if (firstDay > date.key){
                firstDay = date.key
            }
        }
    }

    /**
     * Adds a "Day". If the ScheduledTasks does already exist (id is registered) they will be removed from the new Day
     */
    fun addDay(date: Date, tasks: ArrayList<ScheduledTask>): Boolean{
        if (days.contains(date)){
            return false
        }

        val newIds = emptyList<String>()

        for (task in tasks){
            if (ids.contains(task.id)){
                tasks.remove(task)
            }else{
                ids[task.id] = task
                newIds.addLast(task.id)
            }
        }

        days[date] = newIds.toCollection(ArrayList())

        firstDay()
        lastDay()

        return true
    }

    /**
     * Removes the Date from the Schedule.
     * The IDs of the Scheduled Tasks.csv are completely deleted (also in the IDKeeper)
     */
    fun deleteDay(date: Date){
        if (!days.keys.contains(date)){
            return
        }

        for (id in days[date]!!){
            ids.remove(id)
            IdKeeper.deleteId(id)
        }

        firstDay()
        lastDay()
    }

    /**
     * Returns the ids of the tasks in a day (if that day does exist)
     */
    fun getDay(date: Date): Array<String>?{
        return days[date]?.toTypedArray()
    }

    /**
     * Adds a Scheduled Task to a certain day. The Date is used to specify the date
     * If the date does not exist, it will be created
     * @return false if the id is already used
     */
    fun addScheduledTask(
        date: Date,
        parentTaskId: String,
        time: Pair<Time, Time>,
        takenPeople: Array<String>
    ): String {
        val newSTask = ScheduledTask(
            time,
            parentTaskId,
            takenPeople
        )
        ids[newSTask.id] = newSTask

        if (days[date] == null){
            days[date] = arrayListOf(ids[newSTask.id]!!.id)
        }else{
            days[date]!!.add(ids[newSTask.id]!!.id)
        }

        return newSTask.id
    }

    fun removeScheduledTask(date: Date, task: ScheduledTask): Boolean{
        if (days[date] == null || ids.contains(task.id)){
            return false
        }

        days[date]!!.remove(task.id)
        ids.remove(task.id)
        return true
    }

    /**
     * If the id does not already exist, null is returned.
     * If the ScheduledTask is not found, null will be returned too.
     */
    fun getScheduledTask(taskId: String): ScheduledTask?{
        if (!ids.contains(taskId)){
            return null
        }

        return ids[taskId]
    }

    /**
     * Checks if a task does exist
     */
    fun doesScheduledTaskExist(taskId: String): Boolean{
        return ids.contains(taskId)
    }

    /**
     * Returns the scheduled Tasks.csv of an abstract task.
     * Returns a Map of <ID, Date>
     */
    fun getScheduledTasksOf(abstractTaskId: String): Map<String, Date>{
        val parentTask = Tasks.getTask(abstractTaskId) ?: throw Error("Could not find abstract task")

        val result = mutableMapOf<String, Date>()
        for (day in days){
            println(day)
            for (id in day.value){
                if (parentTask.isChild(id)){
                    result[id] = day.key
                }
            }
        }
        return result
    }

    /**
     * Returns the date of a scheduled task
     */
    fun getDateOfScheduledTask(scheduledTaskId: String): Date?{
        for (date in days.keys){
            if (days[date]!!.contains(scheduledTaskId)){
                return date
            }
        }
        return null
    }

    /**
     * Returns all ids of all the scheduled Tasks.csv
     */
    fun getAllTasks(): Array<String>{
        return ids.keys.toTypedArray()
    }
}