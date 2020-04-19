package com.devtau.ironHeroes.ui.fragments.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.devtau.ironHeroes.BaseViewModel
import com.devtau.ironHeroes.R
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

class OtherViewModel(
    private val trainingsRepository: TrainingsRepository,
    private val exercisesInTrainingsRepository: ExercisesInTrainingsRepository,
    private val heroesRepository: HeroesRepository
): BaseViewModel() {

    private val forceUpdateTrainingAndExercises = MutableLiveData<Boolean>(false)

    private val _trainings: LiveData<List<Training>> = forceUpdateTrainingAndExercises.switchMap {
        trainingsRepository.observeList().switchMap { processTrainingsFromDB(it, _snackbarText) }
    }
    val trainings: LiveData<List<Training>> = _trainings


    private val _exercises: LiveData<List<ExerciseInTraining>> = forceUpdateTrainingAndExercises.switchMap {
        exercisesInTrainingsRepository.observeList().switchMap { processExercisesFromDB(it, _snackbarText) }
    }
    val exercises: LiveData<List<ExerciseInTraining>> = _exercises


    private val _openHero = MutableLiveData<Event<HumanType>>()
    val openHero: LiveData<Event<HumanType>> = _openHero
    fun openHero(humanType: HumanType) {
        _openHero.value = Event(humanType)
    }


    private val _openDBViewer = MutableLiveData<Event<Unit>>()
    val openDBViewer: LiveData<Event<Unit>> = _openDBViewer
    fun openDBViewer() {
        _openDBViewer.value = Event(Unit)
    }


    private val _exportToFile = MutableLiveData<Event<Unit>>()
    val exportToFile: LiveData<Event<Unit>> = _exportToFile
    private val _exportedToFile = MutableLiveData<Event<ImpExData>>()
    val exportedToFile: LiveData<Event<ImpExData>> = _exportedToFile
    fun exportToFileRequested() {
        _exportToFile.value = Event(Unit)
    }
    fun exportToFileConfirmed(exchangeDirName: String) {
        var trainingsExportedCount = 0
        var exercisesExportedCount = 0
        fun showExported() {
            if (trainingsExportedCount > 0 && exercisesExportedCount > 0)
                _exportedToFile.value = Event(ImpExData(trainingsExportedCount, exercisesExportedCount))
        }

        val trainings = _trainings.value
        val exercises = _exercises.value
        if (trainings == null || trainings.isEmpty() || exercises == null || exercises.isEmpty()) {
            _snackbarText.value = Event(R.string.no_exercises_or_trainings_found)
            return
        }
        viewModelScope.launch {
            val exported = FileUtils.exportToJSON(trainings, exchangeDirName, FileUtils.TRAININGS_FILE_NAME)
            if (exported == null) {
                _snackbarText.value = Event(R.string.export_error)
            } else {
                trainingsExportedCount = exported
                showExported()
            }
        }

        viewModelScope.launch {
            val exported = FileUtils.exportToJSON(exercises, exchangeDirName, FileUtils.EXERCISES_FILE_NAME)
            if (exported == null) {
                _snackbarText.value = Event(R.string.export_error)
            } else {
                exercisesExportedCount = exported
                showExported()
            }
        }
    }


    private val _importFromFile = MutableLiveData<Event<Unit>>()
    val importFromFile: LiveData<Event<Unit>> = _importFromFile
    private val _importedFromFile = MutableLiveData<Event<ImpExData>>()
    val importedFromFile: LiveData<Event<ImpExData>> = _importedFromFile
    fun importFromFileRequested() {
        _importFromFile.value = Event(Unit)
    }
    fun importFromFileConfirmed(exchangeDirName: String) {
        var trainingsImportedCount = 0
        var exercisesImportedCount = 0
        fun showReadFromFile() {
            if (trainingsImportedCount > 0 && exercisesImportedCount > 0)
                _importedFromFile.value = Event(ImpExData(trainingsImportedCount, exercisesImportedCount))
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


    private val _clearDB = MutableLiveData<Event<Unit>>()
    val clearDB: LiveData<Event<Unit>> = _clearDB
    fun clearDBRequested() {
        _clearDB.value = Event(Unit)
    }
    fun clearDBConfirmed() {
        viewModelScope.launch {
            trainingsRepository.deleteAll()
            exercisesInTrainingsRepository.deleteAll()
            heroesRepository.deleteAll()
        }
    }


    companion object {
        private const val LOG_TAG = "OtherViewModel"
    }
}