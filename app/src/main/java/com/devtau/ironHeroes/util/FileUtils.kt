package com.devtau.ironHeroes.util

import android.os.Environment
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Training
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object FileUtils {

    private const val TXT_EXT = ".txt"
    const val TRAININGS_FILE_NAME = "Trainings"
    const val EXERCISES_FILE_NAME = "ExercisesInTrainings"


    suspend fun exportToJSON(
        list: List<*>, exchangeDirName: String, fileName: String
    ): Int? = withContext(Dispatchers.IO) {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val exchangeDir = File(downloadsDir, exchangeDirName)
        if (!exchangeDir.exists()) exchangeDir.mkdirs()

        val json = serializeList(list)
        if (json == null) {
            Timber.e("exportToJSON. json is null. aborting")
            return@withContext null
        }

        val file = File(exchangeDir, "$fileName$TXT_EXT")
        var writer: FileWriter? = null
        var result: Int?
        try {
            file.createNewFile()
            writer = FileWriter(file)
            writer.write(json)
            Timber.d("exportToJSON. exported")
            result = list.size
        } catch (e: Exception) {
            Timber.e("exportToJSON. error ${e.message}\n$e")
            result = null
        } finally {
            writer?.close()
        }
        result
    }


    suspend fun readTrainingsJSON(
        exchangeDirName: String, fileName: String
    ): List<Training> = withContext(Dispatchers.IO) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val exchangeDir = File(downloadsDir, exchangeDirName)
        val fileToRead = File(exchangeDir, "$fileName$TXT_EXT")
        val reader = BufferedReader(FileReader(fileToRead))
        val list = deserializeListOfTrainings(reader.readLine())
        reader.close()
        Timber.d("readTrainingsJSON. heroes=$list")
        list ?: emptyList()
    }

    suspend fun readExercisesJSON(
        exchangeDirName: String, fileName: String
    ): List<ExerciseInTraining> = withContext(Dispatchers.IO) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val exchangeDir = File(downloadsDir, exchangeDirName)
        val fileToRead = File(exchangeDir, "$fileName$TXT_EXT")
        val reader = BufferedReader(FileReader(fileToRead))
        val list = deserializeListOfExercisesInTrainings(reader.readLine())
        reader.close()
        Timber.d("readExercisesJSON. heroes=$list")
        list ?: emptyList()
    }


    private fun serializeList(list: List<*>): String? = Gson().toJson(list)

    private fun deserializeListOfTrainings(string: String?): List<Training>? =
        if (string == null || string.isEmpty()) null
        else {
            val listType = object: TypeToken<List<Training>>() {}.type
            Gson().fromJson<List<Training>>(string, listType)
        }

    private fun deserializeListOfExercisesInTrainings(string: String?): List<ExerciseInTraining>? =
        if (string == null || string.isEmpty()) null
        else {
            val listType = object: TypeToken<List<ExerciseInTraining>>() {}.type
            Gson().fromJson<List<ExerciseInTraining>>(string, listType)
        }
}