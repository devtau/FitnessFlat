package com.devtau.ironHeroes.ui.activities.trainingsList

import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.ui.StandardView

interface TrainingsView: StandardView {
    fun updateTrainings(list: List<Training>?): Unit?
    fun showChampions(list: List<String>?, selectedIndex: Int)
    fun showHeroes(list: List<String>?, selectedIndex: Int)
}