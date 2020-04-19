package com.devtau.ironHeroes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.Logger

open class BaseViewModel: ViewModel() {

    protected val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText


    protected fun processTrainingsFromDB(
        result: Result<List<Training>>,
        snackbarText: MutableLiveData<Event<Int>>
    ): LiveData<List<Training>> =
        if (result is Result.Success) {
            Logger.d(LOG_TAG, "got new trainings list. size=${result.data.size}")
            MutableLiveData(result.data)
        } else {
            Logger.e(LOG_TAG, "Error loading trainings")
            snackbarText.value = Event(R.string.error_trainings_list)
            MutableLiveData(emptyList())
        }

    protected fun processExercisesFromDB(
        result: Result<List<ExerciseInTraining>>,
        snackbarText: MutableLiveData<Event<Int>>
    ): LiveData<List<ExerciseInTraining>> =
        if (result is Result.Success) {
            Logger.d(LOG_TAG, "got new exercisesInTrainings list. size=${result.data.size}")
            MutableLiveData(result.data)
        } else {
            Logger.e(LOG_TAG, "Error loading exercisesInTrainings")
            snackbarText.value = Event(R.string.error_exercises_list)
            MutableLiveData(emptyList())
        }


    companion object {
        private const val LOG_TAG = "BaseViewModel"
    }
}