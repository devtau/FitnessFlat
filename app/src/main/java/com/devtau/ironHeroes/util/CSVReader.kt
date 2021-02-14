package com.devtau.ironHeroes.util

import android.os.Environment
import com.devtau.ironHeroes.util.FileUtils.CSV_EXT
import com.devtau.ironHeroes.util.FileUtils.LINE_END
import com.devtau.ironHeroes.util.FileUtils.QUOTE_CHAR
import com.devtau.ironHeroes.util.FileUtils.SEPARATOR
import timber.log.Timber
import java.io.*
import java.util.*

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
                sb.append(LINE_END)
                nextLineLoc = readNextLine()
                if (nextLineLoc == null) break
            }
            var i = 0
            while (i < nextLineLoc!!.length) {
                val c = nextLineLoc[i]
                when {
                    c == QUOTE_CHAR ->
                        if (inQuotes && nextLineLoc.length > i + 1 && nextLineLoc[i + 1] == QUOTE_CHAR) {
                            sb.append(nextLineLoc[i + 1])
                            i++
                        } else {
                            inQuotes = !inQuotes
                            if (i > 2 && nextLineLoc[i - 1] != SEPARATOR &&
                                nextLineLoc.length > i + 1 && nextLineLoc[i + 1] != SEPARATOR) {
                                sb.append(c)
                            }
                        }
                    c == SEPARATOR && !inQuotes -> {
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
        fun readCSV(exchangeDirName: String, fileName: String) {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val exchangeDir = File(downloadsDir, exchangeDirName)
            val fileToRead = File(exchangeDir, "$fileName$CSV_EXT")
            val reader = CSVReader(FileReader(fileToRead))

            var nextLine: Array<String>?
            val values = StringBuilder()

            nextLine = reader.readNext()
            while (nextLine != null) {
                for (i in nextLine.indices) {
                    if (i == nextLine.size - 1) values.append(nextLine[i])
                    else values.append(nextLine[i]).append(SEPARATOR)
                }
                values.append("\n")
                nextLine = reader.readNext()
            }
            Timber.d("readCSV. values=$values")
        }
    }
}