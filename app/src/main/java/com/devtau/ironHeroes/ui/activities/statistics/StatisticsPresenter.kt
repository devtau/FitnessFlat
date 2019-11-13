package com.devtau.ironHeroes.ui.activities.statistics

interface StatisticsPresenter {
    fun onStop()
    fun restartLoaders()
    fun filterAndUpdateChart(muscleGroupIndex: Int, exerciseIndex: Int)
}