package com.devtau.ironHeroes.ui.fragments.trainingDetails

import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.ui.StandardView
import io.reactivex.functions.Action
import java.util.*

interface TrainingDetailsContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
        fun updateTrainingData(championIndex: Int, heroIndex: Int, date: Calendar?)
        fun dateDialogRequested(tempDate: Calendar?)
        fun onBackPressed(action: Action)
        fun deleteTraining()
        fun provideExercises(): List<ExerciseInTraining>?
        fun provideTraining(): Training?
        fun onExerciseMoved(fromPosition: Int, toPosition: Int)
        fun addExerciseClicked()
    }

    interface View: StandardView {
        fun showScreenTitle(newTraining: Boolean)
        fun showTrainingDate(date: Calendar)
        fun showExercises(list: List<ExerciseInTraining>?): Unit?
        fun showChampions(list: List<String>?, selectedIndex: Int)
        fun showHeroes(list: List<String>?, selectedIndex: Int)
        fun showDateDialog(date: Calendar, minDate: Calendar, maxDate: Calendar)
        fun closeScreen(): Unit?
        fun showNewExerciseDialog(position: Int)
    }
}