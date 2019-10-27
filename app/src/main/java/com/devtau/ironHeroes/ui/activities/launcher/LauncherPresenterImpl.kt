package com.devtau.ironHeroes.ui.activities.launcher

import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager

class LauncherPresenterImpl(
    private val view: LauncherView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager?
): DBSubscriber(), LauncherPresenter {

    init {
        dataLayer.updateHeroes(Hero.getMockChampions())
        dataLayer.updateHeroes(Hero.getMockHeroes())
        dataLayer.updateTrainings(Training.getMock())
        dataLayer.updateExercises(Exercise.getMock())
        dataLayer.updateMuscleGroups(MuscleGroup.getMock())
    }


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {

    }
    //</editor-fold>
}