package com.devtau.ironHeroes.ui.fragments.functions

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devtau.ironHeroes.BaseViewModel
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.data.source.repositories.*
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.prefs.PreferencesManager
import kotlinx.coroutines.launch
import java.util.*

class FunctionsViewModel(
    private val trainingsRepository: TrainingsRepository,
    private val heroesRepository: HeroesRepository,
    private val exercisesInTrainingsRepository: ExercisesInTrainingsRepository,
    private val exercisesRepository: ExercisesRepository,
    private val muscleGroupsRepository: MuscleGroupsRepository,
    private val prefs: PreferencesManager
): BaseViewModel() {

    private val _turnPage = MutableLiveData<Event<Int>>()
    val turnPage: LiveData<Event<Int>> = _turnPage
    fun turnPage(pageIndex: Int) {
        _turnPage.value = Event(pageIndex)
    }


    private val _showDemoConfigDialog = MutableLiveData<Event<Unit>>()
    val showDemoConfigDialog: LiveData<Event<Unit>> = _showDemoConfigDialog
    fun loadDemoConfigConfirmed(context: Context) {
        prefs.firstLaunch = false
        viewModelScope.launch {
            createMuscleGroupsAndExercises(context)
            val localeIsRu = Locale.getDefault() == Locale("ru", "RU")
            heroesRepository.saveList(Hero.getMockHeroes(context))
            heroesRepository.saveList(Hero.getMockChampions(context))
            trainingsRepository.saveList(Training.getMock(context))
            exercisesInTrainingsRepository.saveList(ExerciseInTraining.getMock(context, localeIsRu))
        }
    }
    fun loadDemoConfigDeclined(context: Context) {
        createMuscleGroupsAndExercises(context)
        _showCreateHeroesDialog.value = Event(Unit)
    }


    private val _showCreateHeroesDialog = MutableLiveData<Event<Unit>>()
    val showCreateHeroesDialog: LiveData<Event<Unit>> = _showCreateHeroesDialog
    fun createHeroesConfirmed() {
        prefs.firstLaunch = false
    }
    fun createHeroesDeclined() {
        prefs.firstLaunch = false
    }


    init {
        viewModelScope.launch {
            val heroesResult = heroesRepository.getList()
            if (prefs.firstLaunch && heroesResult is Result.Success && heroesResult.data.isEmpty()) {
                _showDemoConfigDialog.value = Event(Unit)
            }
        }
    }

    private fun createMuscleGroupsAndExercises(context: Context) {
        viewModelScope.launch {
            muscleGroupsRepository.saveList(MuscleGroup.getMock(context))
            exercisesRepository.saveList(Exercise.getMock(context))
        }
    }


    companion object {
        private const val LOG_TAG = "FunctionsViewModel"
    }
}