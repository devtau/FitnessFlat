package com.devtau.ironHeroes.ui.fragments.trainingsList

import com.devtau.ironHeroes.data.dao.HeroDao
import com.devtau.ironHeroes.data.dao.TrainingDao
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import com.devtau.ironHeroes.util.SpinnerUtils
import io.reactivex.functions.Consumer

class TrainingsPresenterImpl(
    private val view: TrainingsContract.View,
    private val heroDao: HeroDao,
    private val trainingDao: TrainingDao,
    private val prefs: PreferencesManager
): DBSubscriber(), TrainingsContract.Presenter {

    private var trainings = mutableListOf<Training>()
    private var champions = mutableListOf<Hero>()
    private var heroes = mutableListOf<Hero>()


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {
        disposeOnStop(trainingDao.getList()
            .map { relation -> relation.map { it.convert() } }
            .subscribeDefault(Consumer {
                trainings.clear()
                trainings.addAll(it)
                publishDataToView()
            }, "trainingDao.getList"))

        disposeOnStop(heroDao.getList(HumanType.CHAMPION.ordinal)
            .subscribeDefault(Consumer {
                champions.clear()
                champions.addAll(it)
                publishDataToView()
            }, "heroDao.getList"))

        disposeOnStop(heroDao.getList(HumanType.HERO.ordinal)
            .subscribeDefault(Consumer {
                heroes.clear()
                heroes.addAll(it)
                publishDataToView()
            }, "heroDao.getList"))
    }

    override fun filterAndUpdateList(championIndex: Int, heroIndex: Int) {
        val championId = if (championIndex == 0) null else champions[championIndex - 1].id
        val heroId = if (heroIndex == 0) null else heroes[heroIndex - 1].id

        if (prefs.favoriteChampionId == championId && prefs.favoriteHeroId == heroId) return
        prefs.favoriteChampionId = championId
        prefs.favoriteHeroId = heroId
        view.updateTrainings(filter(trainings, championId, heroId))
        Logger.d(LOG_TAG, "filterAndUpdateList. " +
                "trainings size=${trainings.size}, " +
                "champions size=${champions.size}, " +
                "heroes size=${heroes.size}" +
                "championId=$championId" +
                "heroId=$heroId")
    }

    override fun isChampionFilterNeeded() = prefs.showChampionFilter
    override fun isHeroFilterNeeded() = prefs.showHeroFilter
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun publishDataToView() {
        if (trainings.isEmpty() || champions.isEmpty() || heroes.isEmpty()) return
        view.updateTrainings(filter(trainings, prefs.favoriteChampionId, prefs.favoriteHeroId))

        view.showChampions(
            SpinnerUtils.getHeroesSpinnerStrings(champions, withEmptyString = true),
            SpinnerUtils.getSelectedHeroIndex(champions, prefs.favoriteChampionId)
        )

        view.showHeroes(
            SpinnerUtils.getHeroesSpinnerStrings(heroes, withEmptyString = true),
            SpinnerUtils.getSelectedHeroIndex(heroes, prefs.favoriteHeroId)
        )

        Logger.d(LOG_TAG, "publishDataToView. " +
                "trainings size=${trainings.size}, " +
                "champions size=${champions.size}, " +
                "heroes size=${heroes.size}")
    }

    private fun filter(list: List<Training>, championId: Long?, heroId: Long?): List<Training> {
        val filtered = ArrayList<Training>()
        for (next in list)
            if ((championId == null || next.championId == championId)
                && (heroId == null || next.heroId == heroId)) filtered.add(next)
        Logger.d(LOG_TAG, "filter. list size=${list.size}, " +
                "championId=$championId, heroId=$heroId, filtered size=${filtered.size}")
        return filtered
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "TrainingsPresenter"
    }
}