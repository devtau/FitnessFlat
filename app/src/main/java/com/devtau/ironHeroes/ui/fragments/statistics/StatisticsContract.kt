package com.devtau.ironHeroes.ui.fragments.statistics

import com.devtau.ironHeroes.ui.StandardView
import com.github.mikephil.charting.data.LineData
import java.util.*

interface StatisticsContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
        fun filterAndUpdateChart(muscleGroupIndex: Int, exerciseIndex: Int)
        fun onBalloonClicked(trainingId: Long?, exerciseInTrainingId: Long?)
    }

    interface View: StandardView {
        fun showMuscleGroups(list: List<String>?, selectedIndex: Int)
        fun showExercises(list: List<String>?, selectedIndex: Int)
        fun showStatisticsData(lineData: LineData?, xLabels: List<Calendar>, xLabelsCount: Int)
        fun showExerciseDetails(heroId: Long?, trainingId: Long?, exerciseInTrainingId: Long?)
    }
}