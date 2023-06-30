package objects

import classes.data.ScheduledTask
import classes.time.Date

object Schedule {
    private val days: MutableMap<Date, ArrayList<ScheduledTask>> = mutableMapOf()
    private var ids: ArrayList<String> = arrayListOf()

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
     * Adds a "Day". If the ScheduledTasks do already exist (Id is registered) they will be removed
     */
    fun addDay(date: Date, tasks: ArrayList<ScheduledTask>): Boolean{
        if (this.days.contains(date)){
            return false
        }

        for (task in tasks){
            if (ids.contains(task.id)){
                tasks.remove(task)
            }else{
                ids.add(task.id)
            }
        }

        this.days[date] = tasks

        if (date < this.firstDay) {
            this.firstDay = date
        }else if (date > this.lastDay){
            this.lastDay = date
        }

        return true
    }

    /**
     * Removes the Date from the Schedule.
     * The IDs of the Scheduled Tasks are not remembered anymore
     */
    fun deleteDay(date: Date){
        if (!this.days.keys.contains(date)){
            return
        }

        for (task in this.days[date]!!){
            this.ids.remove(task.id)
            IdKeeper.deleteId(task.id)
        }

        if (this.firstDay == date){
            this.firstDay()
        }else if (this.lastDay == date){
            this.lastDay()
        }
    }

    fun getDay(date: Date): ArrayList<ScheduledTask>?{
        return this.days[date]
    }

    /**
     * Adds a Scheduled Task to a certain day. The Date is used to specify the date
     * @return false if the date does not exist or the id is already used
     */
    fun addScheduledTask(date: Date, task: ScheduledTask): Boolean{
        if (this.days[date] == null || this.ids.contains(task.id)){
            return false
        }

        this.days[date]!!.add(task)
        this.ids.add(task.id)
        return true
    }

    fun removeScheduledTask(date: Date, task: ScheduledTask): Boolean{
        if (this.days[date] == null || this.ids.contains(task.id)){
            return false
        }

        this.days[date]!!.remove(task)
        this.ids.remove(task.id)
        return true
    }

    /**
     * Quickest Method to find a Scheduled Task. Only the specified Date will be searched
     * If the id or date does not exist, null is returned
     * If the ScheduledTask is not found on the given Date, null is returned
     */
    fun getScheduledTask(date: Date, taskId: String): ScheduledTask?{
        if (this.days[date] == null || !(this.ids.contains(taskId))){
            return null
        }

        for (task in this.days[date]!!){
            if (task.id == taskId){
                return task
            }
        }

        return null
    }

    /**
     * Slower method to find a Scheduled Task. Searches all the Dates until the Scheduled Task is find or none are left.
     * If the id does not already exist, null is returned.
     * If the ScheduledTask is not found, null will be returned too.
     * @throws Ids unsorted. If that happens, I fucked up.
     */
    fun getScheduledTask(taskId: String): ScheduledTask?{
        if (!this.ids.contains(taskId)){
            return null
        }

        for (day in this.days){
            for (task in day.value){
                if (task.id == taskId){
                    return task
                }
            }
        }

        this.ids.remove(taskId)
        return null
    }

}