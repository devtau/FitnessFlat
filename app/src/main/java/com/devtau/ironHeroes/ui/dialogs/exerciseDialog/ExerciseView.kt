package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.ui.StandardView

interface ExerciseView: StandardView {
    fun showMuscleGroups(list: List<String>?, selectedIndex: Int)
    fun showExercises(list: List<String>?, selectedIndex: Int): Unit?
    fun showExerciseDetails(exercise: ExerciseInTraining?)
}