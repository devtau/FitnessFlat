package com.devtau.ironHeroes.data.source.local.exerciseInTraining

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Error
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExerciseInTrainingLocalDataSourceImpl internal constructor(
    private val dao: ExerciseInTrainingDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ExerciseInTrainingLocalDataSource {

    //<editor-fold desc="Single object operations">
    override suspend fun saveItem(item: ExerciseInTraining) = withContext(ioDispatcher) {
        dao.insert(item)
    }

    override suspend fun getItem(id: Long?) = withContext(ioDispatcher) {
        val item = dao.getById(id)?.convert()
        if (item != null) Success(item)
        else Error(Exception("ExerciseInTraining not found"))
    }

    override fun observeItem(id: Long?): LiveData<Result<ExerciseInTraining?>> =
        dao.observeItem(id).map {
            if (it == null) Error(Exception("ExerciseInTraining not found"))
            else Success(it.convert())
        }

    override suspend fun deleteItem(item: ExerciseInTraining) = withContext(ioDispatcher) {
        dao.delete(item)
    }
    //</editor-fold>


    //<editor-fold desc="Group operations">
    override suspend fun saveList(list: List<ExerciseInTraining>) = withContext(ioDispatcher) {
        dao.insertList(list)
    }

    override suspend fun getList() = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.getList().map { relation -> relation.convert() })
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getListForHero(heroId: Long?) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.getListForHero(heroId).map { relation -> relation.convert() })
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getListForTraining(trainingId: Long) = withContext(ioDispatcher) {
        return@withContext try {
            Success(dao.getListForTraining(trainingId).map { relation -> relation.convert() })
        } catch (e: Exception) {
            Error(e)
        }
    }

    override fun observeList() =
        dao.observeList().switchMap {
            convertRelations(it)
        }

    override fun observeListForHero(heroId: Long?) =
        dao.observeListForHero(heroId).switchMap {
            convertRelations(it)
        }

    override fun observeListForTraining(trainingId: Long?) =
        dao.observeListForTraining(trainingId).switchMap {
            convertRelations(it)
        }

    override suspend fun deleteListForTraining(trainingId: Long) = withContext(ioDispatcher) {
        dao.deleteListForTraining(trainingId)
    }

    override suspend fun deleteAll() = withContext(ioDispatcher) {
        dao.delete()
    }
    //</editor-fold>


    private fun convertRelations(relations: List<ExerciseInTrainingRelation>): LiveData<Result<List<ExerciseInTraining>>> {
        val result = MutableLiveData<Result<List<ExerciseInTraining>>>()
        result.value = if (relations.isEmpty()) Success(emptyList())
        else Success(relations.map { it.convert() })
        return result
    }
}