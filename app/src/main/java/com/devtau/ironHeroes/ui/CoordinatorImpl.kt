package com.devtau.ironHeroes.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.enums.HumanType
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

    override fun launchHeroDetails(view: View?, heroId: Long?, humanType: HumanType) {
        if (view == null) return
        val bundle = bundleOf(HUMAN_TYPE to humanType)
        if (heroId != null) bundle.putLong(HERO_ID, heroId)
        view.findNavController().navigate(R.id.action_heroesFragment_to_heroDetailsFragment, bundle)
    }

    override fun launchTrainingDetails(view: View?, trainingId: Long?) {
        val bundle = Bundle()
        if (trainingId != null) bundle.putLong(TRAINING_ID, trainingId)
        view?.findNavController()?.navigate(R.id.action_functionsFragment_to_trainingDetailsFragment, bundle)
    }

    override fun showExercise(view: View?, heroId: Long?, trainingId: Long?, exerciseInTrainingId: Long?, position: Int?) {
        if (view == null || heroId == null || trainingId == null) {
            Logger.e(ExerciseDialog.LOG_TAG, "showExerciseDialog. bad data. aborting")
            return
        }
        val bundle = bundleOf(
            HERO_ID to heroId,
            TRAINING_ID to trainingId,
            EXERCISE_IN_TRAINING_ID to exerciseInTrainingId,
            POSITION to position
        )
        view.findNavController().navigate(R.id.action_trainingDetailsFragment_to_exerciseDialog, bundle)
    }
}