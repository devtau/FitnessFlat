package com.devtau.ironHeroes.ui.activities.heroesList

import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Consumer

class HeroesPresenterImpl(
    private val view: HeroesView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager,
    private val humanType: HumanType
): DBSubscriber(), HeroesPresenter {

    var heroes: List<Hero>? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(when (humanType) {
            HumanType.HERO -> dataLayer.getHeroes(Consumer {
                heroes = it
                view.updateHeroes(it)
            })
            HumanType.CHAMPION -> dataLayer.getChampions(Consumer {
                heroes = it
                view.updateHeroes(it)
            })
        })
    }

    override fun provideHumanType() = humanType

    override fun provideHeroes() = heroes
    //</editor-fold>
}