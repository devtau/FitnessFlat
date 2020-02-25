package com.devtau.ironHeroes.data.source.repositories

import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Error
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSource
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*

class HeroesRepositoryImpl(
    private val remote: HeroesLocalDataSource,
    private val local: HeroesLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): HeroesRepository {
    override suspend fun saveItem(item: Hero) {
        coroutineScope {
            launch { remote.saveItem(item) }
            launch { local.saveItem(item) }
        }
    }

    override suspend fun getItem(id: Long, forceUpdate: Boolean): Result<Hero> =
        wrapEspressoIdlingResource {
            if (forceUpdate) updateFromRemoteDataSource(id)
            return local.getItem(id)
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

    override suspend fun getList(forceUpdate: Boolean): Result<List<Hero>> =
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

    override fun observeList(humanType: HumanType) = local.observeList(humanType)

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