package com.devtau.ironHeroes.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.model.Training

class ExerciseInTrainingRelation {

    @Embedded
    lateinit var exerciseInTraining: ExerciseInTraining

    @Relation(parentColumn = "trainingId", entityColumn = "trainingId")
    lateinit var training: Training

    @Relation(parentColumn = "exerciseId", entityColumn = "exerciseId", entity = Exercise::class)
    lateinit var exercise: ExerciseRelation

    fun convert(): ExerciseInTraining {
        exerciseInTraining.training = training
        exerciseInTraining.exercise = exercise.convert()
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