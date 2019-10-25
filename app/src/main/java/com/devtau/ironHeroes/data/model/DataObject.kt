package com.devtau.ironHeroes.data.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.util.Constants.EMPTY_OBJECT_ID

abstract class DataObject(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    var id: Long? = EMPTY_OBJECT_ID
) {

    fun isEmpty() = id == EMPTY_OBJECT_ID
}