package com.devtau.ironHeroes.ui.activities.heroesList

import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Consumer

class HeroesPresenterImpl(
    private val view: HeroesView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager?
): DBSubscriber(), HeroesPresenter {

    init {
        dataLayer.updateChampions(Champion.getMock())
        dataLayer.updateHeroes(Hero.getMock())
        dataLayer.updateTrainings(Training.getMock())
        dataLayer.updateExercises(Exercise.getMock())
        dataLayer.updateMuscleGroups(MuscleGroup.getMock())
    }

    var heroes: List<Hero>? = null
    var trainings: List<Training>? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(dataLayer.getHeroes(Consumer {
            heroes = it
            view.updateHeroes(it)
        }))
        disposeOnStop(dataLayer.getTrainings(Consumer {
            trainings = it
        }))
    }
    //</editor-fold>
}