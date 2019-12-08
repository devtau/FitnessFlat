package com.devtau.ironHeroes.ui.activities.launcher

interface LauncherPresenter {
    fun onStop()
    fun restartLoaders()
    fun exportToFile()
    fun importFromFile()
}