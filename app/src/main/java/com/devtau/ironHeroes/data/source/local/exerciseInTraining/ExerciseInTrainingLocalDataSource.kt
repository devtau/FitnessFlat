package com.devtau.ironHeroes.data.source.local.exerciseInTraining

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.source.local.BaseLocalDataSource

interface ExerciseInTrainingLocalDataSource: BaseLocalDataSource<ExerciseInTraining> {

    suspend fun getListForHero(heroId: Long?): Result<List<ExerciseInTraining>>
    suspend fun getListForTraining(trainingId: Long): Result<List<ExerciseInTraining>>
    fun observeListForHero(heroId: Long?): LiveData<Result<List<ExerciseInTraining>>>
    fun observeListForTraining(trainingId: Long?): LiveData<Result<List<ExerciseInTraining>>>
    suspend fun deleteListForTraining(trainingId: Long): Int
}