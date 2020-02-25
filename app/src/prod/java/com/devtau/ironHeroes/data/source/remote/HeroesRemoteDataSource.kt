package com.devtau.ironHeroes.data.source.remote

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.*
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSource
import com.devtau.ironHeroes.enums.HumanType
import kotlinx.coroutines.delay
import java.util.*

/**
 * Implementation of the data source that adds a latency simulating network.
 */
class HeroesRemoteDataSource(context: Context): HeroesLocalDataSource {

    private var heroesOnServer = LinkedHashMap<Long, Hero>(3)
    private val observableHeroes = MutableLiveData<Result<List<Hero>>>()

    init {
        val demoHeroes = Hero.getMockHeroes(context)
        val demoChampions = Hero.getMockChampions(context)
        for (next in demoHeroes) heroesOnServer[next.id!!] = next
        for (next in demoChampions) heroesOnServer[next.id!!] = next
    }


    override suspend fun saveItem(item: Hero): Long {
        heroesOnServer[item.id!!] = item
        return 1
    }

    override suspend fun getItem(id: Long): Result<Hero> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_MS)
        heroesOnServer[id]?.let {
            return Success(it)
        }
        return Error(Exception("Hero not found"))
    }

    override fun observeItem(id: Long) = observableHeroes.map { list ->
        when (list) {
            is Loading -> Loading
            is Error -> Error(list.exception)
            is Success -> {
                val hero = list.data.firstOrNull() { it.id == id }
                    ?: return@map Error(Exception("Not found"))
                Success(hero)
            }
        }
    }

    override suspend fun deleteItem(id: Long): Int {
        heroesOnServer.remove(id)
        return 1
    }


    override suspend fun saveList(list: List<Hero>) {
        for (next in list) heroesOnServer[next.id!!] = next
    }

    override suspend fun getList(humanType: HumanType?): Result<List<Hero>> {
        // Simulate network by delaying the execution.
        val list = heroesOnServer.values.toList()
        delay(SERVICE_LATENCY_MS)
        return Success(list)
    }

    override fun observeList(humanType: HumanType?) = observableHeroes

    override suspend fun deleteAll(): Int {
        val size = heroesOnServer.size
        heroesOnServer.clear()
        return size
    }


    companion object {
        private const val SERVICE_LATENCY_MS = 2000L
    }
}