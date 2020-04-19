package com.devtau.ironHeroes.data.source.local

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result

interface BaseLocalDataSource<T> {

    //<editor-fold desc="Single object operations">
    suspend fun saveItem(item: T): Long
    suspend fun getItem(id: Long?): Result<T?>
    fun observeItem(id: Long?): LiveData<Result<T?>>
    suspend fun deleteItem(item: T): Int
    //</editor-fold>


    //<editor-fold desc="Group operations">
    suspend fun saveList(list: List<T>)
    suspend fun getList(): Result<List<T>>
    fun observeList(): LiveData<Result<List<T>>>
    suspend fun deleteAll(): Int
    //</editor-fold>
}