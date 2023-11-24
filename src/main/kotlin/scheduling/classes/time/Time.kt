package scheduling.classes.time

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Deals with the time, that is needed for Scheduling the Tasks.csv
 */
class Time (
    time: String
): Comparable<Time> {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("kk:mm")
    private val time: LocalTime = LocalTime.parse(time, formatter)

    fun getTime(): LocalTime{
        return time
    }

    fun changeTime(hours: Long, minutes: Long){
        this.time.plusHours(hours)
        this.time.plusMinutes(minutes)
    }

    override fun compareTo(other: Time): Int {
        return if (other.time > this.time){
            -1
        }else if (this.time > other.time){
            1
        }else{
            0
        }
    }

    override fun toString(): String {
        return time.toString()
    }
}