package com.devtau.ironHeroes.ui.fragments.settings

import androidx.lifecycle.ViewModel
import com.devtau.ironHeroes.util.prefs.PreferencesManager
import timber.log.Timber

class SettingsViewModel(
    private val prefs: PreferencesManager
): ViewModel() {

    var showChampionFilter: Boolean
        get() {
            Timber.d("get showChampionFilter=${prefs.showChampionFilter}")
            return prefs.showChampionFilter
        }
        set(value) {
            Timber.d("set showChampionFilter=$value")
            prefs.showChampionFilter = value
        }

    var showHeroFilter: Boolean
        get() {
            Timber.d("get showHeroFilter=${prefs.showHeroFilter}")
            return prefs.showHeroFilter
        }
        set(value) {
            Timber.d("set showHeroFilter=$value")
            prefs.showHeroFilter = value
        }

    var openEditDialogFromStatistics: Boolean
        get() {
            Timber.d("get openEditDialogFromStatistics=${prefs.openEditDialogFromStatistics}")
            return prefs.openEditDialogFromStatistics
        }
        set(value) {
            Timber.d("set openEditDialogFromStatistics=$value")
            prefs.openEditDialogFromStatistics = value
        }
}