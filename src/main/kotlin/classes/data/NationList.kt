package classes.data

import objects.NationBackend
import objects.People

class NationList {
    private var nations: MutableMap<String, ArrayList<String>> = mutableMapOf()
    fun getNation(nation: String): Array<String>?{
        return nations[
            NationBackend.parseNation(nation = nation)
        ]?.toTypedArray()
    }

    fun getAllPeople():Array<String>?{
        val cache = nations.keys
        val people = arrayListOf<String>()
        for (key in cache){
            people.addAll(nations[key]!!)
        }
        return people.toTypedArray()
    }

    fun addPerson(nation: String, id: String): Boolean{
        val lNation = NationBackend.parseNation(nation) ?: return false
        val lId = People.doesPersonExist(id)
        if (!lId){return false}

        return this.nations[lNation]!!.add(id)
    }

    fun removePerson(nation: String, id: String): Boolean{
        val lNation = NationBackend.parseNation(nation) ?: return false
        return this.nations[lNation]!!.remove(id)
    }

    fun getNationRatio(): Array<Pair<String, Int>>{
        val keys = nations.keys
        var result = emptyArray<Pair<String, Int>>()
        for (key in keys){
            result = result.plus(Pair(key, nations[key]!!.size))
        }
        return result
    }
}