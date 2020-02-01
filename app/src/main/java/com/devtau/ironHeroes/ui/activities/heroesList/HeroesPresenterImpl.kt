package com.devtau.ironHeroes.ui.activities.heroesList

import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DBSubscriber
import io.reactivex.functions.Consumer

class HeroesPresenterImpl(
    private val view: HeroesContract.View,
    private val dataLayer: DataLayer,
    private val humanType: HumanType
): DBSubscriber(), HeroesContract.Presenter {

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