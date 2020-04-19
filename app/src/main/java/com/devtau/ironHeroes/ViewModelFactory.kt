package com.devtau.ironHeroes

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.devtau.ironHeroes.data.source.repositories.*
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.CoordinatorImpl
import com.devtau.ironHeroes.ui.ResourceResolver
import com.devtau.ironHeroes.ui.dialogs.exerciseDialog.ExerciseViewModel
import com.devtau.ironHeroes.ui.fragments.functions.FunctionsViewModel
import com.devtau.ironHeroes.ui.fragments.heroDetails.HeroDetailsViewModel
import com.devtau.ironHeroes.ui.fragments.heroesList.HeroesListViewModel
import com.devtau.ironHeroes.ui.fragments.other.OtherViewModel
import com.devtau.ironHeroes.ui.fragments.settings.SettingsViewModel
import com.devtau.ironHeroes.ui.fragments.statistics.StatisticsViewModel
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
                if (argsBundle == null)
                    throw IllegalArgumentException("HeroDetailsViewModel needs valid argsBundle")
                val trainingIdFromBundle = argsBundle.getLong(CoordinatorImpl.TRAINING_ID)
                val trainingId = if (trainingIdFromBundle == Constants.OBJECT_ID_NA) null else trainingIdFromBundle
                TrainingDetailsViewModel(
                    trainingsRepository, heroesRepository, exercisesInTrainingsRepository,
                    PreferencesManager, trainingId)
            }
            isAssignableFrom(HeroesListViewModel::class.java) -> {
                val humanType = argsBundle?.getSerializable(CoordinatorImpl.HUMAN_TYPE) as HumanType? ?: HumanType.HERO
                HeroesListViewModel(heroesRepository, humanType)
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
                if (argsBundle == null)
                    throw IllegalArgumentException("HeroDetailsViewModel needs valid argsBundle")
                val heroIdFromBundle = argsBundle.getLong(CoordinatorImpl.HERO_ID)
                val heroId = if (heroIdFromBundle == Constants.OBJECT_ID_NA) null else heroIdFromBundle
                val humanType = argsBundle.getSerializable(CoordinatorImpl.HUMAN_TYPE) as HumanType
                HeroDetailsViewModel(heroesRepository, heroId, humanType)
            }
            isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(PreferencesManager)
            }
            isAssignableFrom(StatisticsViewModel::class.java) -> {
                if (resourceResolver == null)
                    throw IllegalArgumentException("StatisticsViewModel needs valid resourceResolver")
                StatisticsViewModel(heroesRepository, muscleGroupsRepository, exercisesRepository,
                    exercisesInTrainingsRepository, PreferencesManager, resourceResolver)
            }
            isAssignableFrom(ExerciseViewModel::class.java) -> {
                if (argsBundle == null)
                    throw IllegalArgumentException("ExerciseViewModel needs valid argsBundle")
                val heroId = argsBundle.getLong(CoordinatorImpl.HERO_ID)
                val trainingId = argsBundle.getLong(CoordinatorImpl.TRAINING_ID)
                val exerciseInTrainingId = argsBundle.getLong(CoordinatorImpl.EXERCISE_IN_TRAINING_ID)
                val position = argsBundle.getInt(CoordinatorImpl.POSITION)
                ExerciseViewModel(trainingsRepository, muscleGroupsRepository, exercisesRepository,
                    exercisesInTrainingsRepository, heroId, trainingId, exerciseInTrainingId, position)
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
