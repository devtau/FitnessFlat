package com.devtau.ironHeroes.data.source.repositories

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.Training

interface TrainingsRepository {

    suspend fun saveItem(item: Training)
    suspend fun getItem(id: Long, forceUpdate: Boolean = false): Result<Training>
    fun observeItem(id: Long): LiveData<Result<Training>>
    suspend fun deleteItem(id: Long)

    suspend fun fetchItemsFromBackend()
    suspend fun getList(forceUpdate: Boolean = false): Result<List<Training>>
    fun observeList(): LiveData<Result<List<Training>>>
    suspend fun deleteAll()
}