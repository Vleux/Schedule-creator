package files

import exceptions.InvalidCSVFileSeparator
import java.io.File

abstract class ReadFile(path: String) {

    private val file: File = File(path)

    /**
     * Reads the file line by line, splits it and let's it's content to be processed
     */
    protected fun readFile(){
        val reader = file.bufferedReader()
        var line = reader.readLine()
        val separator = this.getFileSeparator(line)

        while (line != null){
            val content = line.split(separator)

            if (!this.processData(content)) println("The line $line is invalid")

            line = reader.readLine()
        }

        this.cleanUp()
    }

    /**
     * Receives every line splitted into a List
     */
    protected abstract fun processData(content: List<String>): Boolean

    /**
     * Is run after the data is processed to clean the traces up
     */
    protected abstract fun cleanUp()

    /**
     * Tries different separator and saves the first one that is working
     */
    private fun getFileSeparator(line: String): String {
        //, ; : \t or \s
        val options = arrayOf(",", ";", "\t", " ")
        var content = line.split(",")

        for (delimiter in options) {
            content = line.split(delimiter)
            if (content.size > 1) return delimiter
        }
        throw InvalidCSVFileSeparator("The CSV file's separator ${file.absoluteFile} is invalid")
    }
}