package com.devtau.ironHeroes.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining

class ExerciseInTrainingRelation {

    @Embedded
    lateinit var exerciseInTraining: ExerciseInTraining

    @Relation(parentColumn = "exerciseId", entityColumn = "id")
    lateinit var exercise: Exercise

    fun convert(): ExerciseInTraining {
        exerciseInTraining.exercise = exercise
        return exerciseInTraining
    }


    companion object {
        fun convertList(list: List<ExerciseInTrainingRelation>): List<ExerciseInTraining> {
            val exercises = ArrayList<ExerciseInTraining>()
            for (next in list) exercises.add(next.convert())
            return exercises
        }
    }
}