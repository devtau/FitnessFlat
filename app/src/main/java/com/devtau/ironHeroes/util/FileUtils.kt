package com.devtau.ironHeroes.util

import android.os.Environment
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Training
import io.reactivex.functions.Consumer
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.concurrent.Callable

object FileUtils {

    private const val LOG_TAG = "FileUtils"

    const val CSV_EXT = ".csv"
    const val TXT_EXT = ".txt"
    const val SEPARATOR = ','
    const val QUOTE_CHAR = '"'
    const val ESCAPE_CHAR = '"'
    const val LINE_END = "\n"
    const val TRAININGS_FILE_NAME = "Trainings"
    const val EXERCISES_FILE_NAME = "ExercisesInTrainings"


    fun exportToJSON(list: List<*>, exchangeDirName: String, fileName: String, listener: Consumer<Int?>? = null) {
        Threading.async(Callable {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val exchangeDir = File(downloadsDir, exchangeDirName)
            if (!exchangeDir.exists()) exchangeDir.mkdirs()

            val json = Serializer.serializeList(list)
            if (json == null) {
                Logger.e(LOG_TAG, "exportToJSON. json is null. aborting")
                return@Callable
            }

            val file = File(exchangeDir, "$fileName$TXT_EXT")
            var writer: FileWriter? = null
            try {
                file.createNewFile()
                writer = FileWriter(file)
                writer.write(json)
                Logger.d(LOG_TAG, "exportToJSON. exported")
                listener?.accept(list.size)
            } catch (e: Exception) {
                Logger.e(LOG_TAG, "exportToJSON. error ${e.message}\n$e")
                listener?.accept(null)
            } finally {
                writer?.close()
            }
        })
    }

    fun readTrainingsJSON(exchangeDirName: String, fileName: String, listener: Consumer<List<Training>>) {
        Threading.async(Callable {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val exchangeDir = File(downloadsDir, exchangeDirName)
            val fileToRead = File(exchangeDir, "$fileName$TXT_EXT")
            val reader = BufferedReader(FileReader(fileToRead))
            val list = Serializer.deserializeListOfTrainings(reader.readLine())
            reader.close()
            Logger.d(LOG_TAG, "readTrainingsJSON. heroes=$list")
            listener.accept(list)
        })
    }

    fun readExercisesJSON(exchangeDirName: String, fileName: String, listener: Consumer<List<ExerciseInTraining>>) {
        Threading.async(Callable {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val exchangeDir = File(downloadsDir, exchangeDirName)
            val fileToRead = File(exchangeDir, "$fileName$TXT_EXT")
            val reader = BufferedReader(FileReader(fileToRead))
            val list = Serializer.deserializeListOfExercisesInTrainings(reader.readLine())
            reader.close()
            Logger.d(LOG_TAG, "readExercisesJSON. heroes=$list")
            listener.accept(list)
        })
    }
}