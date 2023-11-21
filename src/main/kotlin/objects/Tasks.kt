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


}