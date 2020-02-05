package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import com.devtau.ironHeroes.data.dao.ExerciseDao
import com.devtau.ironHeroes.data.dao.ExerciseInTrainingDao
import com.devtau.ironHeroes.data.dao.MuscleGroupDao
import com.devtau.ironHeroes.data.dao.TrainingDao
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.relations.ExerciseInTrainingRelation
import com.devtau.ironHeroes.data.relations.ExerciseRelation
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.INTEGER_NOT_PARSED
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.print
import io.reactivex.functions.Consumer
import java.util.*

class ExercisePresenterImpl(
    private val view: ExerciseContract.View,
    private val trainingDao: TrainingDao,
    private val exerciseDao: ExerciseDao,
    private val muscleGroupDao: MuscleGroupDao,
    private val exerciseInTrainingDao: ExerciseInTrainingDao,
    private val heroId: Long,
    private val trainingId: Long?,
    private var exerciseInTrainingId: Long?,
    private val position: Int?
): DBSubscriber(), ExerciseContract.Presenter {

    private var training: Training? = null
    private val muscleGroups = mutableListOf<MuscleGroup>()
    private val exercises = mutableListOf<Exercise>()
    private var exercisesFiltered = mutableListOf<Exercise>()
    private var exerciseInTraining: ExerciseInTraining? = null
    private var exercisesInTrainings = mutableListOf<ExerciseInTraining>()


    //<editor-fold desc="Presenter overrides">
    override fun onStop() {
        muscleGroups.clear()
        exercises.clear()
        training = null
        exerciseInTraining = null
    }

    override fun restartLoaders() {
        disposeOnStop(muscleGroupDao.getList()
            .subscribeDefault(Consumer {
                muscleGroups.clear()
                muscleGroups.addAll(it)
                prepareAndPublishDataToView()
            }, "muscleGroupDao.getList"))

        disposeOnStop(exerciseDao.getList()
            .map { relation -> ExerciseRelation.convertList(relation) }
            .subscribeDefault(Consumer {
                exercises.clear()
                exercises.addAll(it)
                prepareAndPublishDataToView()
            }, "exerciseDao.getList"))

        disposeOnStop(exerciseInTrainingDao.getListForHeroDesc(heroId)
            .map { relation -> ExerciseInTrainingRelation.convertList(relation) }
            .subscribeDefault(Consumer { exercises ->
                exercisesInTrainings = exercises as MutableList
                prepareAndPublishDataToView()
            }, "exerciseInTrainingDao.getListAsc"))

        exerciseInTrainingId?.let { disposeOnStop(exerciseInTrainingDao.getById(it)
            .map { relation -> relation.convert() }
            .subscribeDefault(Consumer { exercise ->
                exerciseInTraining = exercise
                prepareAndPublishDataToView()
            }, "exerciseInTrainingDao.getById")) }

        trainingId?.let { disposeOnStop(trainingDao.getById(it)
            .map { relation -> relation.convert() }
            .subscribeDefault(Consumer { training ->
                this.training = training
                prepareAndPublishDataToView()
            }, "trainingDao.getById"))}
    }

    override fun updateExerciseData(exerciseIndex: Int, weight: String?, repeats: String?, count: String?, comment: String?) {
        if (exercisesFiltered.isEmpty()) return
        exerciseInTrainingId = exercisesFiltered[exerciseIndex].id
        val weightInt = weight?.toIntOrNull() ?: 0
        val repeatsInt = repeats?.toIntOrNull() ?: INTEGER_NOT_PARSED
        val countInt = count?.toIntOrNull() ?: INTEGER_NOT_PARSED

        if (ExerciseInTraining.allObligatoryPartsPresent(trainingId, exerciseInTrainingId, repeatsInt, countInt)) {
            if (exerciseInTraining == null && position != null) {
                exerciseInTraining = ExerciseInTraining(null, trainingId!!, exerciseInTrainingId!!,
                    weightInt, repeatsInt, countInt, position!!, comment)
            } else if (exerciseInTraining?.someFieldsChanged(exerciseInTrainingId, weightInt, countInt, repeatsInt, comment) == true) {
                exerciseInTraining?.exerciseId = exerciseInTrainingId
                exerciseInTraining?.weight = weightInt
                exerciseInTraining?.repeats = repeatsInt
                exerciseInTraining?.count = countInt
                exerciseInTraining?.comment = comment
            }

            exerciseInTrainingDao.insert(listOf(exerciseInTraining)).subscribeDefault("updateExercisesInTraining. inserted")
        }
    }

    override fun deleteExercise() {
        exerciseInTrainingDao.delete(listOf(exerciseInTraining)).subscribeDefault("deleteExercisesInTraining. deleted")
    }

    override fun provideExercises(): List<Exercise>? = exercises
    override fun filterAndUpdateList(muscleGroupIndex: Int) {
        val muscleGroupId = muscleGroups[muscleGroupIndex].id
        exercisesFiltered = filterExercisesByMuscleGroup(exercises, muscleGroupId)
        val selectedExerciseIndex = if (exerciseInTraining == null) 0
        else AppUtils.getSelectedExerciseIndex(exercisesFiltered, exerciseInTraining?.exerciseId)
        view.showExercises(AppUtils.getExercisesSpinnerStrings(exercisesFiltered), selectedExerciseIndex)
    }

    override fun updatePreviousExerciseData(exerciseIndex: Int) {
        val selectedExerciseId = exercisesFiltered[exerciseIndex].id
        val previous = getPreviousExerciseData(selectedExerciseId, training)
        view.showPreviousExerciseData(previous?.training?.date, previous?.weight, previous?.repeats, previous?.count)
        if (exerciseInTraining == null) view.showExerciseDetails(previous?.weight, previous?.repeats, previous?.count, previous?.comment)
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
    private fun prepareAndPublishDataToView() {
        fun applyMuscleGroupDetails(exerciseInTraining: ExerciseInTraining) {
            for (next in muscleGroups)
                if (exerciseInTraining.exercise?.muscleGroupId == next.id)
                    exerciseInTraining.exercise?.muscleGroup = next
        }

        val exerciseInTraining = exerciseInTraining
        if (AppUtils.isEmpty(muscleGroups) || AppUtils.isEmpty(exercises) ||
            (exerciseInTrainingId != null && exerciseInTraining == null) ||
            (trainingId != null && training == null)) return

        if (exerciseInTraining == null) {
            view.showMuscleGroups(AppUtils.getMuscleGroupsSpinnerStrings(muscleGroups), 0)
            exercisesFiltered = filterExercisesByMuscleGroup(exercises, muscleGroups[0].id)
            view.showExercises(AppUtils.getExercisesSpinnerStrings(exercisesFiltered), 0)

            val previous = if (exercisesFiltered.isEmpty()) null else getPreviousExerciseData(exercisesFiltered[0].id, training)
            view.showPreviousExerciseData(previous?.training?.date, previous?.weight, previous?.repeats, previous?.count)
            view.showExerciseDetails(previous?.weight, previous?.repeats, previous?.count, previous?.comment)
        } else {
            applyMuscleGroupDetails(exerciseInTraining)

            val selectedMuscleGroupId = exerciseInTraining.exercise?.muscleGroupId
            val selectedMuscleGroupIndex = AppUtils.getSelectedMuscleGroupIndex(muscleGroups, selectedMuscleGroupId)
            view.showMuscleGroups(AppUtils.getMuscleGroupsSpinnerStrings(muscleGroups), selectedMuscleGroupIndex)

            exercisesFiltered = filterExercisesByMuscleGroup(exercises, selectedMuscleGroupId)
            val selectedExerciseIndex = AppUtils.getSelectedExerciseIndex(exercisesFiltered, exerciseInTraining.exerciseId)
            view.showExercises(AppUtils.getExercisesSpinnerStrings(exercisesFiltered), selectedExerciseIndex)

            val previous = getPreviousExerciseData(exerciseInTraining.exercise?.id, training)
            view.showPreviousExerciseData(previous?.training?.date, previous?.weight, previous?.repeats, previous?.count)
            view.showExerciseDetails(exerciseInTraining.weight, exerciseInTraining.repeats, exerciseInTraining.count, exerciseInTraining.comment)
        }

        Logger.d(LOG_TAG, "publishDataToView. " +
                "muscleGroups size=${muscleGroups.size}, " +
                "exercises size=${exercises.size}, " +
                "exerciseInTrainingId=$exerciseInTrainingId, " +
                "exerciseInTraining=$exerciseInTraining")
    }

    private fun filterExercisesByMuscleGroup(list: List<Exercise>, muscleGroupId: Long?): MutableList<Exercise>  {
        val listFiltered = list.filter { muscleGroupId == null || it.muscleGroupId == muscleGroupId } as MutableList
        listFiltered.print("filterExercisesByMuscleGroup")
        return listFiltered
    }

    private fun filterExercisesByTrainingDate(list: MutableList<ExerciseInTraining>, training: Training?): MutableList<ExerciseInTraining> {
        if (training == null || list.isEmpty()) return list
        val maxDate = Calendar.getInstance()
        maxDate.timeInMillis = training.date
        maxDate.add(Calendar.HOUR_OF_DAY, -2)
        Logger.d(LOG_TAG, "filterExercisesByTrainingDate. maxDate=${AppUtils.formatDateTimeWithWeekDay(maxDate)}")
        val listFiltered = list.filter {
            val trainingDate = Calendar.getInstance()
            trainingDate.timeInMillis = it.training!!.date
            trainingDate.before(maxDate)
        } as MutableList
        Logger.d(LOG_TAG, "filterExercisesByTrainingDate. list size=${list.size} filtered to size=${listFiltered.size}")
        listFiltered.print("filterExercisesByTrainingDate")
        return listFiltered
    }

    private fun getPreviousExerciseData(exerciseId: Long?, training: Training?): ExerciseInTraining? {
        if (exerciseId == null || training == null) return null
        else {
            val maxDate = Calendar.getInstance()
            maxDate.timeInMillis = training.date
            maxDate.add(Calendar.HOUR_OF_DAY, -2)
            Logger.d(LOG_TAG, "getPreviousExerciseData. maxDate=${AppUtils.formatDateTimeWithWeekDay(maxDate)}")
            exercisesInTrainings.print("getPreviousExerciseData")
            for (next in exercisesInTrainings) {
                val trainingDate = Calendar.getInstance()
                trainingDate.timeInMillis = next.training!!.date
                if (trainingDate.before(maxDate) && exerciseId == next.exerciseId) return next
            }
            return null
        }
    }
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "ExercisePresenter"
    }
}