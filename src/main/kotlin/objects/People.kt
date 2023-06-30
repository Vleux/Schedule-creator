package objects

import classes.data.Person

object People {
    private var people: MutableList<Person> = mutableListOf()

    /**
     * Adds a new Person to the people-list, if it is not already added.
     * The list will be resorted.
     * Returns true on success, false if the person does already exist
     */
    fun addPerson(person: Person): Boolean{
        return if (!this.people.contains(person)){
            this.people.add(person)
            this.people.sort()
            true
        }else{
            false
        }
    }

    /**
     * Deletes a Person from the list, using their id.
     * @return true -> Person deleted
     * @return false -> Person was not found.
     */
    fun removePerson(personId: String): Boolean{
        for (person in this.people){
            if (person.id == personId){
                this.people.remove(person)
                IdKeeper.deleteId(person.id)
                return true
            }
        }
        return false
    }

    /**
     * Returns the Person with the given ID.
     * If there is no Person with such an ID, null will be returned
     */
    fun getPerson(personId: String): Person?{
        for (person in this.people){
            if (person.id == personId){
                return person
            }
        }
        return null
    }

    fun changePerson(changedPerson: Person): Boolean{
        return if (doesPersonExist(changedPerson.id)){
            this.removePerson(changedPerson.id)
            this.addPerson(changedPerson)
            true
        }else{
            false
        }
    }

    fun doesPersonExist(personId: String): Boolean{
        for (person in this.people){
            if (person.id == personId){
                return true
            }
        }
        return false
    }
}