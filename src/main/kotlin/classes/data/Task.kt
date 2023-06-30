package classes.data

import classes.time.Time
import classes.time.Date
import objects.IdKeeper

class Task: Comparable<Task>{

    // Attributes

    private var _id: String

    private var _name: String
    private var _numberOfPeople: Int
    private var _dateTime: Map<Date, Array<Time>>
    private var _incompatibleTasks: Array<String>
    private var _driver: Boolean
    // Constructors

    public constructor(
        name: String,
        numberOfPeople: Int,
        dateTime: Map<Date, Array<Time>>,
        incompatibleTasks: Array<String> = emptyArray(),
        driver: Boolean = false
    ){
        this._name = name
        this._numberOfPeople = numberOfPeople
        this._dateTime = dateTime
        this._incompatibleTasks = incompatibleTasks
        this._driver = driver

        this._id = IdKeeper.getNextTaskId()

    }

    /**
     * This constructor is needed to create a Task that does have the same ID
     * as the previous one. Needed to change a Task in the "Tasks" object.
     */
    private constructor(
        id: String,
        name: String,
        numberOfPeople: Int,
        dateTime: Map<Date, Array<Time>>,
        incompatibleTasks: Array<String>,
        driver: Boolean
    ){
        this._name = name
        this._numberOfPeople = numberOfPeople
        this._dateTime = dateTime
        this._incompatibleTasks = incompatibleTasks
        this._driver = driver


        this._id = id
    }

    // Masks and getters

    val name: String
        get() = this._name
    val numberOfPeople: Int
        get() = this._numberOfPeople
    val dateTime: Map<Date, Array<Time>>
        get() = this._dateTime
    val incompatibleTasks: Array<String>
        get() = this._incompatibleTasks
    val driver: Boolean
        get() = this._driver
    val id: String
        get() = this._id

    // overriding some functions

    override fun equals(other: Any?): Boolean {
        return if (other is Task){
            other.id == this.id
        }else{
            false
        }
    }

    override fun compareTo(other: Task): Int{
        if (other.id == this.id){
            return 0
        }

        val otherID = other.id.substring(5).toInt()
        val thisID = this.id.substring(5).toInt()

        return if (otherID > thisID){
            -1
        }else{
            1
        }
    }

    fun getTimeOn(date: Date): Array<Time>{
        return if (this.dateTime[date] != null) {
            this.dateTime[date]!!
        }
        else{
            emptyArray()
        }
    }

    // Setter, that return instead of change

    fun newName(newName: String): Task{
        return Task(
            this._id,
            newName,
            this.numberOfPeople,
            this.dateTime,
            this.incompatibleTasks,
            this.driver)
    }

    fun newNumberOfPeople(newNumber: Int): Task{
        return Task(
            this._id,
            this.name,
            newNumber,
            this.dateTime,
            this.incompatibleTasks,
            this.driver
        )
    }

    fun newDateTime(newDateTime: Map<Date, Array<Time>>): Task{
        return Task(
            this._id,
            this.name,
            this.numberOfPeople,
            newDateTime,
            this.incompatibleTasks,
            this.driver
        )
    }

    fun newIncompatibleTasks(newIncompatibleTasks: Array<String>): Task {
        return Task(
            this._id,
            this.name,
            this.numberOfPeople,
            this.dateTime,
            newIncompatibleTasks,
            this.driver
        )
    }

    fun newDriver(newDriver: Boolean): Task{
        return Task(
            this._id,
            this.name,
            this.numberOfPeople,
            this.dateTime,
            this.incompatibleTasks,
            newDriver
        )
    }
}