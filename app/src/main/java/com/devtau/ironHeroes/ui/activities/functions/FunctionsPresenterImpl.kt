package com.devtau.ironHeroes.ui.activities.functions

import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Consumer

class FunctionsPresenterImpl(
    private val view: FunctionsView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager
): DBSubscriber(), FunctionsPresenter {

    init {
        disposeOnStop(dataLayer.getHeroes(Consumer { heroes ->
            if (heroes == null || heroes.isEmpty()) {
                dataLayer.updateMuscleGroups(view.provideMockMuscleGroups())
                dataLayer.updateExercises(view.provideMockExercises())
                dataLayer.updateHeroes(Hero.getMockChampions())
                dataLayer.updateHeroes(Hero.getMockHeroes())

                if (BuildConfig.DEBUG) {
                    dataLayer.updateTrainings(Training.getMock())
                    dataLayer.updateExercisesInTraining(ExerciseInTraining.getMock())
                }
            }
        }))
    }


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {

    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "LauncherPresenter"
    }
}