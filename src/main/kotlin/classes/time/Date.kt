package classes.time

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Date (
    date: String
) : Comparable<Date> {

    companion object {

        private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        /**
         * Casts a LocalDate to a Date
         */
        fun toDate(date: LocalDate): Date {
            return Date (
                formatter.format(date)
            )
        }
    }

    private var date: LocalDate = LocalDate.parse(date, formatter)

    /**
     * Returns the date
     */
    fun getDate(): LocalDate{
        return this.date
    }

    /**
     * Changes the Date ans saves the result
     */
    fun changeDate(days: Long){
        this.date = returnChangedDate(days)
    }

    /**
     * Changes the Date and retunrs the result as LocalDate
     */
    fun returnChangedDate(days: Long): LocalDate{
        return this.date.plusDays(days)
    }

    /**
     * Returns the number of Da3ys that have passed between this.date and the given Date
     */
    fun daysUntil(otherDate: Date): Long{
        return ChronoUnit.DAYS.between(otherDate.date, this.date)
    }

    override fun compareTo(other: Date): Int {
        return if (this.date > other.date){
            1
        }else if (this.date < other.date){
            -1
        }else{
            0
        }
    }

    // Override methods

    override fun toString(): String{
        return this.date.toString()
    }

    // Hashing the date-String (for the Map in a Task)
    override fun hashCode(): Int {
        return this.toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other is Date && other.date == this.date)
    }
}