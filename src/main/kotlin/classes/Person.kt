package classes

import objects.IdKeeper
import objects.Nation

data class Person(
    // Personal Data
    var surname: String,
    var lastname: String,
    var age: Int,
    var nationality: String,

    var drivingLicense: Boolean,
    var visit: VisitDate,
    var freeFromDuty: Boolean = false,
    var counter: Int = 0,

): Comparable<Person>{

    val ID: String = IdKeeper.getNextPersonId()

    /**
     * Ensures that the Nationality is one of the existing variants
     */
    init {
        this.nationality = Nation.parseNation(this.nationality)
    }

    /**
     * Compares two Persons with their ID's and nothing else. This will be used for sorting,
     * not for deleting double-entries!!
     */
    override fun compareTo(other: Person): Int {
        if (other.ID == this.ID){
            return 0
        }else{
            var otherID: Int = other.ID.substring(7).toInt()
            var thisID: Int = this.ID.substring(7).toInt()

            if (otherID > thisID){
                return -1
            }else{
                return 1
            }
        }
    }
}