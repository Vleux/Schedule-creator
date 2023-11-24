package scheduling.objects

import scheduling.classes.data.Person
import scheduling.classes.time.Date
import scheduling.classes.time.WorkDays

object People {
    private var people: MutableMap<String, Person> = mutableMapOf()

    /**
     * Adds a new Person to the people-map, if it does not exist already.
     * Returns true on success, false if the person does already exist.
     */
    fun addPerson(person: Person): Boolean{
        if (people[person.id] == null){
            people[person.id] = person
            return true
        }
        return false
    }

    fun getAllPeopleIDs(): Array<String>{
        return people.keys.toTypedArray()
    }

    fun getPersonById(id: String): Person?{
        return people[id]
    }

    fun getNumberOfPeople(): Int{
        return people.size
    }

    /**
     * Removes a person from the people-map if it does exist.
     */
    fun removePerson(personId: String){
        people.remove(personId)
    }

    fun doesPersonExist(personId: String): Boolean{
        return people[personId] != null
    }

    /**Change a Persons properties **/

    fun changePersonFirstname(id: String, newFirstname: String){
        people[id]?.firstname = newFirstname
    }

    fun changePersonLastname(id: String, newLastname: String){
        people[id]?.lastname = newLastname
    }

    fun changeDateOfBirth(id: String, newDateOfBirth: Date){
        people[id]?.dateOfBirth = newDateOfBirth
    }

    fun changeNationality(id: String, newNation: String){
        people[id]?.nationality = newNation
    }

    fun changeDrivingLicense(id: String, drivingLicense: Boolean){
        people[id]?.drivingLicense = drivingLicense
    }

    fun changeVisit(id:String, newVisit: WorkDays){
        people[id]?.visit = newVisit
    }

    fun changeFreeFromDuty(id: String, newFreeFromDuty: Boolean){
        people[id]?.freeFromDuty = newFreeFromDuty
    }

    fun addTask(personId: String, taskId: String, date: Date){
        people[personId]?.addTask(taskId, date)
    }

    fun removeTask(personId: String, taskId: String){
        people[personId]?.removeTask(taskId)
    }

}