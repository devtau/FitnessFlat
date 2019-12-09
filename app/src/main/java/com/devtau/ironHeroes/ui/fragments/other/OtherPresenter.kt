package com.devtau.ironHeroes.ui.fragments.other

interface OtherPresenter {
    fun onStop()
    fun restartLoaders()
    fun exportToFile()
    fun importFromFile()
}