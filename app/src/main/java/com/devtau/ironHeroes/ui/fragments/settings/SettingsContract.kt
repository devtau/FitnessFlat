package com.devtau.ironHeroes.ui.fragments.settings

import com.devtau.ironHeroes.ui.StandardView

interface SettingsContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
        fun showChampionFilterClicked(checked: Boolean)
        fun showHeroFilterClicked(checked: Boolean)
        fun openEditDialogFromStatisticsClicked(checked: Boolean)

        fun isChampionFilterNeeded(): Boolean
        fun isHeroFilterNeeded(): Boolean
        fun isEditDialogNeeded(): Boolean
    }

    interface View: StandardView
}