package com.devtau.ironHeroes.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.MuscleGroup

class ExerciseRelation {

    @Embedded
    lateinit var exercise: Exercise

    @Relation(parentColumn = "muscleGroupId", entityColumn = "muscleGroupId")
    lateinit var muscleGroup: MuscleGroup

    fun convert(): Exercise {
        exercise.muscleGroup = muscleGroup
        return exercise
    }
}