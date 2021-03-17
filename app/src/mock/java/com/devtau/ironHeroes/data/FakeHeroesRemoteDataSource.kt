package com.devtau.ironHeroes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.devtau.ironHeroes.data.Result.*
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSource
import com.devtau.ironHeroes.enums.HumanType
import java.util.*

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeHeroesRemoteDataSource: HeroesLocalDataSource {

    private val serviceData = LinkedHashMap<Long, Hero>()
    private val observableItems = MutableLiveData<Result<List<Hero>>>()

    override suspend fun saveItem(item: Hero): Long {
        serviceData[item.id!!] = item
        return 1
    }

    override suspend fun getItem(id: Long?): Result<Hero?> {
        serviceData[id]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find hero"))
    }

    override fun observeItem(id: Long?): LiveData<Result<Hero?>> =
        observableItems.map { list ->
            when (list) {
                is Loading -> Loading
                is Error -> Error(list.exception)
                is Success -> {
                    val list = list.data.firstOrNull { it.id == id }
                        ?: return@map Error(Exception("Not found"))
                    Success(list)
                }
            }
        }

    override suspend fun deleteItem(item: Hero): Int {
        serviceData.remove(item.id)
        refreshHeroes()
        return 1
    }

    override suspend fun saveList(list: List<Hero>) {
        for (next in list) serviceData[next.id!!] = next
    }

    override suspend fun getList(humanType: HumanType?): Result<List<Hero>> {
        val list = arrayListOf<Hero>()
        for ((_, value) in serviceData) if (value.humanType == humanType) list.add(value)
        return Success(list)
    }

    override suspend fun getList() = Success(serviceData.values.toList())

    override fun observeList(humanType: HumanType?) = observableItems

    override fun observeList() = observableItems

    override suspend fun deleteAll(): Int {
        val clearedSize = serviceData.size
        serviceData.clear()
        refreshHeroes()
        return clearedSize
    }


    private suspend fun refreshHeroes() = observableItems.postValue(getList())
}