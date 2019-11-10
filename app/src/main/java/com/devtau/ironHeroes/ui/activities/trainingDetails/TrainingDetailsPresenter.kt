package com.devtau.ironHeroes.ui.activities.trainingDetails

import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.HourMinute
import com.devtau.ironHeroes.data.model.Training
import io.reactivex.functions.Action
import java.util.*

interface TrainingDetailsPresenter {
    fun onStop()
    fun restartLoaders()
    fun updateTrainingData(championIndex: Int, heroIndex: Int, date: Calendar?)
    fun dateDialogRequested(tempDate: Calendar?)
    fun onBackPressed(action: Action)
    fun deleteTraining()
    fun provideExercises(): List<ExerciseInTraining>?
    fun provideTraining(): Training?
}