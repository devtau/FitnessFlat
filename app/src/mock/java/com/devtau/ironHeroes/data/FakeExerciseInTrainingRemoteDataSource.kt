package com.devtau.ironHeroes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.devtau.ironHeroes.data.Result.*
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.source.local.exerciseInTraining.ExerciseInTrainingLocalDataSource
import java.util.*

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeExerciseInTrainingRemoteDataSource: ExerciseInTrainingLocalDataSource {

    private val serviceData = LinkedHashMap<Long, ExerciseInTraining>()
    private val observableItems = MutableLiveData<Result<List<ExerciseInTraining>>>()


    override suspend fun getListForHero(heroId: Long?): Result<List<ExerciseInTraining>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getListForTraining(trainingId: Long): Result<List<ExerciseInTraining>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeListForHero(heroId: Long?): LiveData<Result<List<ExerciseInTraining>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeListForTraining(trainingId: Long?): LiveData<Result<List<ExerciseInTraining>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteListForTraining(trainingId: Long): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun saveItem(item: ExerciseInTraining): Long {
        serviceData[item.id!!] = item
        return 1
    }

    override suspend fun getItem(id: Long?): Result<ExerciseInTraining> {
        serviceData[id]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find training"))
    }

    override fun observeItem(id: Long?): LiveData<Result<ExerciseInTraining?>> =
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

    override suspend fun deleteItem(item: ExerciseInTraining): Int {
        serviceData.remove(item.id)
        refreshExercises()
        return 1
    }

    override suspend fun saveList(list: List<ExerciseInTraining>) {
        for (next in list) serviceData[next.id!!] = next
    }

    override suspend fun getList(): Result<List<ExerciseInTraining>> = Success(serviceData.values.toList())

    override fun observeList(): LiveData<Result<List<ExerciseInTraining>>> = observableItems

    override suspend fun deleteAll(): Int {
        val clearedSize = serviceData.size
        serviceData.clear()
        refreshExercises()
        return clearedSize
    }


    private suspend fun refreshExercises() = observableItems.postValue(getList())
}