package com.devtau.ironHeroes.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.wrappers.EditDialogDataWrapper
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.activities.DBViewerActivity
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialog
import com.devtau.ironHeroes.util.Logger

object CoordinatorImpl: Coordinator {

    const val HERO_ID = "heroId"
    const val HUMAN_TYPE = "humanType"
    const val TRAINING_ID = "trainingId"
    const val EXERCISE_IN_TRAINING_ID = "exerciseInTrainingId"
    const val POSITION = "position"


    override fun launchHeroes(view: View?, humanType: HumanType) {
        if (view == null) return
        val bundle = bundleOf(HUMAN_TYPE to humanType)
        view.findNavController().navigate(R.id.action_functionsFragment_to_heroesFragment, bundle)
    }

    override fun launchHeroDetails(view: View?, heroId: Long, humanType: HumanType) {
        if (view == null) return
        val bundle = bundleOf(HUMAN_TYPE to humanType)
        bundle.putLong(HERO_ID, heroId)
        view.findNavController().navigate(R.id.action_heroesFragment_to_heroDetailsFragment, bundle)
    }

    override fun launchTrainingDetails(view: View?, trainingId: Long) {
        val bundle = Bundle()
        bundle.putLong(TRAINING_ID, trainingId)
        view?.findNavController()?.navigate(R.id.action_functionsFragment_to_trainingDetailsFragment, bundle)
    }

    override fun launchDBViewer(context: Context?) {
        context?.startActivity(Intent(context, DBViewerActivity::class.java))
    }

    override fun showExerciseFromTraining(view: View?, dialogData: EditDialogDataWrapper) =
        showExercise(view, dialogData, R.id.action_trainingDetailsFragment_to_exerciseDialog)

    override fun showExerciseFromStatistics(view: View?, dialogData: EditDialogDataWrapper) =
        showExercise(view, dialogData, R.id.action_functionsFragment_to_exerciseDialog)


    private fun showExercise(view: View?, dialogData: EditDialogDataWrapper, navId: Int) {
        if (view == null) {
            Logger.e(ExerciseDialog.LOG_TAG, "showExerciseDialog. bad data. aborting")
            return
        }
        val bundle = bundleOf(
            HERO_ID to dialogData.heroId,
            TRAINING_ID to dialogData.trainingId,
            EXERCISE_IN_TRAINING_ID to dialogData.exerciseInTrainingId,
            POSITION to dialogData.position
        )
        view.findNavController().navigate(navId, bundle)
    }
}