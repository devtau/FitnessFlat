package com.devtau.ironHeroes.ui.activities.trainingDetails

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.ui.StandardView
import java.util.*

interface TrainingDetailsView: StandardView {
    fun showScreenTitle(newTraining: Boolean)
    fun showBirthdayNA()
    fun showTrainingDetails(training: Training?)
    fun showChampions(list: List<Hero>?, selectedChampionId: Long)
    fun showHeroes(list: List<Hero>?, selectedHeroId: Long)
    fun onDateSet(date: Calendar)
    fun showDeleteTrainingBtn(show: Boolean)
    fun closeScreen()
}