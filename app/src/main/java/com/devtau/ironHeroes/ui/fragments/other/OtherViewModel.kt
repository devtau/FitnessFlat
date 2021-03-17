package com.devtau.ironHeroes.ui.fragments.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.devtau.ironHeroes.BaseViewModel
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.model.wrappers.ImpExData
import com.devtau.ironHeroes.data.source.repositories.ExercisesInTrainingsRepository
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepository
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.FileUtils
import kotlinx.coroutines.launch
import timber.log.Timber

class OtherViewModel(
    private val trainingsRepository: TrainingsRepository,
    private val exercisesInTrainingsRepository: ExercisesInTrainingsRepository,
    private val heroesRepository: HeroesRepository
): BaseViewModel() {

    private val forceUpdateTrainingAndExercises = MutableLiveData(false)

    private val _trainings: LiveData<List<Training>> = forceUpdateTrainingAndExercises.switchMap {
        trainingsRepository.observeList().switchMap(::processTrainingsFromDB)
    }
    val trainings: LiveData<List<Training>> = _trainings

    private fun processTrainingsFromDB(
        result: Result<List<Training>>,
    ): LiveData<List<Training>> = if (result is Result.Success) {
        Timber.d("got new trainings list. size=${result.data.size}")
        MutableLiveData(result.data)
    } else {
        Timber.e("Error loading trainings")
        snackbarText.value = Event(R.string.error_trainings_list)
        MutableLiveData(emptyList())
    }


    private val _exercises: LiveData<List<ExerciseInTraining>> = forceUpdateTrainingAndExercises.switchMap {
        exercisesInTrainingsRepository.observeList().switchMap(::processExercisesFromDB)
    }
    val exercises: LiveData<List<ExerciseInTraining>> = _exercises

    private fun processExercisesFromDB(
        result: Result<List<ExerciseInTraining>>,
    ): LiveData<List<ExerciseInTraining>> = if (result is Result.Success) {
        Timber.d("got new exercisesInTrainings list. size=${result.data.size}")
        MutableLiveData(result.data)
    } else {
        Timber.e("Error loading exercisesInTrainings")
        snackbarText.value = Event(R.string.error_exercises_list)
        MutableLiveData(emptyList())
    }


    val openHero = MutableLiveData<Event<HumanType>>()
    fun openHero(humanType: HumanType) {
        openHero.value = Event(humanType)
    }


    val exportToFile = MutableLiveData<Event<Unit>>()
    val exportedToFile = MutableLiveData<Event<ImpExData>>()
    fun exportToFileRequested() {
        exportToFile.value = Event(Unit)
    }
    fun exportToFileConfirmed(exchangeDirName: String) {
        var trainingsExportedCount = 0
        var exercisesExportedCount = 0
        fun showExported() {
            if (trainingsExportedCount > 0 && exercisesExportedCount > 0)
                exportedToFile.value = Event(ImpExData(trainingsExportedCount, exercisesExportedCount))
        }

        val trainings = _trainings.value
        val exercises = _exercises.value
        if (trainings == null || trainings.isEmpty() || exercises == null || exercises.isEmpty()) {
            snackbarText.value = Event(R.string.no_exercises_or_trainings_found)
            return
        }
        viewModelScope.launch {
            val exported = FileUtils.exportToJSON(trainings, exchangeDirName, FileUtils.TRAININGS_FILE_NAME)
            if (exported == null) {
                snackbarText.value = Event(R.string.export_error)
            } else {
                trainingsExportedCount = exported
                showExported()
            }
        }

        viewModelScope.launch {
            val exported = FileUtils.exportToJSON(exercises, exchangeDirName, FileUtils.EXERCISES_FILE_NAME)
            if (exported == null) {
                snackbarText.value = Event(R.string.export_error)
            } else {
                exercisesExportedCount = exported
                showExported()
            }
        }
    }


    val importFromFile = MutableLiveData<Event<Unit>>()
    val importedFromFile = MutableLiveData<Event<ImpExData>>()
    fun importFromFileRequested() {
        importFromFile.value = Event(Unit)
    }
    fun importFromFileConfirmed(exchangeDirName: String) {
        var trainingsImportedCount = 0
        var exercisesImportedCount = 0
        fun showReadFromFile() {
            if (trainingsImportedCount > 0 && exercisesImportedCount > 0)
                importedFromFile.value = Event(ImpExData(trainingsImportedCount, exercisesImportedCount))
        }

        viewModelScope.launch {
            val list = FileUtils.readTrainingsJSON(exchangeDirName, FileUtils.TRAININGS_FILE_NAME)
            trainingsRepository.saveList(list)
            trainingsImportedCount = list.size
            showReadFromFile()
        }

        viewModelScope.launch {
            val list = FileUtils.readExercisesJSON(exchangeDirName, FileUtils.EXERCISES_FILE_NAME)
            exercisesInTrainingsRepository.saveList(list)
            exercisesImportedCount = list.size
            showReadFromFile()
        }
    }


    val clearDB = MutableLiveData<Event<Unit>>()
    fun clearDBRequested() {
        clearDB.value = Event(Unit)
    }
    fun clearDBConfirmed() {
        viewModelScope.launch {
            trainingsRepository.deleteAll()
            exercisesInTrainingsRepository.deleteAll()
            heroesRepository.deleteAll()
            snackbarText.value = Event(R.string.database_cleared)
        }
    }


    val loadDemoConfig = MutableLiveData<Event<Unit>>()
    fun loadDemoConfigRequested() {
        loadDemoConfig.value = Event(Unit)
    }

    val dbIsEmpty: LiveData<Boolean> = MutableLiveData(true)
}