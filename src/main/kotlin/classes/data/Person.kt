package classes.data

import classes.time.Date
import classes.time.WorkDays
import objects.IdKeeper
import objects.NationBackend
import java.time.LocalDate

class Person: Comparable<Person>{

    // Attributes

    private var _id: String
    private var _surname: String
    private var _lastname: String
    private var _dateOfBirth: Date
    private var _nationality: String
        set(value){
            field = NationBackend.parseNation(value)
        }
    private var _drivingLicense: Boolean
    private var _visit: WorkDays
    private var _freeFromDuty: Boolean

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

        this._surname = surname
        this._lastname = lastname
        this._dateOfBirth = dateOfBirth
        this._nationality = nation
        this._drivingLicense = drivingLicense
        this._visit = visit
        this._freeFromDuty = freeFromDuty
    }

    private constructor(
        id: String,
        surname: String,
        lastname: String,
        dateOfBirth: Date,
        nation: String,
        drivingLicense: Boolean,
        visit: WorkDays,
        freeFromDuty: Boolean
    ){
        this._id = id

        this._surname = surname
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
    val surname: String
        get() = this._surname
    val lastname: String
        get() = this._lastname
    val dateOfBirth: Date
        get() = this._dateOfBirth
    val age: Int // in Years
        get() {
            val today = Date.toDate(LocalDate.now())
            return (this.dateOfBirth.daysUntil(today) / 365).toInt()
        }
    val nationality: String
        get() = this._nationality
    val drivingLicense: Boolean
        get() = this._drivingLicense
    val visit: WorkDays
        get() = this._visit
    val freeFromDuty: Boolean
        get() = this._freeFromDuty

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

    // Functions to change values and return a new Person

    fun newSurname(newSurname: String): Person {
        return Person(
            this._id,
            newSurname,
            this.lastname,
            this.dateOfBirth,
            this.nationality,
            this.drivingLicense,
            this.visit,
            this.freeFromDuty
        )
    }

    fun newLastname(newLastname: String): Person {
        return Person(
            this._id,
            this.surname,
            newLastname,
            this.dateOfBirth,
            this.nationality,
            this.drivingLicense,
            this.visit,
            this.freeFromDuty
        )
    }

    fun newDateOfBirth(newDateOfBirth: Date): Person {
        return Person(
            this._id,
            this.surname,
            this.lastname,
            newDateOfBirth,
            this.nationality,
            this.drivingLicense,
            this.visit,
            this.freeFromDuty
        )
    }

    fun newNationality(newNationality: String): Person {
        return Person(
            this._id,
            this.surname,
            this.lastname,
            this.dateOfBirth,
            newNationality,
            this.drivingLicense,
            this.visit,
            this.freeFromDuty
        )
    }

    fun newDrivingLicense(newDrivingLicense: Boolean): Person {
        return Person(
            this._id,
            this.surname,
            this.lastname,
            this.dateOfBirth,
            this.nationality,
            newDrivingLicense,
            this.visit,
            this.freeFromDuty
        )
    }

    fun newVisit(newVisit: WorkDays): Person {
        return Person(
            this._id,
            this.surname,
            this.lastname,
            this.dateOfBirth,
            this.nationality,
            this.drivingLicense,
            newVisit,
            this.freeFromDuty
        )
    }

    fun newFreeFromDuty(newFreeFromDuty: Boolean): Person {
        return Person(
            this._id,
            this.surname,
            this.lastname,
            this.dateOfBirth,
            this.nationality,
            this.drivingLicense,
            this.visit,
            newFreeFromDuty
        )
    }
}