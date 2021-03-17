package com.devtau.ironHeroes.data.source.local.exerciseInTraining

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.source.local.exercise.ExerciseRelation
import timber.log.Timber

class ExerciseInTrainingRelation {

    @Embedded
    lateinit var exerciseInTraining: ExerciseInTraining

    @Relation(parentColumn = "trainingId", entityColumn = "trainingId")
    var training: Training? = null

    @Relation(parentColumn = "exerciseId", entityColumn = "exerciseId", entity = Exercise::class)
    var exercise: ExerciseRelation? = null

    fun convert(): ExerciseInTraining {
        val msg = when {
            training == null -> "training with id=${exerciseInTraining.trainingId} not found in db"
            exercise == null -> "exercise with id=${exerciseInTraining.exercise} not found in db"
            else -> null
        }

        if (msg != null) {
            Timber.e(msg)
            return exerciseInTraining
        }
        exerciseInTraining.training = training
        exerciseInTraining.exercise = exercise!!.convert()
        return exerciseInTraining
    }
}