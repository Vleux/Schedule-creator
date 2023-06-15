package classes

import objects.IdKeeper
import objects.NationBackend

data class Person(
    // Personal Data
    var surname: String,
    var lastname: String,
    var dateOfBirth: Date,
    var nationality: String,

    var drivingLicense: Boolean,
    var visit: WorkDays,
    var freeFromDuty: Boolean = false,
    var counter: Int = 0,

    ): Comparable<Person>{

    val id: String = IdKeeper.getNextPersonId()

    /**
     * Ensures that the Nationality is one of the existing variants
     */
    init {
        this.nationality = NationBackend.parseNation(this.nationality)
    }

    /**
     * Compares two Persons with their ID's and nothing else. This will be used for sorting,
     * not for deleting double-entries!!
     */
    override fun compareTo(other: Person): Int {
        return if (other.id == this.id){
            0
        }else{
            val otherID: Int = other.id.substring(7).toInt()
            val thisID: Int = this.id.substring(7).toInt()

            if (otherID > thisID){
                -1
            }else{
                1
            }
        }
    }

    /**
     * Returns the age of the Person (only the Years, Days are ignored)
     */
    fun age(day: Date): Int{
        return (this.dateOfBirth.daysUntil(day) / 365).toInt()
    }
}