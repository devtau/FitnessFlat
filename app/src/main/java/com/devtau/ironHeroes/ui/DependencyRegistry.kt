package com.devtau.ironHeroes.ui

import com.devtau.ironHeroes.data.DB
import com.devtau.ironHeroes.data.DataLayerImpl
import com.devtau.ironHeroes.enums.HumanType
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
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.EXERCISE_IN_TRAINING_ID
import com.devtau.ironHeroes.util.Constants.HERO_ID
import com.devtau.ironHeroes.util.Constants.HUMAN_TYPE
import com.devtau.ironHeroes.util.Constants.POSITION
import com.devtau.ironHeroes.util.Constants.TRAINING_ID
import com.devtau.ironHeroes.util.PreferencesManager

class DependencyRegistry {

    fun inject(activity: FunctionsActivity) {
        val dataLayer = DataLayerImpl(activity, DB.getInstance(activity))
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager(activity)
        activity.presenter = FunctionsPresenterImpl(activity, dataLayer, networkLayer, prefs)
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

    fun inject(fragment: TrainingsFragment) {
        val context = fragment.context ?: return
        val dataLayer = DataLayerImpl(context, DB.getInstance(context))
        val networkLayer = NetworkLayerImpl(context)
        val prefs = PreferencesManager(context)
        fragment.presenter = TrainingsPresenterImpl(fragment, dataLayer, networkLayer, prefs)
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
        val position = if (dialog.arguments?.containsKey(POSITION) == true)
            dialog.arguments?.getInt(POSITION) else null
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
            exerciseId,
            position
        )
    }

    fun inject(fragment: StatisticsFragment) {
        val context = fragment.context ?: return
        val dataLayer = DataLayerImpl(context, DB.getInstance(context))
        val networkLayer = NetworkLayerImpl(context)
        val prefs = PreferencesManager(context)
        val heroId = if (fragment.arguments?.containsKey(HERO_ID) == true) fragment.arguments?.getLong(HERO_ID) else null
        if (heroId == null) {
            AppUtils.alert(LOG_TAG, "$fragment misses necessary heroId", context)
            return
        }
        fragment.presenter = StatisticsPresenterImpl(fragment, dataLayer, networkLayer, prefs, heroId)
    }

    fun inject(fragment: SettingsFragment) {
        val context = fragment.context ?: return
        val dataLayer = DataLayerImpl(context, DB.getInstance(context))
        val networkLayer = NetworkLayerImpl(context)
        val prefs = PreferencesManager(context)
        fragment.presenter = SettingsPresenterImpl(fragment, dataLayer, networkLayer, prefs)
    }

    fun inject(fragment: OtherFragment) {
        val context = fragment.context ?: return
        val dataLayer = DataLayerImpl(context, DB.getInstance(context))
        val networkLayer = NetworkLayerImpl(context)
        val prefs = PreferencesManager(context)
        fragment.presenter = OtherPresenterImpl(fragment, dataLayer, networkLayer, prefs)
    }

    companion object {
        private const val LOG_TAG = "DependencyRegistry"
    }
}