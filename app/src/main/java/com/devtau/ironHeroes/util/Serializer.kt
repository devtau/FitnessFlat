package com.devtau.ironHeroes.util

import android.text.TextUtils
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Training
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Serializer {

    private const val LOG_TAG = "Serializer"

    fun serializeList(list: List<*>): String? = Gson().toJson(list)


    fun deserializeListOfLongs(string: String?): List<Long>? =
            if (TextUtils.isEmpty(string)) null
            else {
                val listType = object: TypeToken<List<Long>>() {}.type
                Gson().fromJson<List<Long>>(string, listType)
            }

    fun deserializeListOfInts(string: String?): List<Int>? =
            if (TextUtils.isEmpty(string)) null
            else {
                val listType = object: TypeToken<List<Int>>() {}.type
                Gson().fromJson<List<Int>>(string, listType)
            }

    fun deserializeListOfTrainings(string: String?): List<Training>? =
        if (TextUtils.isEmpty(string)) null
        else {
            val listType = object: TypeToken<List<Training>>() {}.type
            Gson().fromJson<List<Training>>(string, listType)
        }

    fun deserializeListOfExercisesInTrainings(string: String?): List<ExerciseInTraining>? =
        if (TextUtils.isEmpty(string)) null
        else {
            val listType = object: TypeToken<List<ExerciseInTraining>>() {}.type
            Gson().fromJson<List<ExerciseInTraining>>(string, listType)
        }
}