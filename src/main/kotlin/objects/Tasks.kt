package objects

import classes.data.Task

object Tasks {

    private val allTasks: ArrayList<Task> = arrayListOf()

    fun doesTaskExist(taskId: String): Boolean{
        for (task in this.allTasks){
            if (task.id == taskId){
                return true
            }
        }
        return false
    }

    /**
     * Adds a new Task to all Tasks.
     * True -> Successfully added
     * False -> Task with same id does already exist
     */
    fun addTask(newTask: Task): Boolean{
        return if (!this.doesTaskExist(newTask.id)){
            this.allTasks.add(newTask)
            true
        }else{
            false
        }
    }

    fun removeTask(oldTaskId: String): Boolean{
        for (task in this.allTasks){
            if (task.id == oldTaskId){
                this.allTasks.remove(task)
                return true
            }
        }
        return false
    }

    fun getTask(taskId: String): Task?{
        for (task in this.allTasks){
            if (task.id == taskId){
                return task
            }
        }
        return null
    }

    fun changeTask(changedTask: Task): Boolean{
        return if (doesTaskExist(changedTask.id)){
            this.removeTask(changedTask.id)
            this.addTask(changedTask)
            true
        }else{
            false
        }
    }

}