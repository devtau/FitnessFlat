package com.devtau.ironHeroes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.activities.heroDetails.HeroDetailsActivity
import com.devtau.ironHeroes.ui.activities.heroesList.HeroesActivity
import com.devtau.ironHeroes.ui.activities.trainingDetails.TrainingDetailsActivity
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog
import com.devtau.ironHeroes.ui.fragments.other.OtherFragment
import com.devtau.ironHeroes.ui.fragments.settings.SettingsFragment
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsFragment
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsFragment
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.Logger

object CoordinatorImpl: Coordinator {

    private const val EXERCISE_DIALOG_TAG = "com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog"


    //<editor-fold desc="activities">
    override fun launchHeroesActivity(context: Context?, humanType: HumanType) {
        val intent = Intent(context, HeroesActivity::class.java)
        intent.putExtra(Constants.HUMAN_TYPE, humanType)
        context?.startActivity(intent)
    }

    override fun launchHeroDetailsActivity(context: Context?, heroId: Long?, humanType: HumanType) {
        val intent = Intent(context, HeroDetailsActivity::class.java)
        if (heroId != null) intent.putExtra(Constants.HERO_ID, heroId)
        intent.putExtra(Constants.HUMAN_TYPE, humanType)
        context?.startActivity(intent)
    }

    override fun launchTrainingDetailsActivity(context: Context?, trainingId: Long?) {
        val intent = Intent(context, TrainingDetailsActivity::class.java)
        if (trainingId != null) intent.putExtra(Constants.TRAINING_ID, trainingId)
        context?.startActivity(intent)
    }
    //</editor-fold>


    //<editor-fold desc="dialogs">
    override fun showExerciseDialog(fragmentManager: FragmentManager?, heroId: Long?, trainingId: Long?,
                                    exerciseInTrainingId: Long?, position: Int?) {
        fun newExerciseDialogInstance(heroId: Long, trainingId: Long?, exerciseInTrainingId: Long?,
                                      position: Int?): ExerciseDialog {
            val fragment = ExerciseDialog()
            val args = Bundle()
            args.putLong(Constants.HERO_ID, heroId)
            if (trainingId != null) args.putLong(Constants.TRAINING_ID, trainingId)
            if (exerciseInTrainingId != null) args.putLong(Constants.EXERCISE_IN_TRAINING_ID, exerciseInTrainingId)
            if (position != null) args.putInt(Constants.POSITION, position)
            fragment.arguments = args
            return fragment
        }

        if (fragmentManager == null || heroId == null || trainingId == null) {
            Logger.e(ExerciseDialog.LOG_TAG, "showExerciseDialog. bad data. aborting")
            return
        }
        val ft = fragmentManager.beginTransaction()
        val prev = fragmentManager.findFragmentByTag(EXERCISE_DIALOG_TAG)
        if (prev != null) ft.remove(prev)
        ft.addToBackStack(null)
        val newFragment = newExerciseDialogInstance(heroId, trainingId, exerciseInTrainingId, position)
        newFragment.show(ft, EXERCISE_DIALOG_TAG)
    }
    //</editor-fold>

    //<editor-fold desc="fragments">
    override fun newOtherFragmentInstance() = OtherFragment()

    override fun newSettingsFragmentInstance() = SettingsFragment()

    override fun newStatisticsFragmentInstance(heroId: Long): StatisticsFragment {
        val fragment = StatisticsFragment()
        val args = Bundle()
        args.putLong(Constants.HERO_ID, heroId)
        fragment.arguments = args
        return fragment
    }

    override fun newTrainingsFragmentInstance() = TrainingsFragment()
    //</editor-fold>
}