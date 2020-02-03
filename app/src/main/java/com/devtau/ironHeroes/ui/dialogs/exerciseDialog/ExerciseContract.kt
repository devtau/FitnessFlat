package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.ui.StandardView

interface ExerciseContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
        fun updateExerciseData(exerciseIndex: Int, weight: String?, repeats: String?, count: String?, comment: String?)
        fun deleteExercise()
        fun provideExercises(): List<Exercise>?
        fun filterAndUpdateList(muscleGroupIndex: Int)
        fun updatePreviousExerciseData(exerciseIndex: Int)
    }

    interface View: StandardView {
        fun showMuscleGroups(list: List<String>?, selectedIndex: Int)
        fun showExercises(list: List<String>?, selectedIndex: Int): Unit?
        fun showExerciseDetails(weight: Int?, repeats: Int?, count: Int?, comment: String?)
        fun showPreviousExerciseData(date: Long?, weight: Int?, repeats: Int?, count: Int?)
    }
}