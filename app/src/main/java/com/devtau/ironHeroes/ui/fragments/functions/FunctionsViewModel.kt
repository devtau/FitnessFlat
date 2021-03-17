package com.devtau.ironHeroes.ui.fragments.functions

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devtau.ironHeroes.BaseViewModel
import com.devtau.ironHeroes.R
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

    val turnPage = MutableLiveData<Event<Int>>()
    fun turnPage(pageIndex: Int) {
        turnPage.value = Event(pageIndex)
    }


    val showDemoConfigDialog = MutableLiveData<Event<Unit>>()
    fun loadDemoConfigConfirmed(context: Context) {
        prefs.firstLaunch = false
        viewModelScope.launch {
            createMuscleGroupsAndExercises(context)
            val localeIsRu = Locale.getDefault() == Locale("ru", "RU")
            heroesRepository.saveList(Hero.getMockHeroes(context))
            heroesRepository.saveList(Hero.getMockChampions(context))
            trainingsRepository.saveList(Training.getMock(context))
            exercisesInTrainingsRepository.saveList(ExerciseInTraining.getMock(context, localeIsRu))
            snackbarText.value = Event(R.string.database_populated)
        }
    }
    fun loadDemoConfigDeclined(context: Context) {
        createMuscleGroupsAndExercises(context)
        showCreateHeroesDialog.value = Event(Unit)
    }


    val showCreateHeroesDialog = MutableLiveData<Event<Unit>>()
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
                showDemoConfigDialog.value = Event(Unit)
            }
        }
    }

    private fun createMuscleGroupsAndExercises(context: Context) {
        viewModelScope.launch {
            muscleGroupsRepository.saveList(MuscleGroup.getMock(context))
            exercisesRepository.saveList(Exercise.getMock(context))
        }
    }
}