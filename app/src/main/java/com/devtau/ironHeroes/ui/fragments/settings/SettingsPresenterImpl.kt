package com.devtau.ironHeroes.ui.fragments.settings

import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager

class SettingsPresenterImpl(
    private val view: SettingsContract.View,
    private val prefs: PreferencesManager
): DBSubscriber(), SettingsContract.Presenter {


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {

    }

    override fun showChampionFilterClicked(checked: Boolean) {
        prefs.showChampionFilter = checked
    }

    override fun showHeroFilterClicked(checked: Boolean) {
        prefs.showHeroFilter = checked
    }

    override fun openEditDialogFromStatisticsClicked(checked: Boolean) {
        prefs.openEditDialogFromStatistics = checked
    }

    override fun isChampionFilterNeeded() = prefs.showChampionFilter
    override fun isHeroFilterNeeded() = prefs.showHeroFilter
    override fun isEditDialogNeeded() = prefs.openEditDialogFromStatistics
    //</editor-fold>

    //<editor-fold desc="Private methods">

    //</editor-fold>


    companion object {
        private const val LOG_TAG = "SettingsPresenter"
    }
}