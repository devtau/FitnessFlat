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
                    view.showMsg(R.string.load_demo_configuration, Action {
                        prefs.firstLaunch = false
                        loadDemoConfig()
                    }, Action {
                        prefs.firstLaunch = false
                    })
                }
            }, "getList"))
    }


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {

    }
    //</editor-fold>


    private fun loadDemoConfig() {
        muscleGroupDao.insert(view.provideMockMuscleGroups()).subscribeDefault("updateMuscleGroups. inserted")
        exerciseDao.insert(view.provideMockExercises()).subscribeDefault("updateExercises. inserted")
        heroDao.insert(view.provideMockHeroes()).subscribeDefault("heroes. inserted")
        heroDao.insert(view.provideMockChampions()).subscribeDefault("champions. inserted")
        heroDao.insert(view.provideMockChampions()).subscribeDefault("champions. inserted")
        trainingDao.insert(view.provideMockTrainings()).subscribeDefault("updateTrainings. inserted")
        exerciseInTrainingDao.insert(view.provideMockExercisesInTrainings()).subscribeDefault("updateExercisesInTraining. inserted")
    }


    companion object {
        private const val LOG_TAG = "FunctionsPresenter"
    }
}