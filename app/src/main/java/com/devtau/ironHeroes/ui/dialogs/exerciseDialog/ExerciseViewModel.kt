package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devtau.ironHeroes.BaseViewModel
import com.devtau.ironHeroes.adapters.IronSpinnerAdapter
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.*
import com.devtau.ironHeroes.data.model.wrappers.ExerciseDataWrapper
import com.devtau.ironHeroes.data.source.repositories.ExercisesInTrainingsRepository
import com.devtau.ironHeroes.data.source.repositories.ExercisesRepository
import com.devtau.ironHeroes.data.source.repositories.MuscleGroupsRepository
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepository
import com.devtau.ironHeroes.enums.DialogAction
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.*
import kotlinx.coroutines.launch
import java.util.*

class ExerciseViewModel(
    private val trainingsRepository: TrainingsRepository,
    private val muscleGroupsRepository: MuscleGroupsRepository,
    private val exercisesRepository: ExercisesRepository,
    private val exercisesInTrainingsRepository: ExercisesInTrainingsRepository,
    private val heroId: Long,
    private val trainingId: Long,
    private var exerciseInTrainingId: Long,
    private val position: Int
): BaseViewModel() {

    private var training: Training? = null
    private var exerciseInTraining: ExerciseInTraining? = null
    private var exercisesInTrainings: List<ExerciseInTraining>? = null
    var muscleGroups: List<MuscleGroup>? = null


    private val _selectedMuscleGroupId = MutableLiveData<Long?>(null)
    val selectedMuscleGroupId: LiveData<Long?> = _selectedMuscleGroupId
    val muscleGroupSelectedListener = object: IronSpinnerAdapter.ItemSelectedListener {
        override fun onItemSelected(item: SpinnerItem?, humanType: HumanType?) {
            if (_selectedMuscleGroupId.value != item?.id) {
                Logger.d(LOG_TAG, "onMuscleGroupSelected. id=${item?.id}")
                _selectedMuscleGroupId.value = item?.id
                _exercisesFiltered.value = filterExercises(exercises, selectedMuscleGroupId.value)
            }
        }
    }


    var exercises: List<Exercise>? = null
    private val _exercisesFiltered = MutableLiveData<List<Exercise>>()
    val exercisesFiltered: LiveData<List<Exercise>> = _exercisesFiltered
    private val _selectedExerciseId = MutableLiveData<Long?>(null)
    val selectedExerciseId: LiveData<Long?> = _selectedExerciseId
    val exerciseSelectedListener = object: IronSpinnerAdapter.ItemSelectedListener {
        override fun onItemSelected(item: SpinnerItem?, humanType: HumanType?) {
            if (_selectedExerciseId.value != item?.id) {
                Logger.d(LOG_TAG, "onExerciseSelected. id=${item?.id}")
                _selectedExerciseId.value = item?.id
                updatePreviousExerciseData(exerciseInTraining)
            }
        }
    }


    val weight = MutableLiveData("")
    val repeats = MutableLiveData("")
    val count = MutableLiveData("")
    val comment = MutableLiveData("")


    private val _showPreviousExerciseData = MutableLiveData<Event<ExerciseDataWrapper>>()
    val showPreviousExerciseData: LiveData<Event<ExerciseDataWrapper>> = _showPreviousExerciseData


    private val _startRecreationTimer = MutableLiveData<Event<Int>>()
    val startRecreationTimer: LiveData<Event<Int>> = _startRecreationTimer
    fun recreationClicked(number: Int) {
        _startRecreationTimer.value = Event(number)
    }


    private val _dismissDialog = MutableLiveData<Event<Unit>>()
    val dismissDialog: LiveData<Event<Unit>> = _dismissDialog
    fun dialogActionClicked(action: DialogAction) {
        Logger.d(LOG_TAG, "dialogActionClicked. action=$action")
        when(action) {
            DialogAction.CANCEL -> {/*no specific action required*/}
            DialogAction.DELETE -> deleteExercise()
            DialogAction.SAVE -> updateExerciseData()
        }
        _dismissDialog.value = Event(Unit)
    }


    private fun deleteExercise() {
        viewModelScope.launch {
            exerciseInTraining?.let {
                exercisesInTrainingsRepository.deleteItem(it)
            }
        }
    }

    private fun updateExerciseData() {
        val exerciseId = selectedExerciseId.value
        val exerciseInTraining = exerciseInTraining
        val weightInt = weight.value?.toIntOrNull() ?: 0
        val repeatsInt = repeats.value?.toIntOrNull() ?: Constants.INTEGER_NOT_PARSED
        val countInt = count.value?.toIntOrNull() ?: Constants.INTEGER_NOT_PARSED
        val comment = comment.value

        if (ExerciseInTraining.allObligatoryPartsPresent(trainingId, exerciseId, repeatsInt, countInt)) {
            if (exerciseInTraining == null) {
                viewModelScope.launch {
                    val tempExercise = ExerciseInTraining(null, trainingId, exerciseId!!,
                        weightInt, repeatsInt, countInt, position, comment)
                    exerciseInTrainingId = exercisesInTrainingsRepository.saveItem(tempExercise)
                }
            } else if (exerciseInTraining.someFieldsChanged(exerciseId, weightInt, countInt, repeatsInt, comment)) {
                exerciseInTraining.exerciseId = exerciseId
                exerciseInTraining.weight = weightInt
                exerciseInTraining.repeats = repeatsInt
                exerciseInTraining.count = countInt
                exerciseInTraining.comment = comment

                viewModelScope.launch {
                    exercisesInTrainingsRepository.saveItem(exerciseInTraining)
                }
            }
        }
    }

    private fun updatePreviousExerciseData(exerciseInTraining: ExerciseInTraining?) {
        val previous = getPreviousExerciseData(selectedExerciseId.value, training?.date, exercisesInTrainings)
        _showPreviousExerciseData.value = Event(ExerciseDataWrapper(
            previous?.training?.date, previous?.weight, previous?.repeats, previous?.count
        ))
        if (exerciseInTraining == null) showExerciseDetails(previous)
    }

    private fun getPreviousExerciseData(
        exerciseId: Long?, trainingDate: Long?, exercisesInTrainings: List<ExerciseInTraining>?
    ): ExerciseInTraining? {
        if (exerciseId == null || trainingDate == null || exercisesInTrainings == null || exercisesInTrainings.isEmpty()) return null
        else {
            val maxDate = Calendar.getInstance()
            maxDate.timeInMillis = trainingDate
            maxDate.add(Calendar.HOUR_OF_DAY, -2)
            Logger.d(LOG_TAG, "getPreviousExerciseData. maxDate=${DateUtils.formatDateTimeWithWeekDay(maxDate)}")
            exercisesInTrainings.print("getPreviousExerciseData")
            for (next in exercisesInTrainings) {
                val nextTrainingDate = Calendar.getInstance()
                nextTrainingDate.timeInMillis = next.training!!.date
                if (nextTrainingDate.before(maxDate) && exerciseId == next.exerciseId) return next
            }
            return null
        }
    }

    private fun filterExercises(exercises: List<Exercise>?, muscleGroupId: Long?): List<Exercise>  {
        if (exercises == null) return emptyList()
        val listFiltered = exercises.filter {
            muscleGroupId == null || it.muscleGroupId == muscleGroupId
        }
        listFiltered.print("filterExercises")
        return listFiltered
    }

    private suspend fun loadCommonData() {
        val resultExercises = exercisesRepository.getList(false)
        if (resultExercises is Result.Success) exercises = resultExercises.data

        val resultMuscleGroups = muscleGroupsRepository.getList(false)
        if (resultMuscleGroups is Result.Success) muscleGroups = resultMuscleGroups.data

        val resultExercisesInTrainings = exercisesInTrainingsRepository.getListForHero(heroId)
        if (resultExercisesInTrainings is Result.Success) exercisesInTrainings = resultExercisesInTrainings.data
    }

    private fun initDataForNewExerciseInTraining() {
        training = null
        exerciseInTraining = null
        _selectedMuscleGroupId.value = muscleGroups?.get(0)?.id
        _exercisesFiltered.value = filterExercises(exercises, selectedMuscleGroupId.value)
        _selectedExerciseId.value = _exercisesFiltered.value?.get(0)?.id
        updatePreviousExerciseData(null)
    }

    private suspend fun initDataForExistingExerciseInTraining(trainingId: Long, exerciseInTrainingId: Long) {
        val resultTraining = trainingsRepository.getItem(trainingId)
        if (resultTraining is Result.Success) training = resultTraining.data

        val resultExerciseInTraining = exercisesInTrainingsRepository.getItem(exerciseInTrainingId)
        if (resultExerciseInTraining is Result.Success) {
            exerciseInTraining = resultExerciseInTraining.data

            _selectedMuscleGroupId.value = exerciseInTraining?.exercise?.muscleGroupId
            _exercisesFiltered.value = filterExercises(exercises, selectedMuscleGroupId.value)
            _selectedExerciseId.value = exerciseInTraining?.exerciseId
            showExerciseDetails(exerciseInTraining)
            updatePreviousExerciseData(exerciseInTraining)
        }
    }

    private fun showExerciseDetails(exerciseInTraining: ExerciseInTraining?) {
        weight.value = exerciseInTraining?.weight?.toString()
        repeats.value = exerciseInTraining?.repeats?.toString() ?: ExerciseInTraining.DEFAULT_REPEATS
        count.value = exerciseInTraining?.count?.toString() ?: ExerciseInTraining.DEFAULT_COUNT
    }


    init {
        viewModelScope.launch {
            loadCommonData()

            if (exerciseInTrainingId == Constants.OBJECT_ID_NA) {
                initDataForNewExerciseInTraining()
            } else {
                initDataForExistingExerciseInTraining(trainingId, exerciseInTrainingId)
            }
        }
    }


    companion object {
        private const val LOG_TAG = "ExerciseViewModel"
    }
}