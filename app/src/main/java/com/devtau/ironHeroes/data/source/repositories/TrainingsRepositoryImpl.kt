package com.devtau.ironHeroes.data.source.repositories

import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Error
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSource
import com.devtau.ironHeroes.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*

class TrainingsRepositoryImpl(
    private val remote: TrainingsLocalDataSource,
    private val local: TrainingsLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TrainingsRepository {

    override suspend fun saveItem(item: Training) {
        coroutineScope {
            launch { remote.saveItem(item) }
            launch { local.saveItem(item) }
        }
    }

    override suspend fun getItem(id: Long, forceUpdate: Boolean): Result<Training> {
        // Set app as busy while this function executes.
        wrapEspressoIdlingResource {
            if (forceUpdate) updateFromRemoteDataSource(id)
            return local.getItem(id)
        }
    }

    override fun observeItem(id: Long) = local.observeItem(id)

    override suspend fun deleteItem(id: Long) {
        coroutineScope {
            launch { remote.deleteItem(id) }
            launch { local.deleteItem(id) }
        }
    }


    override suspend fun fetchItemsFromBackend() {
        when (val remoteList = remote.getList()) {
            is Success -> {
                local.deleteAll()
                remoteList.data.forEach { local.saveItem(it) }
            }
            is Error -> throw remoteList.exception
        }
    }

    override suspend fun getList(forceUpdate: Boolean): Result<List<Training>> {
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

    override suspend fun deleteAll() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remote.deleteAll() }
                launch { local.deleteAll() }
            }
        }
    }


    private suspend fun updateFromRemoteDataSource(id: Long) {
        val remoteList = remote.getItem(id)
        if (remoteList is Success) local.saveItem(remoteList.data)
    }
}