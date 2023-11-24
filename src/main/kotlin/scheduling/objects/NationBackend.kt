package scheduling.objects

object NationBackend {
    private var Nations: ArrayList<String> = arrayListOf("macedonia", "germany")

    fun addNation(newNation: String){
        Nations.add(newNation)
    }

    /**
     * Deletes a Nation if it is valid
     */
    fun deleteNation(obsoleteNation: String){
        if (validNation(obsoleteNation)){
            Nations.remove(parseNation(obsoleteNation))
        }
    }

    private fun validNation(nation: String): Boolean {
        return Nations.contains(nation.lowercase())
    }

    /**
     * checks if a Nation is valid and returns the nation, the first Letter is capitalized
     */
    fun parseNation(nation: String): String?{
        return if (validNation(nation)){
            nation.lowercase().replaceFirstChar { it.uppercase() }
        }else{
            null
        }
    }

    fun getAllNations(): Array<String>{
        return Nations.toTypedArray()
    }
}