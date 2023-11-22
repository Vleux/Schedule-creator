package classes.data

import objects.People

class PersonList {
    private var maxF: MutableMap<String, Int> = mutableMapOf()
    private var medF: MutableMap<String, Int> = mutableMapOf()
    private var lowF: MutableMap<String, Int> = mutableMapOf()
    private var genT: MutableMap<String, Int> = mutableMapOf()

    private var maxFRev: MutableMap<Int, ArrayList<String>> = mutableMapOf()
    private var medFRev: MutableMap<Int, ArrayList<String>> = mutableMapOf()
    private var lowFRev: MutableMap<Int, ArrayList<String>> = mutableMapOf()
    private var genTRev: MutableMap<Int, ArrayList<String>> = mutableMapOf()

    val genTaskCount: MutableMap<Int, ArrayList<String>>
        get() = this.genTRev
    val maxFairnessCount: MutableMap<Int, ArrayList<String>>
        get() = this.maxFRev
    val medFairnessCount: MutableMap<Int, ArrayList<String>>
        get() = this.medFRev
    val lowFairnessCount: MutableMap<Int, ArrayList<String>>
        get() = this.lowFRev

    init{
        val people = People.getAllPeopleIDs()
        val ids = arrayListOf<String>()

        for (id in people){
            val person = People.getPersonById(id)
            maxF[id] = 0
            medF[id] = 0
            lowF[id] = 0
            genT[id] = 0
            ids.add(id)
        }

        maxFRev[0] = ids
        medFRev[0] = ids
        lowFRev[0] = ids
        genTRev[0] = ids
    }

    /**
     * Increasing and decreasing the task counts by 1.
     * Minimal value: 0
     * Does not check for Limits!
     */

    fun increaseMaxFCount(id: String): Boolean{
        val cache = maxF[id] ?: return false

        maxF[id] = maxF[id]!! + 1
        maxFRev[cache]!!.remove(id)
        maxFRev[cache + 1]!!.add(id)
        return true
    }

    fun increaseMedFCount(id: String): Boolean{
        val cache = medF[id] ?: return false

        medF[id] = medF[id]!! + 1
        medFRev[cache]!!.remove(id)
        medFRev[cache + 1]!!.add(id)
        return true
    }

    fun increaseLowFCount(id: String): Boolean{
        val cache = lowF[id] ?: return false

        lowF[id] = lowF[id]!! + 1
        lowFRev[cache]!!.remove(id)
        lowFRev[cache + 1]!!.add(id)
        return true
    }

    fun increaseGenCount(id: String): Boolean{
        val cache = genT[id] ?: return false

        genT[id] = genT[id]!! + 1
        genTRev[cache]!!.remove(id)
        genTRev[cache + 1]!!.add(id)
        return true
    }

    fun decreaseMaxFCount(id: String): Boolean{
        val cache = maxF[id] ?: return false
        if ((cache - 1) < 0){return false}

        maxF[id] = cache - 1
        maxFRev[cache]!!.remove(id)
        maxFRev[cache - 1]!!.add(id)
        return true
    }

    fun decreaseMedFCount(id: String): Boolean{
        val cache = medF[id] ?: return false
        if ((cache - 1) < 0){return false}

        medF[id] = cache - 1
        medFRev[cache]!!.remove(id)
        medFRev[cache - 1]!!.add(id)
        return true
    }

    fun decreaseLowFCount(id: String): Boolean{
        val cache = maxF[id] ?: return false
        if ((cache - 1) < 0){return false}

        lowF[id] = cache - 1
        lowFRev[cache]!!.remove(id)
        lowFRev[cache - 1]!!.add(id)
        return true
    }

    fun decreaseGenCount(id: String): Boolean{
        val cache = genT[id] ?: return false

        genT[id] = genT[id]!! - 1
        genTRev[cache]!!.remove(id)
        genTRev[cache - 1]!!.add(id)
        return true
    }

    fun addPerson(id: String): Boolean{
        if (!People.doesPersonExist(id)){return false}
        val ids = arrayListOf<String>()

        maxF[id] = 0
        medF[id] = 0
        lowF[id] = 0
        genT[id] = 0

        if (maxFRev[0] == null){
            maxFRev[0] = arrayListOf(id)
            medFRev[0] = arrayListOf(id)
            lowFRev[0] = arrayListOf(id)
            genTRev[0] = arrayListOf(id)
        }else{
            maxFRev[0]!!.add(id)
            medFRev[0]!!.add(id)
            lowFRev[0]!!.add(id)
            genTRev[0]!!.add(id)
        }
        return true
    }

    fun removePerson(id: String){
        if (genT[id] == null){return}
        genTRev[genT[id]]!!.remove(id)
        maxFRev[maxF[id]]!!.remove(id)
        medFRev[medF[id]]!!.remove(id)
        lowFRev[lowF[id]]!!.remove(id)

        genT.remove(id)
        maxF.remove(id)
        medF.remove(id)
        lowF.remove(id)

    }
    fun getPeople(): Array<String>{
        return this.genT.keys.toTypedArray()
    }
}