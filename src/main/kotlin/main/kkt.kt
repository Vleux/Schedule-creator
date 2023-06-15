package main

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {
    val kid = Child()
    kid.use()

}

open class Parent{
    protected open fun new(){
        println(1)
    }

    fun use(){
        println("Hello")
        new()
    }
}

class Child: Parent() {
    override fun new(){
        super.new()
        println("Added")
    }
}
