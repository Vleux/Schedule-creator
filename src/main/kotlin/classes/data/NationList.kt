package classes.data

import objects.NationBackend
import objects.People

class NationList {
    private var nations: MutableMap<String, PersonList> = mutableMapOf()

    fun getNation(nation: String): PersonList?{
        return nations[
            NationBackend.parseNation(nation = nation)
        ]
    }

    fun getAllPeople(): Array<String>{
        val cache = nations.keys
        val people = arrayListOf<String>()
        for (key in cache){
            people.addAll(nations[key]!!.getPeople())
        }
        return people.toTypedArray()
    }

    fun addPerson(nation: String, id: String): Boolean{
        val lNation = NationBackend.parseNation(nation) ?: return false
        val lId = People.doesPersonExist(id)
        if (!lId){return false}

        return this.nations[lNation]!!.addPerson(id)
    }

    fun removePerson(nation: String, id: String){
        val lNation = NationBackend.parseNation(nation) ?: return
        this.nations[lNation]!!.removePerson(id)
    }

    /**
     * Returns an array: Array<Pair<NATION, Amount of People>>
     */
    fun getNationRatio(): Array<Pair<String, Int>>{
        val keys = nations.keys
        var result = emptyArray<Pair<String, Int>>()
        for (key in keys){
            result = result.plus(Pair(key, nations[key]!!.getPeople().size))
        }
        return result
    }
}