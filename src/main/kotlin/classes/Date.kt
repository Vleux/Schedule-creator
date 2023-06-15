package classes

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Date (
    date: String
) : Comparable<Date> {

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
     * Returns the number of Days that have passed between this.date and the given Date
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

    companion object {

        private val formatter = DateTimeFormatter.ofPattern("dd-mm-yyyy")

        /**
         * Casts a LocalDate to a Date
         */
        fun toDate(date: LocalDate): Date{
            return Date (
                this.formatter.format(date)
            )
        }
    }
}