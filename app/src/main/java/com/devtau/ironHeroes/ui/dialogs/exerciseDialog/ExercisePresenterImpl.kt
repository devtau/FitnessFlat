package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.DataObject
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.INTEGER_NOT_PARSED
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import io.reactivex.functions.Consumer
import java.util.*
import kotlin.collections.ArrayList

class ExercisePresenterImpl(
    private val view: ExerciseView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager?,
    private val trainingId: Long?,
    private var exerciseInTrainingId: Long?
): DBSubscriber(), ExercisePresenter {

    private var muscleGroups: List<MuscleGroup>? = null
    private var exercises: List<Exercise>? = null
    private var exercisesFiltered: List<Exercise>? = null
    private var exerciseInTraining: ExerciseInTraining? = null
    private var exercisesInTrainings: List<ExerciseInTraining>? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(dataLayer.getMuscleGroups(Consumer {
            muscleGroups = it
            prepareAndPublishDataToView()
        }))
        disposeOnStop(dataLayer.getExercises(Consumer {
            exercises = it
            prepareAndPublishDataToView()
        }))
        val exerciseInTrainingId = exerciseInTrainingId
        if (exerciseInTrainingId != null) dataLayer.getExerciseInTrainingAndClose(exerciseInTrainingId, Consumer {
            exerciseInTraining = it
            prepareAndPublishDataToView()
        })

        val trainingId = trainingId
        if (trainingId != null) {
            dataLayer.getTrainingByIdAndClose(trainingId, Consumer { training ->
                val maxDate = Calendar.getInstance()
                if (training != null) maxDate.timeInMillis = training.date
                maxDate.add(Calendar.HOUR_OF_DAY, -2)
                dataLayer.getAllExercisesInTrainingsAndClose(3L, maxDate.timeInMillis, Consumer {
                    exercisesInTrainings = it
                    prepareAndPublishDataToView()
                })
            })
        } else {
            val maxDate = Calendar.getInstance()
            maxDate.add(Calendar.HOUR_OF_DAY, -2)
            dataLayer.getAllExercisesInTrainingsAndClose(3L, maxDate.timeInMillis, Consumer {
                exercisesInTrainings = it
                prepareAndPublishDataToView()
            })
        }
    }

    override fun updateExerciseData(exerciseIndex: Int, weight: String?, count: String?, comment: String?) {
        exerciseInTrainingId = exercisesFiltered?.get(exerciseIndex)?.id
        val weightInt = try {
            weight?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            Logger.i(LOG_TAG, "bad data in weight field")
            0
        }
        val countInt = try {
            count?.toInt() ?: INTEGER_NOT_PARSED
        } catch (e: NumberFormatException) {
            Logger.e(LOG_TAG, "bad data in count field")
            INTEGER_NOT_PARSED
        }

        if (ExerciseInTraining.allObligatoryPartsPresent(trainingId, exerciseInTrainingId, weightInt, countInt)) {
            if (exerciseInTraining == null) {
                exerciseInTraining = ExerciseInTraining(null, trainingId!!, exerciseInTrainingId!!, weightInt, countInt)
            } else if (exerciseInTraining?.someFieldsChanged(exerciseInTrainingId, weightInt, countInt, comment) == true) {
                exerciseInTraining?.exerciseId = exerciseInTrainingId
                exerciseInTraining?.weight = weightInt
                exerciseInTraining?.count = countInt
                exerciseInTraining?.comment = comment
            }

            dataLayer.updateExercisesInTraining(listOf(exerciseInTraining))
        }
    }

    override fun deleteExercise() {
        dataLayer.deleteExercisesInTraining(listOf(exerciseInTraining))
    }

    override fun provideExercises(): List<Exercise>? = exercises
    override fun filterAndUpdateList(muscleGroupIndex: Int) {
        val muscleGroupId = muscleGroups?.get(muscleGroupIndex)?.id
        exercisesFiltered = filter(exercises, muscleGroupId)
        val selectedExerciseIndex = if (exerciseInTraining == null) 0
        else getSelectedItemIndex(exercisesFiltered, exerciseInTraining?.exerciseId)
        view.showExercises(getSpinnerStrings2(exercisesFiltered), selectedExerciseIndex)
    }

    override fun updatePreviousExerciseData(exerciseIndex: Int) {
        val selectedExerciseId = exercisesFiltered?.get(exerciseIndex)?.id
        val previous = getPreviousExerciseData(selectedExerciseId)
        view.showPreviousExerciseData(previous?.training?.date, previous?.weight, previous?.count)
        if (exerciseInTraining == null) view.showExerciseDetails(previous?.weight, previous?.count, previous?.comment)
    }
    //</editor-fold>


    private fun prepareAndPublishDataToView() {
        fun applyMuscleGroupDetails(exerciseInTraining: ExerciseInTraining) {
            for (next in muscleGroups!!)
                if (exerciseInTraining.exercise?.muscleGroupId == next.id)
                    exerciseInTraining.exercise?.muscleGroup = next
        }

        val exerciseInTraining = exerciseInTraining
        if (AppUtils.isEmpty(muscleGroups) || AppUtils.isEmpty(exercises) ||
            (exerciseInTrainingId != null && exerciseInTraining == null)) return

        if (exerciseInTraining == null) {
            view.showMuscleGroups(getSpinnerStrings1(muscleGroups), 0)
            exercisesFiltered = filter(exercises, muscleGroups!![0].id)
            view.showExercises(getSpinnerStrings2(exercisesFiltered), 0)

            val previous = getPreviousExerciseData(exercisesFiltered?.get(0)?.id)
            view.showPreviousExerciseData(previous?.training?.date, previous?.weight, previous?.count)
            view.showExerciseDetails(previous?.weight, previous?.count, previous?.comment)
        } else {
            applyMuscleGroupDetails(exerciseInTraining)

            val selectedMuscleGroupId = exerciseInTraining.exercise?.muscleGroupId
            val selectedMuscleGroupIndex = getSelectedItemIndex(muscleGroups, selectedMuscleGroupId)
            view.showMuscleGroups(getSpinnerStrings1(muscleGroups), selectedMuscleGroupIndex)

            exercisesFiltered = filter(exercises, selectedMuscleGroupId)
            val selectedExerciseIndex = getSelectedItemIndex(exercisesFiltered, exerciseInTraining.exerciseId)
            view.showExercises(getSpinnerStrings2(exercisesFiltered), selectedExerciseIndex)

            val previous = getPreviousExerciseData(exerciseInTraining.exercise?.id)
            view.showPreviousExerciseData(previous?.training?.date, previous?.weight, previous?.count)
            view.showExerciseDetails(exerciseInTraining.weight, exerciseInTraining.count, exerciseInTraining?.comment)
        }

        Logger.d(LOG_TAG, "publishDataToView. " +
                "muscleGroups size=${muscleGroups?.size}, " +
                "exercises size=${exercises?.size}, " +
                "exerciseInTrainingId=$exerciseInTrainingId, " +
                "exerciseInTraining=$exerciseInTraining")
    }

    private fun getSpinnerStrings1(list: List<MuscleGroup>?): List<String> {
        val spinnerStrings = ArrayList<String>()
        if (list != null) for (next in list) spinnerStrings.add(next.name)
        return spinnerStrings
    }

    private fun getSpinnerStrings2(list: List<Exercise>?): List<String> {
        val spinnerStrings = ArrayList<String>()
        if (list != null) for (next in list) spinnerStrings.add(next.name)
        return spinnerStrings
    }

    private fun getSelectedItemIndex(list: List<DataObject>?, selectedId: Long?): Int {
        var index = 0
        if (list != null) for (i in list.indices)
            if (list[i].id == selectedId) index = i
        return index
    }

    private fun filter(list: List<Exercise>?, muscleGroupId: Long?): List<Exercise> {
        val filtered = ArrayList<Exercise>()
        if (list != null) for (next in list)
            if (muscleGroupId == null || next.muscleGroupId == muscleGroupId) filtered.add(next)
        Logger.d(LOG_TAG, "filter. list size=${list?.size}, muscleGroupId=$muscleGroupId, filtered size=${filtered.size}")
        return filtered
    }

    private fun getPreviousExerciseData(exerciseId: Long?): ExerciseInTraining? {
        when {
            exerciseId == null -> return null
            exercisesInTrainings != null -> {
                for (next in exercisesInTrainings!!)
                    if (exerciseId == next.exerciseId)
                        return next
                return null
            }
            else -> return null
        }
    }




    companion object {
        private const val LOG_TAG = "ExercisePresenter"
    }
}