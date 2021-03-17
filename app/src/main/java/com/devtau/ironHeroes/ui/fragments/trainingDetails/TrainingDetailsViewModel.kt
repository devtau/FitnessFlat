package com.devtau.ironHeroes.ui.fragments.trainingDetails

import androidx.lifecycle.*
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
import com.devtau.ironHeroes.util.prefs.PreferencesManager
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class TrainingDetailsViewModel(
    private val trainingsRepository: TrainingsRepository,
    private val heroesRepository: HeroesRepository,
    private val exercisesInTrainingsRepository: ExercisesInTrainingsRepository,
    private val prefs: PreferencesManager?,
    private var trainingId: Long?
): ViewModel() {

    val training = MutableLiveData<Training?>(null)


    private val forceUpdateHeroes = MutableLiveData(false)


    val trainingDate = MutableLiveData<String>()
    fun updateTrainingDate(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        val date = DateUtils.getRoundDate(year, month, dayOfMonth, hour, minute)
        trainingDate.value = DateUtils.formatDateTimeWithWeekDay(date)
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
            closeScreenEvent.value = Event(Unit)
        }
    }


    private val _exercises: LiveData<List<ExerciseInTraining>?> = training.switchMap { training ->
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
    }


    val champions: LiveData<List<Hero>> = forceUpdateHeroes.switchMap {
        heroesRepository.observeList(HumanType.CHAMPION).switchMap {
            MutableLiveData(if (it is Success) it.data else emptyList())
        }
    }
    val selectedChampionId = MutableLiveData<Long?>()


    val heroes: LiveData<List<Hero>> = forceUpdateHeroes.switchMap {
        heroesRepository.observeList(HumanType.HERO).switchMap {
            MutableLiveData(if (it is Success) it.data else emptyList())
        }
    }
    val selectedHeroId = MutableLiveData<Long?>()


    val heroSelectedListener = object: ItemSelectedListener {
        override fun onItemSelected(item: SpinnerItem?, humanType: HumanType?) {
            when (humanType) {
                HumanType.HERO -> {
                    if (selectedHeroId.value != item?.id) {
                        selectedHeroId.value = item?.id
                        updateTraining()
                    }
                }
                HumanType.CHAMPION -> {
                    if (selectedChampionId.value != item?.id) {
                        selectedChampionId.value = item?.id
                        updateTraining()
                    }
                }
            }
        }
    }


    val showDateDialog = MutableLiveData<Event<DatePickerDialogDataWrapper>>()
    fun dateDialogRequested() {
        val trainingDate = training.value?.date
        val selectedDate = Calendar.getInstance()
        if (trainingDate != null) selectedDate.timeInMillis = trainingDate

        val nowMinusCentury = Calendar.getInstance()
        nowMinusCentury.add(Calendar.YEAR, -100)

        val nowPlusTwoDays = Calendar.getInstance()
        nowPlusTwoDays.add(Calendar.DAY_OF_MONTH, 2)

        showDateDialog.value = Event(DatePickerDialogDataWrapper(selectedDate, nowMinusCentury, nowPlusTwoDays))
    }


    val openExerciseEvent = MutableLiveData<Event<EditDialogDataWrapper>>()
    fun openExercise() = openExercise(null)//null for add new item
    fun openExercise(item: ExerciseInTraining?) {
        val heroId = training.value?.heroId
        val trainingId = training.value?.id
        if (heroId == null || trainingId == null) {
            Timber.e("openExercise. bad data. aborting")
            return
        }
        openExerciseEvent.value = Event(EditDialogDataWrapper(
            heroId,
            trainingId,
            item?.id ?: Constants.OBJECT_ID_NA,
            item?.position ?: getNextExercisePosition()
        ))
    }

    val closeScreenEvent = MutableLiveData<Event<Unit>>()


    private fun processExercisesFromDB(result: Result<List<ExerciseInTraining>?>): LiveData<List<ExerciseInTraining>?> =
        if (result is Success && result.data != null) {
            Timber.d("got new exercises=${result.data}")
            MutableLiveData(result.data)
        } else {
            MutableLiveData(null)
        }

    private fun getNextExercisePosition(): Int = with(training.value?.exercises) {
        val max = this?.maxByOrNull { it.position }
        if (max == null) 0 else max.position + 1
    }

    private fun updateTraining() {
        val training = training.value
        val championId = selectedChampionId.value
        val heroId = selectedHeroId.value
        val date = DateUtils.parseDateTimeWithWeekDay(trainingDate.value)?.timeInMillis
        if (!Training.allObligatoryPartsPresent(championId, heroId, date)) {
            Timber.w("updateTraining. some data missing. aborting")
            return
        }

        Timber.d("updateTraining. training={$training}, championId=$championId, heroId=$heroId, date=${DateUtils.formatDateTimeWithWeekDay(date)}")
        if (trainingId == null || training == null) {
            viewModelScope.launch {
                val newTraining = Training(null, championId!!, heroId!!, date!!)
                trainingId = trainingsRepository.saveItem(newTraining)
                newTraining.id = trainingId
                this@TrainingDetailsViewModel.training.value = newTraining
            }
            return
        }

        if (training.someFieldsChanged(championId, heroId, date)) {
            viewModelScope.launch {
                val updatedTraining = Training(trainingId, championId!!, heroId!!, date!!)
                trainingsRepository.saveItem(updatedTraining)
                this@TrainingDetailsViewModel.training.value = updatedTraining
            }
        }
    }

    private fun getSelectedHumanId(prefs: PreferencesManager?, humanType: HumanType, training: Training? = null): Long? = when (humanType) {
        HumanType.HERO -> training?.heroId ?: prefs?.favoriteHeroId
        HumanType.CHAMPION -> training?.championId ?: prefs?.favoriteChampionId
    }


    init {
        if (trainingId == null) {
            training.value = null
            trainingDate.value = DateUtils.formatDateTimeWithWeekDay(DateUtils.getRoundDate())
            selectedChampionId.value = getSelectedHumanId(prefs, HumanType.CHAMPION)
            selectedHeroId.value = getSelectedHumanId(prefs, HumanType.HERO)
        } else {
            viewModelScope.launch {
                val result = trainingsRepository.getItem(trainingId)
                if (result is Success && result.data != null) {
                    training.value = result.data
                    trainingDate.value = result.data.formatDateTimeWithWeekDay()
                    selectedChampionId.value = getSelectedHumanId(prefs, HumanType.CHAMPION, result.data)
                    selectedHeroId.value = getSelectedHumanId(prefs, HumanType.HERO, result.data)
                }
            }
        }
    }
}