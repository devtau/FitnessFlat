package com.devtau.ironHeroes.ui.activities.heroesList

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.StandardView

interface HeroesContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
        fun provideHumanType(): HumanType
        fun provideHeroes(): List<Hero>?
    }

    interface View: StandardView {
        fun updateHeroes(list: List<Hero>?): Unit?
    }
}