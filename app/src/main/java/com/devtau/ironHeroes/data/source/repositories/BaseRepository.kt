package com.devtau.ironHeroes.data.source.repositories

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result

interface BaseRepository<T> {

    suspend fun saveItem(item: T): Long
    suspend fun getItem(id: Long?, forceUpdate: Boolean = false): Result<T?>
    fun observeItem(id: Long?): LiveData<Result<T?>>
    suspend fun deleteItem(item: T)

    suspend fun fetchItemsFromBackend()
    suspend fun saveList(list: List<T>)
    suspend fun getList(forceUpdate: Boolean = false): Result<List<T>>
    fun observeList(): LiveData<Result<List<T>>>
    suspend fun deleteAll()
}