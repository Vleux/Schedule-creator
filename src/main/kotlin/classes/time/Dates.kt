package classes.time

open class Dates(
    @JvmField protected var firstDay: Date,
    @JvmField protected var lastDay: Date
){

    init {
        this.check()
    }

    fun getFirstDay(): Date {
        return this.firstDay
    }

    fun getLastDay(): Date {
        return this.lastDay
    }

    // Changing the First Day

    fun changeFirstDay(days: Long){
        this.firstDay.changeDate(days)
        this.check()
    }

    fun startOn(day: Date){
        this.firstDay = day
        this.check()
    }

    // Changing the LastDay

    fun changeLastDay(day: Long){
        this.lastDay.changeDate(day)
        this.check()
    }

    fun endOn(day: Date){
        this.lastDay = day
        this.check()
    }

    /**
     * Returns all the Dates in the Date-Period
     */
    fun getDateArray(): Array<Date>{
        val cache = mutableListOf<Date>()
        val currentDay = this.firstDay
        while (currentDay <= this.lastDay){
            cache.add(currentDay)
            currentDay.changeDate(1)
        }
        return cache.toTypedArray()
    }

    // Ensuring, that the last and first Day are in the correct order

    protected open fun check(){
        if (firstDay > lastDay){
            val cache = firstDay
            firstDay = lastDay
            lastDay = cache
        }
    }

}