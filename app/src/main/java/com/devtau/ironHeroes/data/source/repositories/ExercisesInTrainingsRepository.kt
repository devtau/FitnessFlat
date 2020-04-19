package com.devtau.ironHeroes.data.source.repositories

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.ExerciseInTraining

interface ExercisesInTrainingsRepository: BaseRepository<ExerciseInTraining> {

    fun observeListForTraining(trainingId: Long?): LiveData<Result<List<ExerciseInTraining>>>
    fun observeListForHero(heroId: Long?): LiveData<Result<List<ExerciseInTraining>>>
    suspend fun getListForHero(heroId: Long?): Result<List<ExerciseInTraining>>
    suspend fun deleteListForTraining(trainingId: Long)
}