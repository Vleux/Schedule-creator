package objects

object Nation {
    private var Nations: MutableList<String>

    init {
        Nations = mutableListOf("macedonia", "germany")
    }

    fun addNation(newNation: String){
        Nations.add(newNation)
    }

    private fun validNation(nation: String): Boolean {
        return Nations.contains(nation.lowercase())
    }

    /**
     * checks if a Nation is valid and returns the nation, the first Letter is capizalized
     */
    fun parseNation(nation: String): String{
        if (validNation(nation)){
            return nation.lowercase().replaceFirstChar { it.uppercase() }
        }else{
            return "invalid"
        }
    }
}