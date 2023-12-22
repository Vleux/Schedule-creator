package scheduling.classes.task

import scheduling.classes.time.Time
import scheduling.objects.IdKeeper
import scheduling.objects.People
import scheduling.objects.Tasks

/**
 * DO NOT USE THE OPTION TO GIVE THE ID YOURSELF IF POSSIBLE
 */
class ScheduledTask(
    var time: Pair<Time, Time>,
    parentTask: String,
    takenPeople: Array<String>
) : Comparable<ScheduledTask>{

    private var _id: String
    private var _parentTask: String = parentTask
    private var _takenPeople: Array<String>

    // Getter

    val id: String
        get() = this._id
    val takenPeople: Array<String>
        get() = this._takenPeople
    val parentTask: String
        get() = this._parentTask

    init {
        if (!Tasks.doesTaskExist(parentTask)){
            this._id = ""
            this._parentTask = ""
            this._takenPeople = emptyArray()
            this.time = Pair(Time("00:00"), Time("00:00"))
        }else{
            this._id = IdKeeper.getNextScheduledTaskId()
            val cachedPeopleList = mutableListOf<String>()
            for (personId in takenPeople){
                if(People.doesPersonExist(personId)){
                    cachedPeopleList.add(personId)
                }
            }
            this._takenPeople = cachedPeopleList.toTypedArray()
        }

    }

    // Changing

    fun addPerson(personId: String): Boolean{
        if (!People.doesPersonExist(personId) ||
            this.takenPeople.contains(personId) ||
            this.takenPeople.size >= Tasks.getTask(parentTask)!!.numberOfPeople){
            return false
        }
        this._takenPeople = this._takenPeople.plus(personId)
        return true
    }

    fun removePerson(personId: String): Boolean{
        if (!this.takenPeople.contains(personId)){
            return true
        }

        this._takenPeople = this._takenPeople.filter{
            it != personId
        }.toTypedArray()

        return !this._takenPeople.contains(personId)
    }


    /**
     * Returns the number of people, that are still needed.
     * -1 if the parentTask does not exist any longer
     */
    fun peopleNeeded(): Int{
        val task = Tasks.getTask(this._parentTask) ?: return -1

        return task.numberOfPeople - this.takenPeople.size
    }

    override fun hashCode(): Int {
        var result = _id.hashCode()
        result = 31 * result + _parentTask.hashCode()
        return result
    }

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
}