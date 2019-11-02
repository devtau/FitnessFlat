package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.ui.StandardView

interface ExerciseView: StandardView {
    fun showExerciseDetails(exerciseInTraining: ExerciseInTraining?, exercises: List<Exercise>?)
}