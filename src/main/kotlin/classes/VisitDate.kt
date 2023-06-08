package classes

class VisitDate(
    arrival: Date,
    leave: Date
) {

    private var firstWorkDay: Date
    private var lastWorkDay: Date
    
    init {
        arrival.nextDay()
        leave.previousDay()
        this.firstWorkDay = arrival
        this.lastWorkDay = leave
    }

    fun getFirstWorkDay(): Date {
        return this.firstWorkDay
    }

    fun getLastWorkDay(): Date{
        return this.lastWorkDay
    }
}