package objects

import classes.data.Person
import classes.time.Date
import classes.time.WorkDays

object People {
    private var people: MutableMap<String, Person> = mutableMapOf()

    /**
     * Adds a new Person to the people-map, if it does not exist already.
     * Returns true on success, false if the person does already exist.
     */
    fun addPerson(person: Person): Boolean{
        if (this.people[person.id] == null){
            this.people[person.id] = person
            return true
        }
        return false
    }

    fun getAllPeopleIDs(): Array<String>{
        return this.people.keys.toTypedArray()
    }

    fun getPersonById(id: String): Person?{
        return this.people[id]
    }

    fun getNumberOfPeople(): Int{
        return this.people.size
    }

    /**
     * Removes a person from the people-map if it does exist.
     */
    fun removePerson(personId: String){
        this.people.remove(personId)
    }

    fun doesPersonExist(personId: String): Boolean{
        return this.people[personId] != null
    }

    /**Change a Persons properties **/

    fun changePersonFirstname(id: String, newFirstname: String){
        this.people[id]?.firstname = newFirstname
    }

    fun changePersonLastname(id: String, newLastname: String){
        this.people[id]?.lastname = newLastname
    }

    fun changeDateOfBirth(id: String, newDateOfBirth: Date){
        this.people[id]?.dateOfBirth = newDateOfBirth
    }

    fun changeNationality(id: String, newNation: String){
        this.people[id]?.nationality = newNation
    }

    fun changeDrivingLicense(id: String, drivingLicense: Boolean){
        this.people[id]?.drivingLicense = drivingLicense
    }

    fun changeVisit(id:String, newVisit: WorkDays){
        this.people[id]?.visit = newVisit
    }

    fun changeFreeFromDuty(id: String, newFreeFromDuty: Boolean){
        this.people[id]?.freeFromDuty = newFreeFromDuty
    }

    fun addTask(personId: String, taskId: String, date: Date){
        this.people[personId]?.addTask(taskId, date)
    }

    fun removeTask(personId: String, taskId: String){
        this.people[personId]?.removeTask(taskId)
    }

}