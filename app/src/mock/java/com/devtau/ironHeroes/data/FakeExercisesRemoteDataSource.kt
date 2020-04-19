package com.devtau.ironHeroes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.devtau.ironHeroes.data.Result.*
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.source.local.exercise.ExerciseLocalDataSource
import java.util.*

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeExercisesRemoteDataSource: ExerciseLocalDataSource {

    private val serviceData = LinkedHashMap<Long, Exercise>()
    private val observableExercises = MutableLiveData<Result<List<Exercise>>>()

    override suspend fun saveItem(item: Exercise): Long {
        serviceData[item.id!!] = item
        return 1
    }

    override suspend fun getItem(id: Long?): Result<Exercise?> {
        serviceData[id]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find hero"))
    }

    override fun observeItem(id: Long?): LiveData<Result<Exercise?>> =
        observableExercises.map { list ->
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

    override suspend fun deleteItem(item: Exercise): Int {
        serviceData.remove(item.id)
        refreshExercises()
        return 1
    }

    override suspend fun saveList(list: List<Exercise>) {
        for (next in list) serviceData[next.id!!] = next
    }

    override suspend fun getList() = Success(serviceData.values.toList())

    override fun observeList() = observableExercises

    override suspend fun deleteAll(): Int {
        val clearedSize = serviceData.size
        serviceData.clear()
        refreshExercises()
        return clearedSize
    }


    private suspend fun refreshExercises() = observableExercises.postValue(getList())
}