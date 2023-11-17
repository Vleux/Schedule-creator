package classes.data

import classes.time.Date
import classes.time.WorkDays
import objects.IdKeeper
import objects.NationBackend
import java.time.LocalDate

class Person: Comparable<Person>{

    // Attributes

    private var _id: String
    private var _firstname: String
    private var _lastname: String
    private var _dateOfBirth: Date
    private var _nationality: String = ""
        set(value){
            field = NationBackend.parseNation(value)
        }
    private var _drivingLicense: Boolean
    private var _visit: WorkDays
    private var _freeFromDuty: Boolean
    private var myTasks: MutableMap<String, Date> = mutableMapOf()

    // Constructors

    public constructor(
        surname: String,
        lastname: String,
        dateOfBirth: Date,
        nation: String,
        drivingLicense: Boolean,
        visit: WorkDays,
        freeFromDuty: Boolean = false
    ){
        this._id = IdKeeper.getNextPersonId()

        this._firstname = surname
        this._lastname = lastname
        this._dateOfBirth = dateOfBirth
        this._nationality = nation
        this._drivingLicense = drivingLicense
        this._visit = visit
        this._freeFromDuty = freeFromDuty
    }

    // Masks and Getter

    val id: String
        get() = this._id
    var firstname: String
        get() = this._firstname
        set(name: String){
            if (name.length > 0){
                this._firstname = name
            }
        }
    var lastname: String
        get() = this._lastname
        set(newLastname: String){
            if (lastname.length > 0){
                this._lastname = newLastname
            }
        }
    var dateOfBirth: Date
        get() = this._dateOfBirth
        set(newDate: Date){
            val today = Date.toDate(LocalDate.now())
            if (newDate > today){
                this._dateOfBirth = newDate
            }
            this._dateOfBirth = dateOfBirth
        }
    val age: Int // in Years
        get() {
            val today = Date.toDate(LocalDate.now())
            return (this.dateOfBirth.daysUntil(today) / 365).toInt()
        }
    var nationality: String
        get() = this._nationality
        set(nationality){
            val nation = NationBackend.parseNation(nationality)
            if (nation != "invalid"){
                this._nationality = nationality
            }
        }
    var drivingLicense: Boolean
        get() = this._drivingLicense
        set(license){this._drivingLicense = license}
    var visit: WorkDays
        get() = this._visit
        set(newVisit){
            this._visit = newVisit
        }
    var freeFromDuty: Boolean
        get() = this._freeFromDuty
        set(free){
            this._freeFromDuty = free
        }

    // Overriding some functions

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

    override fun equals(other: Any?): Boolean{
        return if (other is Person){
            other.id == this.id
        }else{
            false
        }
    }
}