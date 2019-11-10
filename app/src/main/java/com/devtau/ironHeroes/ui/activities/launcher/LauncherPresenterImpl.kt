package com.devtau.ironHeroes.ui.activities.launcher

import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Consumer

class LauncherPresenterImpl(
    private val view: LauncherView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager
): DBSubscriber(), LauncherPresenter {

    init {
        dataLayer.getHeroes(Consumer {
            if (it?.isEmpty() == true) {
                dataLayer.updateMuscleGroups(MuscleGroup.getMock())
                dataLayer.updateExercises(Exercise.getMock())

                if (BuildConfig.DEBUG) {
                    dataLayer.updateHeroes(Hero.getMockChampions())
                    dataLayer.updateHeroes(Hero.getMockHeroes())
                    dataLayer.updateTrainings(Training.getMock())
                    dataLayer.updateExercisesInTraining(ExerciseInTraining.getMock())
                }
            }
        })
    }


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {

    }
    //</editor-fold>
}