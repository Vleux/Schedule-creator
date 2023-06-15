package classes

open class Dates(
    protected var firstDay: Date,
    protected var lastDay: Date
){

    init {
        this.check()
    }

    fun receiveFirstDay(): Date{
        return this.firstDay
    }

    fun receiveLastDay(): Date{
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

    // Ensuring, that the last and first Day are in the correct order

    protected open fun check(){
        if (firstDay > lastDay){
            val cache = firstDay
            firstDay = lastDay
            lastDay = cache
        }
    }

}