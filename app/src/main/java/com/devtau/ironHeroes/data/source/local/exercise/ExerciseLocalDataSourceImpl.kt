package com.devtau.ironHeroes.data.source.local.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Error
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.Exercise
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExerciseLocalDataSourceImpl internal constructor(
    private val dao: ExerciseDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ExerciseLocalDataSource {

    //<editor-fold desc="Single object operations">
    override suspend fun saveItem(item: Exercise) = withContext(ioDispatcher) {
        dao.insert(item)
    }

    override suspend fun getItem(id: Long?): Result<Exercise> = withContext(ioDispatcher) {
        val item = dao.getById(id)?.convert()
        if (item != null) Success(item)
        else Error(Exception("Exercise not found"))
    }

    override fun observeItem(id: Long?): LiveData<Result<Exercise?>> =
        dao.observeItem(id).map {
            if (it == null) Error(Exception("Exercise not found"))
            else Success(it.convert())
        }

    override suspend fun deleteItem(item: Exercise) = withContext(ioDispatcher) {
        dao.delete(item)
    }
    //</editor-fold>


    //<editor-fold desc="Group operations">
    override suspend fun saveList(list: List<Exercise>) = withContext(ioDispatcher) {
        dao.insertList(list)
    }

    override suspend fun getList(): Result<List<Exercise>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.getList().map { relation -> relation.convert() })
        } catch (e: Exception) {
            Error(e)
        }
    }

    override fun observeList(): LiveData<Result<List<Exercise>>> =
        dao.observeList().switchMap {
            convertRelations(it)
        }

    override suspend fun deleteAll() = withContext(ioDispatcher) {
        dao.delete()
    }
    //</editor-fold>


    private fun convertRelations(relations: List<ExerciseRelation>): LiveData<Result<List<Exercise>>> {
        val result = MutableLiveData<Result<List<Exercise>>>()
        result.value = Success(relations.map { it.convert() })
        return result
    }
}