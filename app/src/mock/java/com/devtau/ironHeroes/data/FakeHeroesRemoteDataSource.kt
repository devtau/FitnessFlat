package com.devtau.ironHeroes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.Result.*
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSource
import com.devtau.ironHeroes.enums.HumanType
import java.util.LinkedHashMap
/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeHeroesRemoteDataSource: HeroesLocalDataSource {

    private val tasksServiceData = LinkedHashMap<Long, Hero>()
    private val observableHeroes = MutableLiveData<Result<List<Hero>>>()

    override suspend fun saveItem(item: Hero): Long {
        tasksServiceData[item.id!!] = item
        return 1
    }

    override suspend fun getItem(id: Long): Result<Hero> {
        tasksServiceData[id]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find hero"))
    }

    override fun observeItem(id: Long): LiveData<Result<Hero>> =
        observableHeroes.map { list ->
            when (list) {
                is Loading -> Loading
                is Error -> Error(list.exception)
                is Success -> {
                    val list = list.data.firstOrNull() { it.id == id }
                        ?: return@map Error(Exception("Not found"))
                    Success(list)
                }
            }
        }

    override suspend fun deleteItem(id: Long): Int {
        tasksServiceData.remove(id)
        refreshHeroes()
        return 1
    }

    override suspend fun saveList(list: List<Hero>) {
        for (next in list) tasksServiceData[next.id!!] = next
    }

    override suspend fun getList(humanType: HumanType?): Result<List<Hero>> = Success(tasksServiceData.values.toList())

    override fun observeList(humanType: HumanType?): LiveData<Result<List<Hero>>> = observableHeroes

    override suspend fun deleteAll(): Int {
        val clearedSize = tasksServiceData.size
        tasksServiceData.clear()
        refreshHeroes()
        return clearedSize
    }


    private suspend fun refreshHeroes() = observableHeroes.postValue(getList())
}