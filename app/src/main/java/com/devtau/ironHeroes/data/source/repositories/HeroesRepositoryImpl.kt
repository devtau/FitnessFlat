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
    private val local: HeroesLocalDataSource,
    private val remote: HeroesLocalDataSource? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): HeroesRepository {

    override suspend fun saveItem(item: Hero): Long {
        val id = local.saveItem(item)
        item.id = id
        remote?.saveItem(item)
        return id
    }

    override suspend fun getItem(id: Long?, forceUpdate: Boolean): Result<Hero?> =
        wrapEspressoIdlingResource {
            if (forceUpdate) updateFromRemoteDataSource(id)
            return local.getItem(id)
        }

    override fun observeItem(id: Long?) = local.observeItem(id)

    override suspend fun deleteItem(item: Hero) {
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

    override suspend fun saveList(list: List<Hero>) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remote?.saveList(list) }
                launch { local.saveList(list) }
            }
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

    override fun observeList() = local.observeList()

    override suspend fun deleteAll() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remote?.deleteAll() }
                launch { local.deleteAll() }
            }
        }
    }


    private suspend fun updateFromRemoteDataSource(id: Long?) {
        val remoteList = remote?.getItem(id)
        if (remoteList is Success && remoteList.data != null) local.saveItem(remoteList.data)
    }
}