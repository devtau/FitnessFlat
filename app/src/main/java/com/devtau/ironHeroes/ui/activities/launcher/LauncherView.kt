package com.devtau.ironHeroes.ui.activities.launcher

import com.devtau.ironHeroes.ui.StandardView

interface LauncherView: StandardView {
    fun showExported(trainingsCount: Int, exercisesCount: Int)
    fun showReadFromFile(trainingsCount: Int, exercisesCount: Int)
}