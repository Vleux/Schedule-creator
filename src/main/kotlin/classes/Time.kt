package classes

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Deals with the time, that is needed for Scheduling the Tasks
 */
class Time (
    time: String
) {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm")
    private val time: LocalTime = LocalTime.parse(time, formatter)

    fun getTime(): LocalTime{
        return time
    }

    fun changeTime(hours: Long, minutes: Long){
        this.time.plusHours(hours)
        this.time.plusMinutes(minutes)
    }
}