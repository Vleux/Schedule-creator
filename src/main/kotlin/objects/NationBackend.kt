package objects

object NationBackend {
    private var Nations: ArrayList<String> = arrayListOf("macedonia", "germany")

    fun addNation(newNation: String){
        Nations.add(newNation)
    }

    /**
     * Deletes a Natoin if it is valid
     */
    fun deleteNation(obsoleteNation: String){
        if (this.validNation(obsoleteNation)){
            this.Nations.remove(this.parseNation(obsoleteNation))
        }
    }

    private fun validNation(nation: String): Boolean {
        return Nations.contains(nation.lowercase())
    }

    /**
     * checks if a Nation is valid and returns the nation, the first Letter is capitalized
     */
    fun parseNation(nation: String): String{
        return if (validNation(nation)){
            nation.lowercase().replaceFirstChar { it.uppercase() }
        }else{
            "invalid"
        }
    }
}