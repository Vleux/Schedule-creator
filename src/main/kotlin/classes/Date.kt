package classes

import java.time.LocalDate
import java.time.Period

class Date (
    date: String
) {

    var date: LocalDate = LocalDate.parse(date)

    public fun nextDay(){
        var dayPeriod = Period.ofDays(1)
        this.date = this.date.plus(dayPeriod)
    }

    public fun previousDay(){
        var dayPeriod = Period.ofDays(1)
        this.date = this.date.minus(dayPeriod)
    }


}