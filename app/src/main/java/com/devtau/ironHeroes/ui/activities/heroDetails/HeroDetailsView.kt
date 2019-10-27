package com.devtau.ironHeroes.ui.activities.heroDetails

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.StandardView
import java.util.*

interface HeroDetailsView: StandardView {
    fun showScreenTitle(newHero: Boolean, humanType: HumanType)
    fun showBirthdayNA()
    fun showHeroDetails(hero: Hero?)
    fun onDateSet(date: Calendar)
    fun showDeleteHeroBtn(show: Boolean)
    fun showHumanType(humanType: HumanType)
    fun closeScreen()
}