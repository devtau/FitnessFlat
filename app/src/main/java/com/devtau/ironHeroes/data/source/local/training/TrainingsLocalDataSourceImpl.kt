package com.devtau.ironHeroes.data.source.local.training

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Error
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.Training
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db
 */
class TrainingsLocalDataSourceImpl internal constructor(
    private val dao: TrainingDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TrainingsLocalDataSource {

    //<editor-fold desc="Single object operations">
    override suspend fun saveItem(item: Training) = withContext(ioDispatcher) {
        dao.insert(item)
    }

    override suspend fun getItem(id: Long): Result<Training> = withContext(ioDispatcher) {
        try {
            val item = dao.getById(id)?.convert()
            if (item != null) {
                return@withContext Success(item)
            } else {
                return@withContext Error(Exception("Training not found"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override fun observeItem(id: Long): LiveData<Result<Training>> =
        dao.observeItem(id).map {
            Success(it.convert())
        }

    override suspend fun deleteItem(id: Long) = withContext(ioDispatcher) {
        dao.delete(id)
    }
    //</editor-fold>


    //<editor-fold desc="Group operations">
    override suspend fun saveList(list: List<Training>) = withContext(ioDispatcher) {
        dao.insertList(list)
    }

    override suspend fun getList(): Result<List<Training>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.getList().map { relation -> relation.convert() })
        } catch (e: Exception) {
            Error(e)
        }
    }

    override fun observeList(): LiveData<Result<List<Training>>> =
        dao.observeList().switchMap {
            convertRelations(it)
        }

    override suspend fun deleteAll() = withContext(ioDispatcher) {
        dao.delete()
    }
    //</editor-fold>


    private fun convertRelations(relations: List<TrainingRelation>): LiveData<Result<List<Training>>> {
        val result = MutableLiveData<Result<List<Training>>>()
        result.value = Success(relations.map { it.convert() })
        return result
    }
}