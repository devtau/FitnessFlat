package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import com.devtau.ironHeroes.data.model.Exercise

interface ExercisePresenter {
    fun onStop()
    fun restartLoaders()
    fun updateExerciseData(exerciseIndex: Int, weight: String?, count: String?)
    fun deleteExercise()
    fun provideExercises(): List<Exercise>?
    fun filterAndUpdateList(muscleGroupIndex: Int)
    fun updatePreviousExerciseData(exerciseIndex: Int)
}