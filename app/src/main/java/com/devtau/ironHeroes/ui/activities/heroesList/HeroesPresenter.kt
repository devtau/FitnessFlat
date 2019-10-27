package com.devtau.ironHeroes.ui.activities.heroesList

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType

interface HeroesPresenter {
    fun onStop()
    fun restartLoaders()
    fun provideHumanType(): HumanType
    fun provideHeroes(): List<Hero>?
}