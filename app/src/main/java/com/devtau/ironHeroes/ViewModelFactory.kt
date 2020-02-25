package com.devtau.ironHeroes

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepository
import com.devtau.ironHeroes.ui.fragments.trainingsList.TrainingsViewModel
import com.devtau.ironHeroes.util.PreferencesManager

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val trainingsRepository: TrainingsRepository,
    private val heroesRepository: HeroesRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
): AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle) = with(modelClass) {
        when {
            isAssignableFrom(TrainingsViewModel::class.java) -> TrainingsViewModel(trainingsRepository, heroesRepository, PreferencesManager)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
