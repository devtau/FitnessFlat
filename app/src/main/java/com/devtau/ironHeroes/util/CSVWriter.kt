package com.devtau.ironHeroes.util

import android.database.Cursor
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.Writer

class CSVWriter(writer: Writer) {

    private val pw: PrintWriter = PrintWriter(writer)


    fun writeNext(nextLine: Array<String?>?) {
        nextLine ?: return
        pw.write(convertToLine(nextLine))
    }

    private fun convertToLine(nextLine: Array<String?>): String {
        val sb = StringBuffer()
        for (i in nextLine.indices) {
            if (i != 0) sb.append(Constants.SEPARATOR)
            val nextElement = nextLine[i]
            nextElement ?: continue
            sb.append(Constants.QUOTE_CHAR)
            for (j in 0 until nextElement.length) {
                val nextChar = nextElement[j]
                if (nextChar == Constants.QUOTE_CHAR || nextChar == Constants.ESCAPE_CHAR) {
                    sb.append(Constants.ESCAPE_CHAR).append(nextChar)
                } else {
                    sb.append(nextChar)
                }
            }
            sb.append(Constants.QUOTE_CHAR)
        }

        sb.append(Constants.LINE_END)
        return sb.toString()
    }

    fun flush() = pw.flush()

    fun close() {
        pw.flush()
        pw.close()
    }


    companion object {
        private const val LOG_TAG = "CSVWriter"

        fun exportToCSV(cursor: Cursor, exchangeDirName: String, fileName: String) {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val exchangeDir = File(downloadsDir, exchangeDirName)
            if (!exchangeDir.exists()) exchangeDir.mkdirs()

            val file = File(exchangeDir, "$fileName${Constants.CSV_EXT}")
            var csvWriter: CSVWriter? = null
            try {
                file.createNewFile()
                csvWriter = CSVWriter(FileWriter(file))
                csvWriter.writeNext(cursor.columnNames)
                while (cursor.moveToNext()) {
                    val nextValue = arrayOfNulls<String>(cursor.columnCount)
                    for (i in 0 until cursor.columnCount) nextValue[i] = cursor.getString(i)
                    csvWriter.writeNext(nextValue)
                }
                Logger.d(LOG_TAG, "exportToCSV. exported")
            } catch (e: Exception) {
                Logger.e(LOG_TAG, "exportToCSV. error ${e.message}\n$e")
            } finally {
                csvWriter?.close()
                cursor.close()
            }
        }
    }
}