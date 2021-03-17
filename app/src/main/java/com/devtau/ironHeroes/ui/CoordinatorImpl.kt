package com.devtau.ironHeroes.ui

import android.view.View
import androidx.navigation.findNavController
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.wrappers.EditDialogDataWrapper
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.fragments.functions.FunctionsFragmentDirections
import com.devtau.ironHeroes.ui.fragments.heroesList.HeroesFragmentDirections
import com.devtau.ironHeroes.ui.fragments.trainingDetails.TrainingDetailsFragmentDirections
import com.devtau.ironHeroes.util.Constants

/**
 * Implementation of navigation manager
 */
object CoordinatorImpl: Coordinator {

    override fun launchHeroes(view: View?, humanType: HumanType) {
        val toolbarTitleId = when (humanType) {
            HumanType.HERO -> R.string.trainees
            HumanType.CHAMPION -> R.string.trainers
        }
        val toolbarTitle = view?.context?.getString(toolbarTitleId) ?: ""
        val direction = FunctionsFragmentDirections.actionFunctionsFragmentToHeroesFragment(
            humanType, toolbarTitle
        )
        view?.findNavController()?.navigate(direction)
    }

    override fun launchHeroDetails(view: View?, heroId: Long, humanType: HumanType) {
        val toolbarTitleId = if (humanType == HumanType.CHAMPION) {
            if (heroId == Constants.OBJECT_ID_NA) R.string.champion_add else R.string.champion_edit
        } else {
            if (heroId == Constants.OBJECT_ID_NA) R.string.hero_add else R.string.hero_edit
        }
        val toolbarTitle = view?.context?.getString(toolbarTitleId) ?: ""
        val direction = HeroesFragmentDirections.actionHeroesFragmentToHeroDetailsFragment(
            humanType, heroId, toolbarTitle
        )
        view?.findNavController()?.navigate(direction)
    }

    override fun launchTrainingDetails(view: View?, trainingId: Long) {
        val toolbarTitleId = if (trainingId == Constants.OBJECT_ID_NA) R.string.training_add else R.string.training_edit
        val toolbarTitle = view?.context?.getString(toolbarTitleId) ?: ""
        val direction = FunctionsFragmentDirections.actionFunctionsFragmentToTrainingDetailsFragment(
            trainingId, toolbarTitle
        )
        view?.findNavController()?.navigate(direction)
    }

    override fun showExerciseFromTraining(view: View?, dialogData: EditDialogDataWrapper) {
        val direction = TrainingDetailsFragmentDirections.actionTrainingDetailsFragmentToExerciseDialog(
            dialogData.exerciseInTrainingId,
            dialogData.heroId,
            dialogData.position,
            dialogData.trainingId
        )
        view?.findNavController()?.navigate(direction)
    }

    override fun showExerciseFromStatistics(view: View?, dialogData: EditDialogDataWrapper) {
        val direction = FunctionsFragmentDirections.actionFunctionsFragmentToExerciseDialog(
            dialogData.exerciseInTrainingId,
            dialogData.heroId,
            dialogData.position,
            dialogData.trainingId
        )
        view?.findNavController()?.navigate(direction)
    }
}