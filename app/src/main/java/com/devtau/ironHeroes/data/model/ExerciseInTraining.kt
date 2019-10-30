package com.devtau.ironHeroes.data.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "ExercisesInTraining",
    indices = [Index("exerciseId")],
    ignoredColumns = ["exercise"]
)
class ExerciseInTraining(
    id: Long?,
    var trainingId: Long?,
    var exerciseId: Long?,
    var exercise: Exercise?,
    var weight: Int,
    var count: Int
): DataObject(id) {

    constructor(id: Long?, trainingId: Long, exerciseId: Long, weight: Int, count: Int):
            this(id, trainingId, exerciseId, null, weight, count)

    companion object {
        fun getMock() = listOf(
            ExerciseInTraining(1, Training.getMock()[0].id, Exercise.getMock()[0].id, Exercise.getMock()[0], 80, 45),
            ExerciseInTraining(2, Training.getMock()[0].id, Exercise.getMock()[1].id, Exercise.getMock()[1], 80, 45),
            ExerciseInTraining(3, Training.getMock()[0].id, Exercise.getMock()[2].id, Exercise.getMock()[2], 80, 45),
            ExerciseInTraining(4, Training.getMock()[0].id, Exercise.getMock()[3].id, Exercise.getMock()[3], 80, 45),
            ExerciseInTraining(5, Training.getMock()[0].id, Exercise.getMock()[4].id, Exercise.getMock()[4], 80, 45))
    }
}