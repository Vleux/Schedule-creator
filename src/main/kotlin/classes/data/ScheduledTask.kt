package classes.data

import classes.time.Time
import objects.IdKeeper
import objects.People
import objects.Tasks

class ScheduledTask: Comparable<ScheduledTask>{

    private var _id: String
    private var _parentTask: String
    private var _takenPeople: Array<String>
    private var _time: Time

    public constructor(
        time: Time,
        parentTask: String,
        takenPeople: Array<String>
    ){
        if (!Tasks.doesTaskExist(parentTask)){
            this._id = ""
            this._parentTask = ""
            this._takenPeople = emptyArray()
            this._time = Time("00:00")
            return
        }

        this._id = IdKeeper.getNextScheduledTaskId()
        this._parentTask = parentTask
        this._time = time

        val cachedPeopleList = mutableListOf<String>()
        for (personId in takenPeople){
            if(People.doesPersonExist(personId)){
                cachedPeopleList.add(personId)
            }
        }

        this._takenPeople = cachedPeopleList.toTypedArray()
    }

    private constructor(
        id: String,
        time: Time,
        parentTask: String,
        takenPeople: Array<String>
    ){
        this._id = id
        this._parentTask = parentTask
        this._takenPeople = takenPeople
        this._time = time
    }

    // Getter

    val id: String
        get() = this._id
    val takenPeople: Array<String>
        get() = this._takenPeople
    val parentTask: String
        get() = this._parentTask
    val time: Time
        get() = this._time

    // Overriding some Methods

    override fun equals(other: Any?): Boolean {
        return if (other is ScheduledTask){
            other.id == this.id
        }else{
            false
        }
    }

    override fun compareTo(other: ScheduledTask): Int {
        return if (other.id == this.id){
            0
        }else{
            val otherID: Int = other.id.substring(14).toInt()
            val thisID: Int = this.id.substring(14).toInt()

            if (otherID > thisID){
                -1
            }else{
                1
            }
        }
    }

    // Creating changed object

    /**
     * Adds a Persons Id to the Scheduled Task that will be returned
     * @return true if the Person is successfully added (does not succeed, if the Person does not exist)
     */
    fun addPerson(personId: String): Pair<Boolean, ScheduledTask>{
        return if (People.doesPersonExist(personId)){
            Pair(
                true,
                ScheduledTask(
                    this.id,
                    this.time,
                    this.parentTask,
                    this.takenPeople + personId
                )
            )
        }else{
            Pair(
                false,
                this
            )
        }
    }

    /**
     * Removes a persons Id from the ScheduledTask that will be returned
     * @return false is only returned, if the personId is still in the takenPeople Array after modification
     */
    fun removePerson(personId: String): Pair<Boolean, ScheduledTask>{
        if (!this.takenPeople.contains(personId)){
            return Pair(true, this)
        }

        val cachedTakenPerson = this._takenPeople.filter{
            it != personId
        }

        return if (cachedTakenPerson.contains(personId)){
            Pair(false, this)
        }else{
            Pair(
                true,
                ScheduledTask(
                    this._id,
                    this._time,
                    this._parentTask,
                    cachedTakenPerson.toTypedArray()
                )
            )
        }

    }

    /**
     * Rewriting the Time, returning a new Object with the changed Time
     */
    fun changeTime(newTime: Time): ScheduledTask{
        return ScheduledTask(
            this._id,
            newTime,
            this._parentTask,
            this._takenPeople
        )
    }

    /**
     * Returns the number of people, that are still needed.
     * -1 if the parentTask does not exist any longer
     */
    fun numberOfPeopleStillNeeded(): Int{
        val task = Tasks.getTask(this._parentTask) ?: return -1

        return task.numberOfPeople - this.takenPeople.size
    }
}