package com.devtau.ironHeroes.data.source.local.exercise

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.util.Logger

class ExerciseRelation {

    @Embedded
    lateinit var exercise: Exercise

    @Relation(parentColumn = "muscleGroupId", entityColumn = "muscleGroupId")
    var muscleGroup: MuscleGroup? = null

    fun convert(): Exercise {
        val msg = if (muscleGroup == null) "muscleGroup with id=${exercise.muscleGroupId} not found in db"
        else null

        if (msg != null) {
            Logger.e(LOG_TAG, msg)
            return exercise
        }
        exercise.muscleGroup = muscleGroup
        return exercise
    }


    companion object {
        private const val LOG_TAG = "ExerciseRelation"
    }
}