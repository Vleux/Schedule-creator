package scheduling.classes.data

/**
 * This class saves the Limits of Tasks.csv in the different Categories.
 * These are general, maximum fairness and medium fairness.
 */
class Limit(generalLimit: Int, maxFairLimit: Int, medFairLimit: Int, lowFairLimit: Int) {
    private var genLimit: Int = generalLimit    //The general Limit for tasks
    private var fairMax: Int = maxFairLimit     // The Limit for max. fair tasks
    private var fairMed: Int = medFairLimit     // The Limit for med. fair tasks
    private var fairLow: Int = lowFairLimit     // The limit for the low fair tasks

    init {
        if (this.genLimit < (this.fairMed + this.fairMax + this.fairLow)){
            this.genLimit = this.fairMax + this.fairMed + this.fairLow
            println("The general limit (${this.genLimit}) is smaller than the fairness limits ${this.fairMed + this.fairMax}")
            println("The general limit was changed to the increased value.")
        }
    }

    fun getGeneralLimit(): Int{return this.genLimit}
    fun getMaximalFairnessLimit(): Int {return this.fairMax}
    fun getMediumFairnessLimit(): Int {return this.fairMed}

    fun getLowFairnessLimit(): Int {return this.fairLow}

    fun setGeneralLimit(newLimit: Int): Boolean{
        if (newLimit < (this.fairMax + this.fairMed + this.fairLow)){
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

        if ((this.fairMax + this.fairMed + this.fairLow )> this.genLimit){
            println("The given Limit for maximal fairness $newLimit required the change of the general Limit.")
            println("changed from ${this.genLimit} to ${this.fairMax + this.fairMax + this.fairLow}")
            this.genLimit = this.fairMax + this.fairMed + this.fairLow
            return false
        }
        return true
    }

    /**
     * Returns false if the general Limit was changed
     */
    fun setMediumFairnessLimit(newLimit: Int): Boolean{
        this.fairMed = newLimit

        if (this.genLimit < (this.fairMax + this.fairMed + this.fairLow)){
            println("The change of the medium fairness limit required the change of the general Limit")
            println("The general Limit was changed from ${this.genLimit} to ${this.fairMax + this.fairLow + this.fairMed}")
            this.genLimit = this.fairMed + this.fairMax + this.fairLow
            return false
        }
        return true
    }

    /**
     * Returns false if the general limit was changed
     */
    fun setLowFairnessLimit(newLimit: Int): Boolean{
        this.fairLow = newLimit

        if (this.genLimit < (this.fairMax + this.fairMed + this.fairLow)){
            println("The change of the medium fairness limit required the change of the general Limit")
            println("The general Limit was changed from ${this.genLimit} to ${this.fairMax + this.fairLow + this.fairMed}")

            this.genLimit = this.fairMed + this. fairMax + this.fairLow
            return false
        }
        return true
    }
}