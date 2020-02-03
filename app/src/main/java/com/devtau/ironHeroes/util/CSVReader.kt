package com.devtau.ironHeroes.util

import android.os.Environment
import java.io.*
import java.util.ArrayList

class CSVReader(reader: Reader) {

    private val br: BufferedReader = BufferedReader(reader)
    private var hasNext = true


    @Throws(IOException::class)
    fun readNext(): Array<String>? {
        val nextLine = readNextLine()
        return if (hasNext) parseLine(nextLine) else null
    }

    @Throws(IOException::class)
    private fun readNextLine(): String? {
        val nextLine = br.readLine()
        if (nextLine == null) hasNext = false
        return if (hasNext) nextLine else null
    }

    @Throws(IOException::class)
    private fun parseLine(nextLine: String?): Array<String>? {
        var nextLineLoc: String? = nextLine ?: return null

        val tokensOnThisLine = ArrayList<String>()
        var sb = StringBuffer()
        var inQuotes = false
        do {
            if (inQuotes) {
                sb.append(Constants.LINE_END)
                nextLineLoc = readNextLine()
                if (nextLineLoc == null) break
            }
            var i = 0
            while (i < nextLineLoc!!.length) {
                val c = nextLineLoc[i]
                when {
                    c == Constants.QUOTE_CHAR ->
                        if (inQuotes && nextLineLoc.length > i + 1 && nextLineLoc[i + 1] == Constants.QUOTE_CHAR) {
                            sb.append(nextLineLoc[i + 1])
                            i++
                        } else {
                            inQuotes = !inQuotes
                            if (i > 2 && nextLineLoc[i - 1] != Constants.SEPARATOR &&
                                nextLineLoc.length > i + 1 && nextLineLoc[i + 1] != Constants.SEPARATOR) {
                                sb.append(c)
                            }
                        }
                    c == Constants.SEPARATOR && !inQuotes -> {
                        tokensOnThisLine.add(sb.toString())
                        sb = StringBuffer()
                    }
                    else -> sb.append(c)
                }
                i++
            }
        } while (inQuotes)
        tokensOnThisLine.add(sb.toString())
        return tokensOnThisLine.toTypedArray()
    }

    @Throws(IOException::class)
    fun close() = br.close()


    companion object {
        private const val LOG_TAG = "CSVReader"

        fun readCSV(exchangeDirName: String, fileName: String) {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val exchangeDir = File(downloadsDir, exchangeDirName)
            val fileToRead = File(exchangeDir, "$fileName${Constants.CSV_EXT}")
            val reader = CSVReader(FileReader(fileToRead))

            var nextLine: Array<String>?
            val values = StringBuilder()

            nextLine = reader.readNext()
            while (nextLine != null) {
                for (i in 0 until nextLine.size) {
                    if (i == nextLine.size - 1) values.append(nextLine[i])
                    else values.append(nextLine[i]).append(Constants.SEPARATOR)
                }
                values.append("\n")
                nextLine = reader.readNext()
            }
            Logger.d(LOG_TAG, "readCSV. values=$values")
        }
    }
}