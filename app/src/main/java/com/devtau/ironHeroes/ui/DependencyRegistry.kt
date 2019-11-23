package com.devtau.ironHeroes.ui

import com.devtau.ironHeroes.data.DB
import com.devtau.ironHeroes.data.DataLayerImpl
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.rest.NetworkLayerImpl
import com.devtau.ironHeroes.ui.activities.heroDetails.HeroDetailsActivity
import com.devtau.ironHeroes.ui.activities.heroDetails.HeroDetailsPresenterImpl
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesActivity
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesPresenterImpl
import com.devtau.ironHeroes.ui.activities.statistics.StatisticsActivity
import com.devtau.ironHeroes.ui.activities.statistics.StatisticsPresenterImpl
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExercisePresenterImpl
import com.devtau.ironHeroes.ui.activities.launcher.LauncherActivity
import com.devtau.ironHeroes.ui.activities.launcher.LauncherPresenterImpl
import com.devtau.ironHeroes.ui.activities.trainingDetails.TrainingDetailsActivity
import com.devtau.ironHeroes.ui.activities.trainingDetails.TrainingDetailsPresenterImpl
import com.devtau.ironHeroes.ui.activities.trainingsList.TrainingsActivity
import com.devtau.ironHeroes.ui.activities.trainingsList.TrainingsPresenterImpl
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.EXERCISE_IN_TRAINING_ID
import com.devtau.ironHeroes.util.Constants.HERO_ID
import com.devtau.ironHeroes.util.Constants.HUMAN_TYPE
import com.devtau.ironHeroes.util.Constants.TRAINING_ID
import com.devtau.ironHeroes.util.PreferencesManager

class DependencyRegistry {

    fun inject(activity: LauncherActivity) {
        val dataLayer = DataLayerImpl(activity, DB.getInstance(activity))
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager(activity)
        activity.presenter = LauncherPresenterImpl(activity, dataLayer, networkLayer, prefs)
    }

    fun inject(activity: HeroesActivity) {
        val dataLayer = DataLayerImpl(activity, DB.getInstance(activity))
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager(activity)
        val humanType = activity.intent?.extras?.getSerializable(HUMAN_TYPE) as HumanType? ?: return
        activity.presenter = HeroesPresenterImpl(activity, dataLayer, networkLayer, prefs, humanType)
    }

    fun inject(activity: HeroDetailsActivity) {
        val dataLayer = DataLayerImpl(activity, DB.getInstance(activity))
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager(activity)
        val heroId = if (activity.intent?.hasExtra(HERO_ID) == true) activity.intent?.extras?.getLong(HERO_ID) else null
        val humanType = activity.intent?.extras?.getSerializable(HUMAN_TYPE) as HumanType? ?: return
        activity.presenter = HeroDetailsPresenterImpl(activity, dataLayer, networkLayer, prefs, heroId, humanType)
    }

    fun inject(activity: TrainingsActivity) {
        val dataLayer = DataLayerImpl(activity, DB.getInstance(activity))
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager(activity)
        activity.presenter = TrainingsPresenterImpl(activity, dataLayer, networkLayer, prefs)
    }

    fun inject(activity: TrainingDetailsActivity) {
        val dataLayer = DataLayerImpl(activity, DB.getInstance(activity))
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager(activity)
        val trainingId = if (activity.intent?.hasExtra(TRAINING_ID) == true) activity.intent?.extras?.getLong(TRAINING_ID) else null
        activity.presenter = TrainingDetailsPresenterImpl(activity, dataLayer, networkLayer, prefs, trainingId)
    }

    fun inject(dialog: ExerciseDialog) {
        val context = dialog.context ?: return
        val dataLayer = DataLayerImpl(context, DB.getInstance(context))
        val networkLayer = NetworkLayerImpl(context)
        val prefs = PreferencesManager(context)
        val heroId = if (dialog.arguments?.containsKey(HERO_ID) == true)
            dialog.arguments?.getLong(HERO_ID) else null
        val trainingId = if (dialog.arguments?.containsKey(TRAINING_ID) == true)
            dialog.arguments?.getLong(TRAINING_ID) else null
        val exerciseId = if (dialog.arguments?.containsKey(EXERCISE_IN_TRAINING_ID) == true)
            dialog.arguments?.getLong(EXERCISE_IN_TRAINING_ID) else null
        if (heroId == null) {
            AppUtils.alert(LOG_TAG, "$dialog misses necessary heroId", context)
            return
        }
        dialog.presenter = ExercisePresenterImpl(
            dialog,
            dataLayer,
            networkLayer,
            prefs,
            heroId,
            trainingId,
            exerciseId
        )
    }

    fun inject(activity: StatisticsActivity) {
        val dataLayer = DataLayerImpl(activity, DB.getInstance(activity))
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager(activity)
        val heroId = if (activity.intent?.hasExtra(HERO_ID) == true) activity.intent?.extras?.getLong(HERO_ID) else null
        if (heroId == null) {
            AppUtils.alert(LOG_TAG, "$activity misses necessary heroId", activity)
            return
        }
        activity.presenter = StatisticsPresenterImpl(activity, dataLayer, networkLayer, prefs, heroId)
    }

    companion object {
        private const val LOG_TAG = "DependencyRegistry"
    }
}