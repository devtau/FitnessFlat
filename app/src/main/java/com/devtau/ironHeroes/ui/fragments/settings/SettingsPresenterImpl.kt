package com.devtau.ironHeroes.ui.fragments.settings

import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager

class SettingsPresenterImpl(
    private val view: SettingsView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager
): DBSubscriber(), SettingsPresenter {


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {

    }

    override fun showChampionFilterClicked(checked: Boolean) {
        prefs.showChampionFilter = checked
        if (!checked) prefs.favoriteChampionId = null
    }

    override fun showHeroFilterClicked(checked: Boolean) {
        prefs.showHeroFilter = checked
        if (!checked) prefs.favoriteHeroId = null
    }

    override fun isChampionFilterNeeded() = prefs.showChampionFilter
    override fun isHeroFilterNeeded() = prefs.showHeroFilter
    //</editor-fold>

    //<editor-fold desc="Private methods">

    //</editor-fold>


    companion object {
        private const val LOG_TAG = "SettingsPresenter"
    }
}