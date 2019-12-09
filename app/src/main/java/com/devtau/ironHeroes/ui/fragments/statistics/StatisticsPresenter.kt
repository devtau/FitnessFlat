package com.devtau.ironHeroes.ui.fragments.statistics

interface StatisticsPresenter {
    fun onStop()
    fun restartLoaders()
    fun filterAndUpdateChart(muscleGroupIndex: Int, exerciseIndex: Int)
    fun onBalloonClicked(trainingId: Long?, exerciseInTrainingId: Long?)
}