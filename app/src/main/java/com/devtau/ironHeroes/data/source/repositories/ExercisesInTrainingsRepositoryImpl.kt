package com.devtau.ironHeroes.data.source.repositories

import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Error
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.source.local.exerciseInTraining.ExerciseInTrainingLocalDataSource
import com.devtau.ironHeroes.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*

class ExercisesInTrainingsRepositoryImpl(
    private val remote: ExerciseInTrainingLocalDataSource?,
    private val local: ExerciseInTrainingLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ExercisesInTrainingsRepository {

    override suspend fun saveItem(item: ExerciseInTraining): Long {
        val id = local.saveItem(item)
        item.id = id
        remote?.saveItem(item)
        return id
    }

    override suspend fun getItem(id: Long?, forceUpdate: Boolean): Result<ExerciseInTraining?> {
        // Set app as busy while this function executes.
        wrapEspressoIdlingResource {
            if (forceUpdate) updateFromRemoteDataSource(id)
            return local.getItem(id)
        }
    }

    override fun observeItem(id: Long?) = local.observeItem(id)

    override suspend fun deleteItem(item: ExerciseInTraining) {
        coroutineScope {
            launch { remote?.deleteItem(item) }
            launch { local.deleteItem(item) }
        }
    }


    override suspend fun fetchItemsFromBackend() {
        when (val remoteList = remote?.getList()) {
            is Success -> {
                local.deleteAll()
                remoteList.data.forEach { local.saveItem(it) }
            }
            is Error -> throw remoteList.exception
        }
    }

    override suspend fun saveList(list: List<ExerciseInTraining>) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remote?.saveList(list) }
                launch { local.saveList(list) }
            }
        }
    }

    override suspend fun getList(forceUpdate: Boolean): Result<List<ExerciseInTraining>> {
        // Set app as busy while this function executes.
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                try {
                    fetchItemsFromBackend()
                } catch (ex: Exception) {
                    return Error(ex)
                }
            }
            return local.getList()
        }
    }

    override fun observeList() = local.observeList()

    override fun observeListForTraining(trainingId: Long?) = local.observeListForTraining(trainingId)

    override fun observeListForHero(heroId: Long?) = local.observeListForHero(heroId)

    override suspend fun getListForHero(heroId: Long?): Result<List<ExerciseInTraining>> {
        wrapEspressoIdlingResource {
            return local.getListForHero(heroId)
        }
    }

    override suspend fun deleteListForTraining(trainingId: Long) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remote?.deleteListForTraining(trainingId) }
                launch { local.deleteListForTraining(trainingId) }
            }
        }
    }

    override suspend fun deleteAll() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remote?.deleteAll() }
                launch { local.deleteAll() }
            }
        }
    }


    private suspend fun updateFromRemoteDataSource(id: Long?) {
        id ?: return
        val remoteList = remote?.getItem(id)
        if (remoteList is Success && remoteList.data != null) local.saveItem(remoteList.data)
    }
}