package objects

import classes.task.Task

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

    fun getTask(taskId: String): Task?{
        for (task in this.allTasks){
            if (task.id == taskId){
                return task
            }
        }
        return null
    }

    fun getAllTasks(): ArrayList<Task>{
        return this.allTasks
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

    /**
     * Deletes a Task.
     * false if it does not exist on the firsthand
     */
    fun removeTask(oldTaskId: String): Boolean{
        for (task in this.allTasks){
            if (task.id == oldTaskId){
                this.allTasks.remove(task)
                return true
            }
        }
        return false
    }


}