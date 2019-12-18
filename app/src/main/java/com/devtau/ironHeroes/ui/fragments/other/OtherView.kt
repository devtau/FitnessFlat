package com.devtau.ironHeroes.ui.fragments.other

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.ui.StandardView

interface OtherView: StandardView {
    fun showExported(trainingsCount: Int, exercisesCount: Int)
    fun showReadFromFile(trainingsCount: Int, exercisesCount: Int)
    fun provideMockHeroes(): List<Hero>
}