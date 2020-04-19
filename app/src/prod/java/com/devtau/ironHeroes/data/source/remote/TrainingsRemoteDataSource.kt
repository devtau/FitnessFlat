package com.devtau.ironHeroes.data.source.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.*
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSource
import kotlinx.coroutines.delay
import java.util.*
/**
 * Implementation of the data source that adds a latency simulating network.
 */
class TrainingsRemoteDataSource(context: Context): TrainingsLocalDataSource {

    private var trainingsOnServer = LinkedHashMap<Long, Training>(3)
    private val observableTrainings = MutableLiveData<Result<List<Training>>>()

    init {
        val demoTrainings = Training.getMock(context)
        for (next in demoTrainings) trainingsOnServer[next.id!!] = next
    }


    override suspend fun saveItem(item: Training): Long {
        trainingsOnServer[item.id!!] = item
        return 1
    }

    override suspend fun getItem(id: Long?): Result<Training?> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_MS)
        trainingsOnServer[id]?.let {
            return Success(it)
        }
        return Error(Exception("Training not found"))
    }

    override fun observeItem(id: Long?): LiveData<Result<Training?>> = observableTrainings.map { list ->
        when (list) {
            is Loading -> Loading
            is Error -> Error(list.exception)
            is Success -> {
                val training = list.data.firstOrNull() { it.id == id }
                    ?: return@map Error(Exception("Not found"))
                Success(training)
            }
        }
    }

    override suspend fun deleteItem(item: Training): Int {
        trainingsOnServer.remove(item.id)
        return 1
    }


    override suspend fun saveList(list: List<Training>) {
        for (next in list) trainingsOnServer[next.id!!] = next
    }

    override suspend fun getList(): Result<List<Training>> {
        // Simulate network by delaying the execution.
        val list = trainingsOnServer.values.toList()
        delay(SERVICE_LATENCY_MS)
        return Success(list)
    }

    override fun observeList() = observableTrainings

    override suspend fun deleteAll(): Int {
        val size = trainingsOnServer.size
        trainingsOnServer.clear()
        return size
    }


    companion object {
        private const val SERVICE_LATENCY_MS = 2000L
    }
}