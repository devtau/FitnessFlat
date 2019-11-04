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

class ExercisePresenterImpl(
    private val view: ExerciseView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager?,
    private val trainingId: Long?,
    private var exerciseId: Long?
): DBSubscriber(), ExercisePresenter {

    private var muscleGroups: List<MuscleGroup>? = null
    private var exercises: List<Exercise>? = null
    private var exercisesFiltered: List<Exercise>? = null
    private var exerciseInTraining: ExerciseInTraining? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(dataLayer.getMuscleGroups(Consumer {
            muscleGroups = it
            publishDataToView()
        }))
        disposeOnStop(dataLayer.getExercises(Consumer {
            exercises = it
            publishDataToView()
        }))
        val exerciseId = exerciseId
        if (exerciseId != null) dataLayer.getExerciseInTrainingAndClose(exerciseId, Consumer {
            exerciseInTraining = it
            publishDataToView()
        })
    }

    override fun updateExerciseData(exerciseIndex: Int, weight: String?, count: String?) {
        exerciseId = exercisesFiltered?.get(exerciseIndex)?.id
        val weightInt = try {
            weight?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            Logger.e(LOG_TAG, "bad data in weight field")
            0
        }
        val countInt = try {
            count?.toInt() ?: INTEGER_NOT_PARSED
        } catch (e: NumberFormatException) {
            Logger.e(LOG_TAG, "bad data in count field")
            INTEGER_NOT_PARSED
        }

        if (ExerciseInTraining.allObligatoryPartsPresent(trainingId, exerciseId, weightInt, countInt)) {
            if (exerciseInTraining == null) {
                exerciseInTraining = ExerciseInTraining(null, trainingId!!, exerciseId!!, weightInt, countInt)
            } else if (exerciseInTraining?.someFieldsChanged(exerciseId, weightInt, countInt) == true) {
                exerciseInTraining?.exerciseId = exerciseId
                exerciseInTraining?.weight = weightInt
                exerciseInTraining?.count = countInt
            }

            dataLayer.updateExercisesInTraining(listOf(exerciseInTraining))
        }
    }

    override fun provideExercises(): List<Exercise>? = exercises
    override fun filterAndUpdateList(muscleGroupIndex: Int) {
        val muscleGroupId = muscleGroups?.get(muscleGroupIndex)?.id
        exercisesFiltered = filter(exercises, muscleGroupId)
        view.showExercises(getSpinnerStrings2(exercisesFiltered), 0)
    }
    //</editor-fold>


    private fun publishDataToView() {
        if (AppUtils.isEmpty(muscleGroups) || AppUtils.isEmpty(exercises) || (exerciseId != null && exerciseInTraining == null)) return
        val exerciseInTraining = if (exerciseId == null) null else exerciseInTraining
        view.showMuscleGroups(getSpinnerStrings1(muscleGroups), 0)
        exercisesFiltered = filter(exercises, muscleGroups!![0].id)
        view.showExercises(getSpinnerStrings2(exercisesFiltered), 0)
        view.showExerciseDetails(exerciseInTraining)

        Logger.d(LOG_TAG, "publishDataToView. " +
                "muscleGroups=$muscleGroups, " +
                "exercises size=${exercises?.size}, " +
                "exerciseId=$exerciseId, " +
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


    companion object {
        private const val LOG_TAG = "ExercisePresenter"
    }
}