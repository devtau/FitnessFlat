package com.devtau.ironHeroes.data.source.local.muscleGroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Error
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.MuscleGroup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MuscleGroupLocalDataSourceImpl internal constructor(
    private val dao: MuscleGroupDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): MuscleGroupLocalDataSource {

    //<editor-fold desc="Single object operations">
    override suspend fun saveItem(item: MuscleGroup) = withContext(ioDispatcher) {
        dao.insert(item)
    }

    override suspend fun getItem(id: Long?): Result<MuscleGroup?> = withContext(ioDispatcher) {
        val item = dao.getById(id)
        if (item != null) {
            Success(item)
        } else {
            Error(Exception("MuscleGroup not found"))
        }
    }

    override fun observeItem(id: Long?): LiveData<Result<MuscleGroup?>> =
        dao.observeItem(id).map {
            if (it == null) Error(Exception("Training not found"))
            else Success(it)
        }

    override suspend fun deleteItem(item: MuscleGroup) = withContext(ioDispatcher) {
        dao.delete(item)
    }
    //</editor-fold>


    //<editor-fold desc="Group operations">
    override suspend fun saveList(list: List<MuscleGroup>) = withContext(ioDispatcher) {
        dao.insertList(list)
    }

    override suspend fun getList(): Result<List<MuscleGroup>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.getList())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override fun observeList(): LiveData<Result<List<MuscleGroup>>> =
        dao.observeList().switchMap {
            convertList(it)
        }

    override suspend fun deleteAll() = withContext(ioDispatcher) {
        dao.delete()
    }
    //</editor-fold>


    private fun convertList(list: List<MuscleGroup>): LiveData<Result<List<MuscleGroup>>> {
        val result = MutableLiveData<Result<List<MuscleGroup>>>()
        result.value = Success(list)
        return result
    }
}