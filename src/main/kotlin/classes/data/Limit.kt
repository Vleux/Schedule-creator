package classes.data

/**
 * This class saves the Limits of Tasks in the different Categories.
 * These are general, maximum fairness and medium fairness.
 */
class Limit(generalLimit: Int, maxFairLimit: Int, medFairLimit: Int) {
    private var genLimit: Int = generalLimit   //The general Limit for tasks
    private var fairMax: Int = maxFairLimit   // The Limit for max. fair tasks
    private var fairMed: Int = medFairLimit   // The Limit for med. fair tasks

    init {
        if (this.genLimit < (this.fairMed + this.fairMax)){
            this.genLimit = this.fairMax + this.fairMed
            println("The general limit (${this.genLimit}) is smaller than the fairness limits ${this.fairMed + this.fairMax}")
            println("The general limit was changed to the increased value.")
        }
    }

    fun getGeneralLimit(): Int{return this.genLimit}
    fun getMaximalFairnessLimit(): Int {return this.fairMax}
    fun getMediumFairnessLimit(): Int {return this.fairMax}

    fun setGeneralLimit(newLimit: Int): Boolean{
        if (newLimit < (this.fairMax + this.fairMed)){
            println("The given limit is too small")
            return false
        }
        this.genLimit = newLimit
        return true
    }

    /**
     * Returns false if the general Limit was changed
     */
    fun setMaximalFairnessLimit(newLimit: Int): Boolean{
        this.fairMax = newLimit

        if ((this.fairMax + this.fairMax )> this.genLimit){
            println("The given Limit for maximal fairness $newLimit required the change of the general Limit.")
            println("changed from ${this.genLimit} to ${this.fairMax + this.fairMax}")
            this.genLimit = this.fairMax + this.fairMed
            return false
        }
        return true
    }

    /**
     * Returns false if the general Limit was changed
     */
    fun setMediumFairnessLimit(newLimit: Int): Boolean{
        this.fairMed = newLimit

        if (this.genLimit < (this.fairMax + this.fairMed)){
            println("The change of the medium fairness limit required the change of the general Limit")
            println("The general Limit was changed from ${this.genLimit} to ${this.fairMax + this.fairMed}")
            this.genLimit = this.fairMed + this.fairMax
            return false
        }
        return true
    }
}