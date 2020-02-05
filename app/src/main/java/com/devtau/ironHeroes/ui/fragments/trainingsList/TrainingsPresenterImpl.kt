package com.devtau.ironHeroes.ui.fragments.trainingsList

import com.devtau.ironHeroes.data.dao.*
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.relations.TrainingRelation
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Consumer

class TrainingsPresenterImpl(
    private val view: TrainingsContract.View,
    private val heroDao: HeroDao,
    private val trainingDao: TrainingDao,
    private val prefs: PreferencesManager
): DBSubscriber(), TrainingsContract.Presenter {

    private var trainings: List<Training>? = null
    private var champions: List<Hero>? = null
    private var heroes: List<Hero>? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(trainingDao.getList()
            .map { TrainingRelation.convertList(it) }
            .subscribeDefault(Consumer {
                trainings = it
                publishDataToView()
            }, "trainingDao.getList"))

        disposeOnStop(heroDao.getList(HumanType.CHAMPION.ordinal)
            .subscribeDefault(Consumer {
                champions = it
                publishDataToView()
            }, "heroDao.getList"))

        disposeOnStop(heroDao.getList(HumanType.HERO.ordinal)
            .subscribeDefault(Consumer {
                heroes = it
                publishDataToView()
            }, "heroDao.getList"))
    }

    override fun provideTrainings() = trainings

    override fun filterAndUpdateList(championIndex: Int, heroIndex: Int) {
        val championId = if (championIndex == 0) null else champions?.get(championIndex - 1)?.id
        val heroId = if (heroIndex == 0) null else heroes?.get(heroIndex - 1)?.id

        if (prefs.favoriteChampionId == championId && prefs.favoriteHeroId == heroId) return
        prefs.favoriteChampionId = championId
        prefs.favoriteHeroId = heroId
        view.updateTrainings(filter(trainings, championId, heroId))
        Logger.d(LOG_TAG, "filterAndUpdateList. " +
                "trainings size=${trainings?.size}, " +
                "champions size=${champions?.size}, " +
                "heroes size=${heroes?.size}" +
                "championId=$championId" +
                "heroId=$heroId")
    }

    override fun isChampionFilterNeeded() = prefs.showChampionFilter
    override fun isHeroFilterNeeded() = prefs.showHeroFilter
    //</editor-fold>

    private fun getSpinnerStrings(list: List<Hero>?): List<String> {
        val spinnerStrings = ArrayList<String>()
        spinnerStrings.add("- -")
        if (list != null) for (next in list) spinnerStrings.add(next.getName())
        return spinnerStrings
    }

    private fun getSelectedItemIndex(list: List<Hero>?, selectedId: Long?): Int {
        var index = 0
        if (list != null)
            for ((i, next) in list.withIndex())
                if (next.id == selectedId)
                    index = i + 1
        Logger.d(LOG_TAG, "getSelectedItemIndex. list size=${list?.size}, selectedId=$selectedId, index=$index")
        return index
    }

    private fun publishDataToView() {
        if (trainings == null || AppUtils.isEmpty(champions) || AppUtils.isEmpty(heroes)) return
        view.updateTrainings(filter(trainings, prefs.favoriteChampionId, prefs.favoriteHeroId))

        view.showChampions(getSpinnerStrings(champions), getSelectedItemIndex(champions, prefs.favoriteChampionId))
        view.showHeroes(getSpinnerStrings(heroes), getSelectedItemIndex(heroes, prefs.favoriteHeroId))
        Logger.d(LOG_TAG, "publishDataToView. " +
                "trainings size=${trainings?.size}, " +
                "champions size=${champions?.size}, " +
                "heroes size=${heroes?.size}")
    }

    private fun filter(list: List<Training>?, championId: Long?, heroId: Long?): List<Training> {
        val filtered = ArrayList<Training>()
        if (list != null) for (next in list)
            if ((championId == null || next.championId == championId)
                && (heroId == null || next.heroId == heroId)) filtered.add(next)
        Logger.d(LOG_TAG, "filter. list size=${list?.size}, championId=$championId, heroId=$heroId, filtered size=${filtered.size}")
        return filtered
    }


    companion object {
        private const val LOG_TAG = "TrainingsPresenter"
    }
}