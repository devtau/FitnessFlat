package com.devtau.ironHeroes.util

import android.database.Cursor
import android.os.Environment
import com.devtau.ironHeroes.util.FileUtils.CSV_EXT
import com.devtau.ironHeroes.util.FileUtils.ESCAPE_CHAR
import com.devtau.ironHeroes.util.FileUtils.LINE_END
import com.devtau.ironHeroes.util.FileUtils.QUOTE_CHAR
import com.devtau.ironHeroes.util.FileUtils.SEPARATOR
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
        for ((i, next) in nextLine.withIndex()) {
            if (i != 0) sb.append(SEPARATOR)
            next ?: continue
            sb.append(QUOTE_CHAR)
            for (nextChar in next) {
                if (nextChar == QUOTE_CHAR || nextChar == ESCAPE_CHAR) {
                    sb.append(ESCAPE_CHAR).append(nextChar)
                } else {
                    sb.append(nextChar)
                }
            }
            sb.append(QUOTE_CHAR)
        }

        sb.append(LINE_END)
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

            val file = File(exchangeDir, "$fileName$CSV_EXT")
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