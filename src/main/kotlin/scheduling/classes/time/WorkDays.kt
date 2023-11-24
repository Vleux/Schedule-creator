package scheduling.classes.time

class WorkDays(
    arrival: Date,
    timeOfArrival: Time,
    leave: Date,
    timeOfLeave: Time
): Dates(arrival, leave) {

    private var freeDays: MutableList<Date> = mutableListOf()
    var timeOfArrival: Time
    var timeOfLeave: Time

    init {
        this.timeOfArrival = timeOfArrival
        this.timeOfLeave = timeOfLeave
    }

    // Manages Free Days (Adding, deleting, getting)

    fun addFreeDay(day: Date){
        if (day <= super.lastDay && day >= super.firstDay){
            freeDays.add(day)
        }
    }

    fun removeFreeDay(day: Date){
        this.freeDays.remove(day)
    }

    fun getFreeDays(): Array<Date>{
        return this.freeDays.toTypedArray()
    }

    /**
     * Returns a List of the days the person is actually working
     */
    fun getWorkDays(): Array<Date>{
        val workDays: MutableList<Date> = mutableListOf()

        val day = this.firstDay.copy()
        val last = this.lastDay.copy()
        while (day <= this.lastDay){
            if (!this.freeDays.contains(day)){
                workDays.add(day)
            }
            day.changeDate(1)
        }

        return workDays.toTypedArray()
    }

    /**
     * Returns only the Time-period during that a Person is working.
     * FreeDays (except for arrival and leave) are ignored
     */
    fun getWorkPeriod(): Dates?{
        return if (this.firstDay.returnChangedDate(1) == this.lastDay.getDate()){
           null
        }else {
            Dates(
                this.firstDay,
                this.lastDay
            )
        }
    }

}