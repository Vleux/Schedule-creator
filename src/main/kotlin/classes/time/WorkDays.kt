package classes.time

class WorkDays(
    arrival: Date,
    leave: Date
): Dates(arrival, leave) {

    init {
        this.changeFirstDay(1)
        this.changeLastDay(-1)
    }

    // Manages Free Days (Adding, deleting, getting)

    private var freeDays: MutableList<Date> = mutableListOf()

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
     * Returns a List of the days the person is acutally working
     */
    fun getWorkDays(): Array<Date>{
        val workDays: MutableList<Date> = mutableListOf()

        val day = this.firstDay
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
                Date.toDate(this.firstDay.returnChangedDate(1)),
                Date.toDate(this.lastDay.returnChangedDate(-1))
            )
        }
    }

}