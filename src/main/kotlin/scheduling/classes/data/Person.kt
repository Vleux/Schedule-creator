package scheduling.classes.data

import scheduling.classes.time.Date
import scheduling.classes.time.WorkDays
import scheduling.objects.IdKeeper
import scheduling.objects.NationBackend
import java.time.LocalDate

class Person: Comparable<Person>{

    // Attributes

    private var _id: String = IdKeeper.getNextPersonId()
    private var _firstname: String
    private var _lastname: String
    private var _dateOfBirth: Date
    private var _nationality: String = ""
        set(value){
            val nation = NationBackend.parseNation(value)
            if (nation != null){
                field = nation
            }
        }
    private var _drivingLicense: Boolean
    private var _visit: WorkDays
    private var _freeFromDuty: Boolean
    private var _myTasks: MutableMap<String, Date> = mutableMapOf()
    var timePercentage: Double = -1.0       // Saves the relative amount of time the person is present

    public constructor(
        firstame: String,
        lastname: String,
        dateOfBirth: Date,
        nation: String,
        drivingLicense: Boolean,
        visit: WorkDays,
        freeFromDuty: Boolean = false
    ){

        this._firstname = firstname
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

    val myTasks: MutableMap<String, Date>
        get() = this._myTasks

    fun addTask(taskId: String, date: Date){
        if (_myTasks[taskId] == null){
            this._myTasks[taskId] = date
        }
    }

    fun removeTask(taskId: String){
        if (_myTasks[taskId] != null){
            _myTasks.remove(taskId)
        }
    }

    fun getTasksWithDate(): MutableMap<String, Date>{return this._myTasks}

    fun getJustTaskIDs(): Array<String>{
        return this._myTasks.keys.toTypedArray()
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

    override fun hashCode(): Int {
        return _id.hashCode()
    }
}