package com.devtau.ironHeroes.ui.activities.functions

import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.dao.*
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class FunctionsPresenterImpl(
    private val view: FunctionsContract.View,
    private val heroDao: HeroDao,
    private val trainingDao: TrainingDao,
    private val exerciseDao: ExerciseDao,
    private val muscleGroupDao: MuscleGroupDao,
    private val exerciseInTrainingDao: ExerciseInTrainingDao,
    private val prefs: PreferencesManager
): DBSubscriber(), FunctionsContract.Presenter {

    init {
        disposeOnStop(heroDao.getList(HumanType.HERO.ordinal)
            .subscribeDefault(Consumer { heroes ->
                if (prefs.firstLaunch && (heroes == null || heroes.isEmpty())) {
                    createMuscleGroupsAndExercises()
                    showDemoConfigDialog()
                }
            }, "getList"))
    }


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {

    }
    //</editor-fold>


    private fun createMuscleGroupsAndExercises() {
        muscleGroupDao.insert(view.provideMockMuscleGroups())
            .subscribeDefault("muscleGroupDao.insert")
        exerciseDao.insert(view.provideMockExercises())
            .subscribeDefault("exerciseDao.insert")
    }

    private fun showDemoConfigDialog() {
        view.showMsg(R.string.load_demo_configuration, Action {
            prefs.firstLaunch = false
            loadDemoConfig()
        }, Action {
            showCreateHeroesDialog()
        })
    }

    private fun showCreateHeroesDialog() {
        view.showMsg(R.string.create_heroes, Action {
            prefs.firstLaunch = false
            view.turnPage(3)
        }, Action {
            prefs.firstLaunch = false
        })
    }

    private fun loadDemoConfig() {
        heroDao.insert(view.provideMockHeroes())
            .subscribeDefault("heroDao.insert heroes")
        heroDao.insert(view.provideMockChampions())
            .subscribeDefault("heroDao.insert champions")
        trainingDao.insert(view.provideMockTrainings())
            .subscribeDefault("trainingDao.insert")
        exerciseInTrainingDao.insert(view.provideMockExercisesInTrainings())
            .subscribeDefault("exerciseInTrainingDao.insert")
    }


    companion object {
        private const val LOG_TAG = "FunctionsPresenter"
    }
}