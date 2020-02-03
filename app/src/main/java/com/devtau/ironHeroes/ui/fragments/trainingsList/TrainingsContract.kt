package com.devtau.ironHeroes.ui.fragments.trainingsList

import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.ui.StandardView

interface TrainingsContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
        fun provideTrainings(): List<Training>?
        fun filterAndUpdateList(championIndex: Int, heroIndex: Int)
        fun isChampionFilterNeeded(): Boolean
        fun isHeroFilterNeeded(): Boolean
    }

    interface View: StandardView {
        fun updateTrainings(list: List<Training>?): Unit?
        fun showChampions(list: List<String>?, selectedIndex: Int)
        fun showHeroes(list: List<String>?, selectedIndex: Int)
    }
}