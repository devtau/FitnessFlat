package com.devtau.ironHeroes.ui.fragments.trainingDetails

import androidx.lifecycle.*
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.IronSpinnerAdapter.ItemSelectedListener
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.SpinnerItem
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.model.wrappers.DatePickerDialogDataWrapper
import com.devtau.ironHeroes.data.model.wrappers.EditDialogDataWrapper
import com.devtau.ironHeroes.data.source.repositories.ExercisesInTrainingsRepository
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepository
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.DateUtils
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.prefs.PreferencesManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class TrainingDetailsViewModel(
    private val trainingsRepository: TrainingsRepository,
    private val heroesRepository: HeroesRepository,
    private val exercisesInTrainingsRepository: ExercisesInTrainingsRepository,
    private val prefs: PreferencesManager?,
    private var trainingId: Long?
): ViewModel() {

    private val _training = MutableLiveData<Training?>(null)
    val training: LiveData<Training?> = _training


    private val forceUpdateHeroes = MutableLiveData(false)


    private val _trainingDate = MutableLiveData<String>()
    val trainingDate: LiveData<String> = _trainingDate
    fun updateTrainingDate(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        val date = DateUtils.getRoundDate(year, month, dayOfMonth, hour, minute)
        _trainingDate.value = DateUtils.formatDateTimeWithWeekDay(date)
        updateTraining()
    }
    fun deleteTraining() {
        val training = training.value
        val trainingId = training?.id
        viewModelScope.launch {
            if (trainingId != null) {
                trainingsRepository.deleteItem(training)
                exercisesInTrainingsRepository.deleteListForTraining(trainingId)
            }
            _closeScreenEvent.value = Event(Unit)
        }
    }


    val toolbarTitle: LiveData<Event<Int>> = MutableLiveData(Event(
        if (trainingId == null) R.string.training_add else R.string.training_edit
    ))


    private val _exercises: LiveData<List<ExerciseInTraining>?> = _training.switchMap { training ->
        exercisesInTrainingsRepository.observeListForTraining(training?.id).switchMap {
            processExercisesFromDB(it)
        }
    }
    val exercises: LiveData<List<ExerciseInTraining>?> = _exercises
    fun onExerciseMoved(fromPosition: Int, toPosition: Int) {
        val exercises = _exercises.value as ArrayList<ExerciseInTraining>?
        if (exercises == null || exercises.isEmpty()) return
        val item = exercises.removeAt(fromPosition)
        exercises.add(toPosition, item)

        for ((i, next) in exercises.withIndex()) next.position = i
        viewModelScope.launch {
            exercisesInTrainingsRepository.saveList(exercises)
        }

        runBlocking {
            launch {
                delay(200L)
                println("Hello 1")
            }

            coroutineScope {
                launch {
                    delay(500L)
                    println("Hello 2")
                }

                delay(100L)
                println("Hello 3")

            }

            println("Hello 4")
        }
    }


    val champions: LiveData<List<Hero>> = forceUpdateHeroes.switchMap {
        heroesRepository.observeList(HumanType.CHAMPION).switchMap {
            MutableLiveData(if (it is Success) it.data else emptyList())
        }
    }
    private val _selectedChampionId = MutableLiveData<Long?>()
    val selectedChampionId: LiveData<Long?> = _selectedChampionId


    val heroes: LiveData<List<Hero>> = forceUpdateHeroes.switchMap {
        heroesRepository.observeList(HumanType.HERO).switchMap {
            MutableLiveData(if (it is Success) it.data else emptyList())
        }
    }
    private val _selectedHeroId = MutableLiveData<Long?>()
    val selectedHeroId: LiveData<Long?> = _selectedHeroId


    val heroSelectedListener = object: ItemSelectedListener {
        override fun onItemSelected(item: SpinnerItem?, humanType: HumanType?) {
            when (humanType) {
                HumanType.HERO -> {
                    if (_selectedHeroId.value != item?.id) {
                        _selectedHeroId.value = item?.id
                        updateTraining()
                        Logger.d(LOG_TAG, "onHeroSelected. heroId=${item?.id}, humanType=$humanType")
                    }
                }
                HumanType.CHAMPION -> {
                    if (_selectedChampionId.value != item?.id) {
                        _selectedChampionId.value = item?.id
                        updateTraining()
                        Logger.d(LOG_TAG, "onHeroSelected. heroId=${item?.id}, humanType=$humanType")
                    }
                }
            }
        }
    }


    private val _showDateDialog = MutableLiveData<Event<DatePickerDialogDataWrapper>>()
    val showDateDialog: LiveData<Event<DatePickerDialogDataWrapper>> = _showDateDialog
    fun dateDialogRequested() {
        val trainingDate = training.value?.date
        val selectedDate = Calendar.getInstance()
        if (trainingDate != null) selectedDate.timeInMillis = trainingDate

        val nowMinusCentury = Calendar.getInstance()
        nowMinusCentury.add(Calendar.YEAR, -100)

        val nowPlusTwoDays = Calendar.getInstance()
        nowPlusTwoDays.add(Calendar.DAY_OF_MONTH, 2)

        _showDateDialog.value = Event(DatePickerDialogDataWrapper(selectedDate, nowMinusCentury, nowPlusTwoDays))
    }


    private val _openExerciseEvent = MutableLiveData<Event<EditDialogDataWrapper>>()
    val openExerciseEvent: LiveData<Event<EditDialogDataWrapper>> = _openExerciseEvent
    fun openExercise() = openExercise(null)//null for add new item
    fun openExercise(item: ExerciseInTraining?) {
        val heroId = training.value?.heroId
        val trainingId = training.value?.id
        if (heroId == null || trainingId == null) {
            Logger.e(LOG_TAG, "openExercise. bad data. aborting")
            return
        }
        _openExerciseEvent.value = Event(EditDialogDataWrapper(
            heroId,
            trainingId,
            item?.id ?: Constants.OBJECT_ID_NA,
            item?.position ?: getNextExercisePosition()
        ))
    }

    private val _closeScreenEvent = MutableLiveData<Event<Unit>>()
    val closeScreenEvent: LiveData<Event<Unit>> = _closeScreenEvent


    private fun processExercisesFromDB(result: Result<List<ExerciseInTraining>?>): LiveData<List<ExerciseInTraining>?> =
        if (result is Success && result.data != null) {
            Logger.d(LOG_TAG, "got new exercises=${result.data}")
            MutableLiveData(result.data)
        } else {
            MutableLiveData(null)
        }

    private fun getNextExercisePosition(): Int = with(training.value?.exercises) {
        if (this == null || this.isEmpty()) 0
        else this.maxBy { it.position }!!.position + 1
    }

    private fun updateTraining() {
        val training = _training.value
        val championId = _selectedChampionId.value
        val heroId = _selectedHeroId.value
        val date = DateUtils.parseDateTimeWithWeekDay(_trainingDate.value)?.timeInMillis
        if (!Training.allObligatoryPartsPresent(championId, heroId, date)) {
            Logger.w(LOG_TAG, "updateTraining. some data missing. aborting")
            return
        }

        Logger.d(LOG_TAG, "updateTraining. training={$training}, championId=$championId, heroId=$heroId," +
                " date=${DateUtils.formatDateTimeWithWeekDay(date)}")
        if (trainingId == null || training == null) {
            viewModelScope.launch {
                val newTraining = Training(null, championId!!, heroId!!, date!!)
                trainingId = trainingsRepository.saveItem(newTraining)
                newTraining.id = trainingId
                _training.value = newTraining
            }
            return
        }

        if (training.someFieldsChanged(championId, heroId, date)) {
            viewModelScope.launch {
                val updatedTraining = Training(trainingId, championId!!, heroId!!, date!!)
                trainingsRepository.saveItem(updatedTraining)
                _training.value = updatedTraining
            }
        }
    }

    private fun getSelectedHumanId(prefs: PreferencesManager?, humanType: HumanType, training: Training? = null): Long? = when (humanType) {
        HumanType.HERO -> training?.heroId ?: prefs?.favoriteHeroId
        HumanType.CHAMPION -> training?.championId ?: prefs?.favoriteChampionId
    }


    init {
        if (trainingId == null) {
            _training.value = null
            _trainingDate.value = DateUtils.formatDateTimeWithWeekDay(DateUtils.getRoundDate())
            _selectedChampionId.value = getSelectedHumanId(prefs, HumanType.CHAMPION)
            _selectedHeroId.value = getSelectedHumanId(prefs, HumanType.HERO)
        } else {
            viewModelScope.launch {
                val result = trainingsRepository.getItem(trainingId)
                if (result is Success && result.data != null) {
                    _training.value = result.data
                    _trainingDate.value = result.data.formatDateTimeWithWeekDay()
                    _selectedChampionId.value = getSelectedHumanId(prefs, HumanType.CHAMPION, result.data)
                    _selectedHeroId.value = getSelectedHumanId(prefs, HumanType.HERO, result.data)
                }
            }
        }
    }


    companion object {
        private const val LOG_TAG = "TrainingDetailsViewModel"
    }
}