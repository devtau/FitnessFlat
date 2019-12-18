package com.devtau.ironHeroes.ui.fragments.settings

interface SettingsPresenter {
    fun onStop()
    fun restartLoaders()
    fun showChampionFilterClicked(checked: Boolean)
    fun showHeroFilterClicked(checked: Boolean)
    fun openEditDialogFromStatisticsClicked(checked: Boolean)

    fun isChampionFilterNeeded(): Boolean
    fun isHeroFilterNeeded(): Boolean
    fun isEditDialogNeeded(): Boolean
}