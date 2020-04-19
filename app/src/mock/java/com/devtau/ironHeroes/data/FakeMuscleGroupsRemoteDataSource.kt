package com.devtau.ironHeroes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.devtau.ironHeroes.data.Result.*
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.data.source.local.muscleGroup.MuscleGroupLocalDataSource
import java.util.*

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeMuscleGroupsRemoteDataSource: MuscleGroupLocalDataSource {

    private val serviceData = LinkedHashMap<Long, MuscleGroup>()
    private val observableItems = MutableLiveData<Result<List<MuscleGroup>>>()

    override suspend fun saveItem(item: MuscleGroup): Long {
        serviceData[item.id!!] = item
        return 1
    }

    override suspend fun getItem(id: Long?): Result<MuscleGroup?> {
        serviceData[id]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find hero"))
    }

    override fun observeItem(id: Long?): LiveData<Result<MuscleGroup?>> =
        observableItems.map { list ->
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

    override suspend fun deleteItem(item: MuscleGroup): Int {
        serviceData.remove(item.id)
        refreshMuscleGroups()
        return 1
    }

    override suspend fun saveList(list: List<MuscleGroup>) {
        for (next in list) serviceData[next.id!!] = next
    }

    override suspend fun getList() = Success(serviceData.values.toList())

    override fun observeList() = observableItems

    override suspend fun deleteAll(): Int {
        val clearedSize = serviceData.size
        serviceData.clear()
        refreshMuscleGroups()
        return clearedSize
    }


    private suspend fun refreshMuscleGroups() = observableItems.postValue(getList())
}