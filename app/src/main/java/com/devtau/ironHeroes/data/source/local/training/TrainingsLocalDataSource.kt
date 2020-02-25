package com.devtau.ironHeroes.data.source.local.training

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.Training

/**
 * Main entry point for accessing trainings data.
 */
interface TrainingsLocalDataSource {

    //<editor-fold desc="Single object operations">
    suspend fun saveItem(item: Training): Long
    suspend fun getItem(id: Long): Result<Training>
    fun observeItem(id: Long): LiveData<Result<Training>>
    suspend fun deleteItem(id: Long): Int
    //</editor-fold>


    //<editor-fold desc="Group operations">
    suspend fun saveList(list: List<Training>)
    suspend fun getList(): Result<List<Training>>
    fun observeList(): LiveData<Result<List<Training>>>
    suspend fun deleteAll(): Int
    //</editor-fold>
}