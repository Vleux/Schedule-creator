package objects

import classes.data.ScheduledTask
import classes.time.Date

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
        for (date in this.days){
            if (lastDay < date.key){
                this.lastDay = date.key
            }
        }
    }

    private fun firstDay(){
        for (date in this.days){
            if (firstDay > date.key){
                firstDay = date.key
            }
        }
    }

    /**
     * Adds a "Day". If the ScheduledTasks does already exist (id is registered) they will be removed from the new Day
     */
    fun addDay(date: Date, tasks: ArrayList<ScheduledTask>): Boolean{
        if (this.days.contains(date)){
            return false
        }

        val newIds = emptyList<String>()

        for (task in tasks){
            if (ids.contains(task.id)){
                tasks.remove(task)
            }else{
                this.ids[task.id] = task
                newIds.addLast(task.id)
            }
        }

        this.days[date] = newIds.toCollection(ArrayList())

        this.firstDay()
        this.lastDay()

        return true
    }

    /**
     * Removes the Date from the Schedule.
     * The IDs of the Scheduled Tasks are completely deleted (also in the IDKeeper)
     */
    fun deleteDay(date: Date){
        if (!this.days.keys.contains(date)){
            return
        }

        for (id in this.days[date]!!){
            this.ids.remove(id)
            IdKeeper.deleteId(id)
        }

        firstDay()
        lastDay()
    }

    /**
     * Returns the ids of the tasks in a day (if that day does exist)
     */
    fun getDay(date: Date): Array<String>?{
        return this.days[date]?.toTypedArray()
    }

    /**
     * Adds a Scheduled Task to a certain day. The Date is used to specify the date
     * If the date does not exist, it will be created
     * @return false if the id is already used
     */
    fun addScheduledTask(date: Date, task: ScheduledTask): Boolean{
        if (ids.contains(task.id)){
            return false
        }
        if (this.days[date] == null){
            this.days[date] = arrayListOf(task.id)
        }else{
            this.days[date]!!.add(task.id)
        }

        this.ids[task.id] = task
        return true
    }

    fun removeScheduledTask(date: Date, task: ScheduledTask): Boolean{
        if (this.days[date] == null || this.ids.contains(task.id)){
            return false
        }

        this.days[date]!!.remove(task.id)
        this.ids.remove(task.id)
        return true
    }

    /**
     * If the id does not already exist, null is returned.
     * If the ScheduledTask is not found, null will be returned too.
     */
    fun getScheduledTask(taskId: String): ScheduledTask?{
        if (!this.ids.contains(taskId)){
            return null
        }

        return this.ids[taskId]
    }

    /**
     * Checks if a task does exist
     */
    fun doesScheduledTaskExist(taskId: String): Boolean{
        return this.ids.contains(taskId)
    }

}