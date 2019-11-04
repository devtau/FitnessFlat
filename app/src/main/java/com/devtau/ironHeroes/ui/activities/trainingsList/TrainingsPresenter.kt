package com.devtau.ironHeroes.ui.activities.trainingsList

import com.devtau.ironHeroes.data.model.Training

interface TrainingsPresenter {
    fun onStop()
    fun restartLoaders()
    fun provideTrainings(): List<Training>?
    fun filterAndUpdateList(championIndex: Int, heroIndex: Int)
}