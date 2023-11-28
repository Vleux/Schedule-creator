package files

import scheduling.classes.time.Time
import java.io.BufferedWriter
import java.io.File

abstract class WriteFile(path: String) {
    private val file: File = File(path)
    private val writer: BufferedWriter = file.bufferedWriter()
    val dataTable: MutableMap<Time, MutableList<String>> = mutableMapOf()
    val days: MutableList<String> = mutableListOf()

    fun writeFile(){
        this.prepareData()
        this.parseData()
        this.cleanUp()
    }

    /**
     * Reading data and storing it in dataTable and Days
     */
    protected abstract fun prepareData()

    /**
     * Parsing the data from dataTable and days into lines and
     * writing them into the csv file
     */
    protected abstract fun parseData()

    /**
     *
     */
    protected fun writeLine(line: Array<String>, delimiter: String){
        writer.write(line.joinToString(delimiter))
        writer.newLine()
    }

    private fun cleanUp(){
        writer.close()
    }

}