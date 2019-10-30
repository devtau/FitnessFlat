package com.devtau.ironHeroes.ui.activities.trainingsList

import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Consumer

class TrainingsPresenterImpl(
    private val view: TrainingsView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager?
): DBSubscriber(), TrainingsPresenter {

    var trainings: List<Training>? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(dataLayer.getTrainings(Consumer {
            trainings = it
            view.updateTrainings(it)
        }))
    }

    override fun provideTrainings() = trainings
    //</editor-fold>
}