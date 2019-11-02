package com.devtau.ironHeroes.ui.dialogs.exerciseDialog

import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.rest.NetworkLayer
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.Constants.EMPTY_OBJECT_ID
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
    private val exerciseId: Long?
): DBSubscriber(), ExercisePresenter {

    private var exercises: List<Exercise>? = null
    private var exerciseInTraining: ExerciseInTraining? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        dataLayer.getExercisesAndClose(Consumer { exercisesFromDB ->
            exercises = exercisesFromDB
            if (exerciseId != null) {
                dataLayer.getExerciseInTrainingAndClose(exerciseId, Consumer { exerciseInTrainingFromDB ->
                    exerciseInTraining = exerciseInTrainingFromDB
                    view.showExerciseDetails(exerciseInTraining, exercises)
                })
            } else {
                view.showExerciseDetails(null, exercises)
            }
        })
    }

    override fun updateExercise(exerciseId: Long?, weight: String?, count: String?) {
        val weightInt = try {
            weight?.toInt() ?: INTEGER_NOT_PARSED
        } catch (e: NumberFormatException) {
            Logger.e(LOG_TAG, "bad data in weight field")
            INTEGER_NOT_PARSED
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
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "ExercisePresenter"
    }
}