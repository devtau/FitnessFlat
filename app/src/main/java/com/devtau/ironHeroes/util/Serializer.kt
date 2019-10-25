package com.devtau.ironHeroes.util

import android.text.TextUtils
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Serializer {

    private const val LOG_TAG = "Serializer"

    fun serializeList(list: List<*>?): String? = if (AppUtils.isEmpty(list)) null else Gson().toJson(list)


    fun deserializeListOfLongs(string: String?): ArrayList<Long>? =
            if (TextUtils.isEmpty(string)) null
            else {
                val listType = object: TypeToken<ArrayList<Long>>() {}.type
                Gson().fromJson<ArrayList<Long>>(string, listType)
            }

    fun deserializeListOfInts(string: String?): ArrayList<Int>? =
            if (TextUtils.isEmpty(string)) null
            else {
                val listType = object: TypeToken<ArrayList<Int>>() {}.type
                Gson().fromJson<ArrayList<Int>>(string, listType)
            }
}