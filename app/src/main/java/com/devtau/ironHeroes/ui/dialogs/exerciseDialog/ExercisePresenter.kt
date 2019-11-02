package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining

interface ExercisePresenter {
    fun onStop()
    fun restartLoaders()
    fun updateExercise(exerciseId: Long?, weight: String?, count: String?)
    fun provideExercises(): List<Exercise>?
}