package com.devtau.ironHeroes.ui.activities.trainingDetails

import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.ui.StandardView
import java.util.*

interface TrainingDetailsView: StandardView {
    fun showScreenTitle(newTraining: Boolean)
    fun showTrainingDate(date: Calendar)
    fun showExercises(list: List<ExerciseInTraining>?): Unit?
    fun showChampions(list: List<String>?, selectedIndex: Int)
    fun showHeroes(list: List<String>?, selectedIndex: Int)
    fun showDateDialog(date: Calendar, minDate: Calendar, maxDate: Calendar)
    fun showDeleteTrainingBtn(show: Boolean)
    fun closeScreen()
}