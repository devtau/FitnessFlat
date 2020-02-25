package com.devtau.ironHeroes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.Result.*
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSource
import java.util.LinkedHashMap
/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeTrainingsRemoteDataSource: TrainingsLocalDataSource {

    private val tasksServiceData = LinkedHashMap<Long, Training>()
    private val observableTrainings = MutableLiveData<Result<List<Training>>>()

    override suspend fun saveItem(item: Training): Long {
        tasksServiceData[item.id!!] = item
        return 1
    }

    override suspend fun getItem(id: Long): Result<Training> {
        tasksServiceData[id]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find training"))
    }

    override fun observeItem(id: Long): LiveData<Result<Training>> =
        observableTrainings.map { list ->
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
        refreshTrainings()
        return 1
    }

    override suspend fun saveList(list: List<Training>) {
        for (next in list) tasksServiceData[next.id!!] = next
    }

    override suspend fun getList(): Result<List<Training>> = Success(tasksServiceData.values.toList())

    override fun observeList(): LiveData<Result<List<Training>>> = observableTrainings

    override suspend fun deleteAll(): Int {
        val clearedSize = tasksServiceData.size
        tasksServiceData.clear()
        refreshTrainings()
        return clearedSize
    }


    private suspend fun refreshTrainings() = observableTrainings.postValue(getList())
}