package com.devtau.ironHeroes.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.MuscleGroup

class ExerciseRelation {

    @Embedded
    lateinit var exercise: Exercise

    @Relation(parentColumn = "muscleGroupId", entityColumn = "id")
    lateinit var muscleGroup: MuscleGroup

    fun convert(): Exercise {
        exercise.muscleGroup = muscleGroup
        return exercise
    }


    companion object {
        fun convertList(list: List<ExerciseRelation>): List<Exercise> {
            val exercises = ArrayList<Exercise>()
            for (next in list) exercises.add(next.convert())
            return exercises
        }
    }
}