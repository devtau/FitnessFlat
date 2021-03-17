package com.devtau.ironHeroes

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.devtau.ironHeroes.data.source.repositories.*
import com.devtau.ironHeroes.ui.ResourceResolver
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseDialogArgs
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseViewModel
import com.devtau.ironHeroes.ui.fragments.functions.FunctionsViewModel
import com.devtau.ironHeroes.ui.fragments.heroDetails.HeroDetailsFragmentArgs
import com.devtau.ironHeroes.ui.fragments.heroDetails.HeroDetailsViewModel
import com.devtau.ironHeroes.ui.fragments.heroesList.HeroesFragmentArgs
import com.devtau.ironHeroes.ui.fragments.heroesList.HeroesViewModel
import com.devtau.ironHeroes.ui.fragments.other.OtherViewModel
import com.devtau.ironHeroes.ui.fragments.settings.SettingsViewModel
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsViewModel
import com.devtau.ironHeroes.ui.fragments.trainingDetails.TrainingDetailsFragmentArgs
import com.devtau.ironHeroes.ui.fragments.trainingDetails.TrainingDetailsViewModel
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsViewModel
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.prefs.PreferencesManager

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val trainingsRepository: TrainingsRepository,
    private val heroesRepository: HeroesRepository,
    private val exercisesInTrainingsRepository: ExercisesInTrainingsRepository,
    private val exercisesRepository: ExercisesRepository,
    private val muscleGroupsRepository: MuscleGroupsRepository,
    owner: SavedStateRegistryOwner,
    private val argsBundle: Bundle?,
    private val resourceResolver: ResourceResolver? = null
): AbstractSavedStateViewModelFactory(owner, argsBundle) {

    override fun <T: ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle) = with(modelClass) {
        when {
            isAssignableFrom(TrainingsViewModel::class.java) -> {
                TrainingsViewModel(trainingsRepository, heroesRepository, PreferencesManager)
            }
            isAssignableFrom(TrainingDetailsViewModel::class.java) -> {
                val args = TrainingDetailsFragmentArgs.fromBundle(argsBundle!!)
                val trainingId = if (args.trainingId == Constants.OBJECT_ID_NA) null else args.trainingId
                TrainingDetailsViewModel(
                    trainingsRepository, heroesRepository, exercisesInTrainingsRepository,
                    PreferencesManager, trainingId)
            }
            isAssignableFrom(HeroesViewModel::class.java) -> {
                val args = HeroesFragmentArgs.fromBundle(argsBundle!!)
                HeroesViewModel(heroesRepository, args.humanType)
            }
            isAssignableFrom(OtherViewModel::class.java) -> {
                OtherViewModel(trainingsRepository, exercisesInTrainingsRepository, heroesRepository)
            }
            isAssignableFrom(FunctionsViewModel::class.java) -> {
                FunctionsViewModel(trainingsRepository, heroesRepository, exercisesInTrainingsRepository,
                    exercisesRepository, muscleGroupsRepository, PreferencesManager
                )
            }
            isAssignableFrom(HeroDetailsViewModel::class.java) -> {
                val args = HeroDetailsFragmentArgs.fromBundle(argsBundle!!)
                val heroId = if (args.heroId == Constants.OBJECT_ID_NA) null else args.heroId
                HeroDetailsViewModel(heroesRepository, heroId, args.humanType)
            }
            isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(PreferencesManager)
            }
            isAssignableFrom(StatisticsViewModel::class.java) -> {
                StatisticsViewModel(heroesRepository, muscleGroupsRepository, exercisesRepository,
                    exercisesInTrainingsRepository, PreferencesManager, resourceResolver!!)
            }
            isAssignableFrom(ExerciseViewModel::class.java) -> {
                val args = ExerciseDialogArgs.fromBundle(argsBundle!!)
                ExerciseViewModel(
                    trainingsRepository, muscleGroupsRepository, exercisesRepository, exercisesInTrainingsRepository,
                    args.exerciseInTrainingId, args.heroId, args.position, args.trainingId
                )
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
