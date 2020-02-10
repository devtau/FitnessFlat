package com.devtau.ironHeroes.ui.fragments.heroesList

import com.devtau.ironHeroes.data.dao.HeroDao
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DBSubscriber
import io.reactivex.functions.Consumer

class HeroesPresenterImpl(
    private val view: HeroesContract.View,
    private val heroDao: HeroDao,
    private val humanType: HumanType
): DBSubscriber(), HeroesContract.Presenter {

    var heroes = arrayListOf<Hero>()


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {
        disposeOnStop(heroDao.getList(humanType.ordinal)
            .subscribeDefault(Consumer {
                heroes.clear()
                heroes.addAll(it)
                view.updateHeroes(heroes)
            }, "heroDao.getList"))
    }

    override fun provideHumanType() = humanType

    override fun provideHeroes() = heroes
    //</editor-fold>
}