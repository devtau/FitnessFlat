package com.devtau.ironHeroes.ui.activities.functions

import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class FunctionsPresenterImpl(
    private val view: FunctionsContract.View,
    private val dataLayer: DataLayer,
    private val prefs: PreferencesManager
): DBSubscriber(), FunctionsContract.Presenter {

    init {
        disposeOnStop(dataLayer.getHeroes(Consumer { heroes ->
            if (prefs.firstLaunch && (heroes == null || heroes.isEmpty())) {
                view.showMsg(R.string.load_demo_configuration, Action {
                    prefs.firstLaunch = false
                    loadDemoConfig()
                }, Action {
                    prefs.firstLaunch = false
                })
            }
        }))
    }


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {

    }
    //</editor-fold>


    private fun loadDemoConfig() {
        dataLayer.updateMuscleGroups(view.provideMockMuscleGroups())
        dataLayer.updateExercises(view.provideMockExercises())
        dataLayer.updateHeroes(view.provideMockHeroes())
        dataLayer.updateHeroes(view.provideMockChampions())
        dataLayer.updateTrainings(view.provideMockTrainings())
        dataLayer.updateExercisesInTraining(view.provideMockExercisesInTrainings())
    }


    companion object {
        private const val LOG_TAG = "FunctionsPresenter"
    }
}