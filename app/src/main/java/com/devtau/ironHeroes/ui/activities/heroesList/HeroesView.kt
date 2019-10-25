package com.devtau.ironHeroes.ui.activities.heroesList

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.ui.StandardView

interface HeroesView: StandardView {
    fun updateHeroes(list: List<Hero>?): Unit?
}