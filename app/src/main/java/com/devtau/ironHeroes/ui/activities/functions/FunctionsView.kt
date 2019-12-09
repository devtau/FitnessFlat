package com.devtau.ironHeroes.ui.activities.functions

import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.ui.StandardView

interface FunctionsView: StandardView {
    fun showExported(trainingsCount: Int, exercisesCount: Int)
    fun showReadFromFile(trainingsCount: Int, exercisesCount: Int)
    fun provideMockExercises(): List<Exercise>
    fun provideMockMuscleGroups(): List<MuscleGroup>
}