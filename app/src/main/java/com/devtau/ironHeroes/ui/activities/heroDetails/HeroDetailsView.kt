package com.devtau.ironHeroes.ui.activities.heroDetails

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.ui.StandardView
import java.util.*

interface HeroDetailsView: StandardView {
    fun showScreenTitle(newHero: Boolean)
    fun showBirthdayNA()
    fun showHeroDetails(hero: Hero?)
    fun onDateSet(date: Calendar)
    fun showDeleteHeroBtn(show: Boolean)
    fun closeScreen()
}