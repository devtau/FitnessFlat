package com.devtau.ironHeroes.ui

import android.os.Bundle
import com.devtau.ironHeroes.data.DB
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.CoordinatorImpl.EXERCISE_IN_TRAINING_ID
import com.devtau.ironHeroes.ui.CoordinatorImpl.HERO_ID
import com.devtau.ironHeroes.ui.CoordinatorImpl.HUMAN_TYPE
import com.devtau.ironHeroes.ui.CoordinatorImpl.POSITION
import com.devtau.ironHeroes.ui.CoordinatorImpl.TRAINING_ID
import com.devtau.ironHeroes.ui.activities.main.MainActivity
import com.devtau.ironHeroes.ui.activities.main.MainPresenterImpl
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExercisePresenterImpl
import com.devtau.ironHeroes.ui.fragments.functions.FunctionsFragment
import com.devtau.ironHeroes.ui.fragments.functions.FunctionsPresenterImpl
import com.devtau.ironHeroes.ui.fragments.heroDetails.HeroDetailsFragment
import com.devtau.ironHeroes.ui.fragments.heroDetails.HeroDetailsPresenterImpl
import com.devtau.ironHeroes.ui.fragments.heroesList.HeroesFragment
import com.devtau.ironHeroes.ui.fragments.heroesList.HeroesPresenterImpl
import com.devtau.ironHeroes.ui.fragments.other.OtherFragment
import com.devtau.ironHeroes.ui.fragments.other.OtherPresenterImpl
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsPresenterImpl
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsFragment
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsPresenterImpl
import com.devtau.ironHeroes.ui.fragments.trainingDetails.TrainingDetailsFragment
import com.devtau.ironHeroes.ui.fragments.trainingDetails.TrainingDetailsPresenterImpl
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsPresenterImpl
import com.devtau.ironHeroes.util.PreferencesManager

object DependencyRegistry {

    private const val LOG_TAG = "DependencyRegistry"
    private val prefs = PreferencesManager
    private val coordinator: Coordinator = CoordinatorImpl


    //<editor-fold desc="injectors">
    fun inject(activity: MainActivity) {
        val db = DB.getInstance(activity)
        val presenter = MainPresenterImpl(activity, db.heroDao(), db.trainingDao(),
            db.exerciseDao(), db.muscleGroupDao(), db.exerciseInTrainingDao(), prefs)
        activity.configureWith(presenter, coordinator)
    }

    fun inject(fragment: FunctionsFragment) = fragment.context?.let {
        val db = DB.getInstance(it)
        val presenter = FunctionsPresenterImpl(fragment, db.heroDao(), db.trainingDao(),
            db.exerciseDao(), db.muscleGroupDao(), db.exerciseInTrainingDao(), prefs)
        fragment.configureWith(presenter, coordinator)
    }

    @Throws(NoSuchElementException::class)
    fun inject(fragment: HeroesFragment) = fragment.context?.let {
        val db = DB.getInstance(it)
        val humanType = humanTypeFromBundleOrThrow(fragment.arguments)
        val presenter = HeroesPresenterImpl(fragment, db.heroDao(), humanType)
        fragment.configureWith(presenter, coordinator)
    }

    @Throws(NoSuchElementException::class)
    fun inject(fragment: HeroDetailsFragment) = fragment.context?.let {
        val db = DB.getInstance(it)
        val heroId = heroIdFromBundle(fragment.arguments)
        val humanType = humanTypeFromBundleOrThrow(fragment.arguments)
        val presenter = HeroDetailsPresenterImpl(fragment, db.heroDao(), heroId, humanType)
        fragment.configureWith(presenter)
    }

    fun inject(fragment: TrainingsFragment) = fragment.context?.let {
        val db = DB.getInstance(it)
        val presenter = TrainingsPresenterImpl(fragment, db.heroDao(), db.trainingDao(), prefs)
        fragment.configureWith(presenter, coordinator)
    }

    fun inject(fragment: TrainingDetailsFragment) = fragment.context?.let {
        val db = DB.getInstance(it)
        val trainingId = trainingIdFromBundle(fragment.arguments)
        val presenter = TrainingDetailsPresenterImpl(fragment, db.heroDao(), db.trainingDao(),
            db.exerciseDao(), db.exerciseInTrainingDao(), prefs, trainingId)
        fragment.configureWith(presenter, coordinator)
    }

    @Throws(NoSuchElementException::class)
    fun inject(dialog: ExerciseDialog) = dialog.context?.let {
        val db = DB.getInstance(it)
        val heroId = heroIdFromBundleOrThrow(dialog.arguments)
        val trainingId = trainingIdFromBundle(dialog.arguments)
        val exerciseId = exerciseIdFromBundle(dialog.arguments)
        val position = positionFromBundle(dialog.arguments)

        val presenter = ExercisePresenterImpl(dialog, db.trainingDao(), db.exerciseDao(),
            db.muscleGroupDao(), db.exerciseInTrainingDao(), heroId, trainingId, exerciseId, position)
        dialog.configureWith(presenter)
    }

    @Throws(NoSuchElementException::class)
    fun inject(fragment: StatisticsFragment) = fragment.context?.let {
        val db = DB.getInstance(it)
        val presenter = StatisticsPresenterImpl(fragment, db.heroDao(), db.exerciseDao(), db.exerciseInTrainingDao(), db.muscleGroupDao(), prefs)
        fragment.configureWith(presenter, coordinator)
    }

    fun inject(fragment: SettingsFragment) = fragment.context?.let {
        val presenter = SettingsPresenterImpl(fragment, prefs)
        fragment.configureWith(presenter)
    }

    fun inject(fragment: OtherFragment) = fragment.context?.let {
        val db = DB.getInstance(it)
        val presenter = OtherPresenterImpl(fragment, db.heroDao(), db.trainingDao(), db.exerciseInTrainingDao())
        fragment.configureWith(presenter, coordinator)
    }
    //</editor-fold>


    //<editor-fold desc="private helpers">
    @Throws(NoSuchElementException::class)
    private fun humanTypeFromBundleOrThrow(bundle: Bundle?): HumanType =
        bundle?.getSerializable(HUMAN_TYPE) as HumanType?
            ?: throw NoSuchElementException("bundle misses necessary HUMAN_TYPE")

    private fun heroIdFromBundle(bundle: Bundle?): Long? =
        if (bundle?.containsKey(HERO_ID) == true) bundle.getLong(HERO_ID) else null

    @Throws(NoSuchElementException::class)
    private fun heroIdFromBundleOrThrow(bundle: Bundle?): Long =
        if (bundle?.containsKey(HERO_ID) == true) bundle.getLong(HERO_ID) else null
            ?: throw NoSuchElementException("bundle misses necessary HERO_ID")

    private fun trainingIdFromBundle(bundle: Bundle?): Long? =
        if (bundle?.containsKey(TRAINING_ID) == true) bundle.getLong(TRAINING_ID) else null

    private fun exerciseIdFromBundle(bundle: Bundle?): Long? =
        if (bundle?.containsKey(EXERCISE_IN_TRAINING_ID) == true) bundle.getLong(EXERCISE_IN_TRAINING_ID) else null

    private fun positionFromBundle(bundle: Bundle?): Int? =
        if (bundle?.containsKey(POSITION) == true) bundle.getInt(POSITION) else null
    //</editor-fold>
}