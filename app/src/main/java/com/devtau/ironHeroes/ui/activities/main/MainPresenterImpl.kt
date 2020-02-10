package com.devtau.ironHeroes.ui.activities.main

import com.devtau.ironHeroes.data.dao.*
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.PreferencesManager

class MainPresenterImpl(
    private val view: MainContract.View,
    private val heroDao: HeroDao,
    private val trainingDao: TrainingDao,
    private val exerciseDao: ExerciseDao,
    private val muscleGroupDao: MuscleGroupDao,
    private val exerciseInTrainingDao: ExerciseInTrainingDao,
    private val prefs: PreferencesManager
): DBSubscriber(), MainContract.Presenter {


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {

    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "MainPresenter"
    }
}