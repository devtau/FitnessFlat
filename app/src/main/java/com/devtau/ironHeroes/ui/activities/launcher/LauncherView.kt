package com.devtau.ironHeroes.ui.activities.launcher

import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.ui.StandardView

interface LauncherView: StandardView {
    fun showExported(trainingsCount: Int, exercisesCount: Int)
    fun showReadFromFile(trainingsCount: Int, exercisesCount: Int)
    fun provideMockExercises(): List<Exercise>
    fun provideMockMuscleGroups(): List<MuscleGroup>
}