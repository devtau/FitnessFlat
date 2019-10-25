package com.devtau.ironHeroes.ui

import com.devtau.ironHeroes.data.DataLayerImpl
import com.devtau.ironHeroes.rest.NetworkLayerImpl
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesActivity
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesPresenterImpl
import com.devtau.ironHeroes.ui.activities.heroDetails.HeroDetailsActivity
import com.devtau.ironHeroes.ui.activities.heroDetails.HeroDetailsPresenterImpl
import com.devtau.ironHeroes.util.PreferencesManager

class DependencyRegistry {

    fun inject(activity: HeroesActivity) {
        val dataLayer = DataLayerImpl(activity)
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager.getInstance(activity)
        activity.presenter = HeroesPresenterImpl(
            activity,
            dataLayer,
            networkLayer,
            prefs
        )
    }

    fun inject(activity: HeroDetailsActivity) {
        val dataLayer = DataLayerImpl(activity)
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager.getInstance(activity)
        val heroId = activity.intent?.extras?.getLong(HeroDetailsActivity.HERO_ID)
        activity.presenter = HeroDetailsPresenterImpl(
            activity,
            dataLayer,
            networkLayer,
            prefs,
            heroId
        )
    }
}