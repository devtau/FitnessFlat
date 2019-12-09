package com.devtau.ironHeroes.ui.fragments.trainingsList

import com.devtau.ironHeroes.data.model.Training

interface TrainingsPresenter {
    fun onStop()
    fun restartLoaders()
    fun provideTrainings(): List<Training>?
    fun filterAndUpdateList(championIndex: Int, heroIndex: Int)
    fun isChampionFilterNeeded(): Boolean
    fun isHeroFilterNeeded(): Boolean
}