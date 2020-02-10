package com.devtau.ironHeroes.ui.fragments.functions

import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.ui.StandardView

interface FunctionsContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
    }

    interface View: StandardView {
        fun showExported(trainingsCount: Int, exercisesCount: Int)
        fun showReadFromFile(trainingsCount: Int, exercisesCount: Int)
        fun provideMockHeroes(): List<Hero>?
        fun provideMockChampions(): List<Hero>?
        fun provideMockExercises(): List<Exercise>?
        fun provideMockMuscleGroups(): List<MuscleGroup>?
        fun provideMockTrainings(): List<Training>?
        fun provideMockExercisesInTrainings(): List<ExerciseInTraining>?
        fun turnPage(pageIndex: Int)
    }
}