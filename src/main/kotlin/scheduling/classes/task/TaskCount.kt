package scheduling.classes.task

class TaskCount {
    private var _generalTasks: Int = 0
    var generalTasks
        get() = _generalTasks
        set(new){
            val cache = new - _generalTasks
            if (_generalTasks + cache > 0 && ((_generalTasks + cache) >= (maximalFairnessTasks + mediumFairnessTasks))){
                _generalTasks = new
            }
        }
    var maximalFairnessTasks: Int = 0
        set(new) {
            val cache = new - maximalFairnessTasks
            if (maximalFairnessTasks + new > 0){
                _generalTasks += cache
                field = new
            }
        }
    var mediumFairnessTasks: Int = 0
        set (new){
            val cache = new - mediumFairnessTasks
            if (mediumFairnessTasks + new > 0){
                this._generalTasks += cache
                field = new
            }
        }

    var lowFairnessTasks: Int = 0
        set (new){
            val cache = new-lowFairnessTasks
            if (lowFairnessTasks + new > 0){
                this._generalTasks += cache
                field = new
            }
        }
}