package com.devtau.ironHeroes.ui

import android.os.Bundle
import com.devtau.ironHeroes.Coordinator
import com.devtau.ironHeroes.CoordinatorImpl
import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.DataLayerImpl
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.rest.NetworkLayerImpl
import com.devtau.ironHeroes.ui.activities.functions.FunctionsActivity
import com.devtau.ironHeroes.ui.activities.functions.FunctionsPresenterImpl
import com.devtau.ironHeroes.ui.activities.heroDetails.HeroDetailsActivity
import com.devtau.ironHeroes.ui.activities.heroDetails.HeroDetailsPresenterImpl
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesActivity
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesPresenterImpl
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsFragment
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsPresenterImpl
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExercisePresenterImpl
import com.devtau.ironHeroes.ui.activities.trainingDetails.TrainingDetailsActivity
import com.devtau.ironHeroes.ui.activities.trainingDetails.TrainingDetailsPresenterImpl
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsPresenterImpl
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog
import com.devtau.ironHeroes.ui.fragments.other.OtherFragment
import com.devtau.ironHeroes.ui.fragments.other.OtherPresenterImpl
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsPresenterImpl
import com.devtau.ironHeroes.util.Constants.EXERCISE_IN_TRAINING_ID
import com.devtau.ironHeroes.util.Constants.HERO_ID
import com.devtau.ironHeroes.util.Constants.HUMAN_TYPE
import com.devtau.ironHeroes.util.Constants.POSITION
import com.devtau.ironHeroes.util.Constants.TRAINING_ID
import com.devtau.ironHeroes.util.PreferencesManager

object DependencyRegistry {

    private const val LOG_TAG = "DependencyRegistry"
    private val dataLayer: DataLayer = DataLayerImpl
    private val networkLayer: NetworkLayer = NetworkLayerImpl
    private val prefs = PreferencesManager
    private val coordinator: Coordinator = CoordinatorImpl


    //<editor-fold desc="injectors">
    fun inject(activity: FunctionsActivity) {
        val presenter = FunctionsPresenterImpl(activity, dataLayer, prefs)
        activity.configureWith(presenter, coordinator)
    }

    @Throws(NoSuchElementException::class)
    fun inject(activity: HeroesActivity) {
        val humanType = humanTypeFromBundleOrThrow(activity.intent?.extras)
        val presenter = HeroesPresenterImpl(activity, dataLayer, humanType)
        activity.configureWith(presenter, coordinator)
    }

    @Throws(NoSuchElementException::class)
    fun inject(activity: HeroDetailsActivity) {
        val heroId = heroIdFromBundle(activity.intent?.extras)
        val humanType = humanTypeFromBundleOrThrow(activity.intent?.extras)
        val presenter = HeroDetailsPresenterImpl(activity, dataLayer, heroId, humanType)
        activity.configureWith(presenter)
    }

    fun inject(fragment: TrainingsFragment) {
        val presenter = TrainingsPresenterImpl(fragment, dataLayer, prefs)
        fragment.configureWith(presenter, coordinator)
    }

    fun inject(activity: TrainingDetailsActivity) {
        val trainingId = trainingIdFromBundle(activity.intent?.extras)
        val presenter = TrainingDetailsPresenterImpl(activity, dataLayer, prefs, trainingId)
        activity.configureWith(presenter, coordinator)
    }

    @Throws(NoSuchElementException::class)
    fun inject(dialog: ExerciseDialog) {
        val heroId = heroIdFromBundleOrThrow(dialog.arguments)
        val trainingId = trainingIdFromBundle(dialog.arguments)
        val exerciseId = exerciseIdFromBundle(dialog.arguments)
        val position = positionFromBundle(dialog.arguments)

        val presenter = ExercisePresenterImpl(dialog, dataLayer, heroId, trainingId, exerciseId, position)
        dialog.configureWith(presenter)
    }

    @Throws(NoSuchElementException::class)
    fun inject(fragment: StatisticsFragment) {
        val heroId = heroIdFromBundleOrThrow(fragment.arguments)
        val presenter = StatisticsPresenterImpl(fragment, dataLayer, prefs, heroId)
        fragment.configureWith(presenter, coordinator)
    }

    fun inject(fragment: SettingsFragment) {
        val presenter = SettingsPresenterImpl(fragment, prefs)
        fragment.configureWith(presenter)
    }

    fun inject(fragment: OtherFragment) {
        val presenter = OtherPresenterImpl(fragment, dataLayer)
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