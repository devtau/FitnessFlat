package com.devtau.ironHeroes.ui.activities.trainingDetails

import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.HourMinute
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
    fun provideTrainingId(): Long?
    fun roundMinutesInHalfHourIntervals(hour: Int, minute: Int): HourMinute
}