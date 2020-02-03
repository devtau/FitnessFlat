package com.devtau.ironHeroes.ui.fragments.other

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.ui.StandardView

interface OtherContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
        fun exportToFile()
        fun importFromFile()
        fun clearDB()
    }

    interface View: StandardView {
        fun showExported(trainingsCount: Int, exercisesCount: Int)
        fun showReadFromFile(trainingsCount: Int, exercisesCount: Int)
        fun provideMockHeroes(): List<Hero>
    }
}