package com.devtau.ironHeroes.data.model

import androidx.room.Entity
import androidx.room.Index
import com.devtau.ironHeroes.util.Constants.INTEGER_NOT_PARSED

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

    override fun toString(): String = "trainingId=$trainingId, exerciseId=$exerciseId, " +
            "exercise=$exercise, weight=$weight, count=$count"

    fun someFieldsChanged(exerciseId: Long?, weight: Int?, count: Int?) =
        exerciseId != this.exerciseId || weight != this.weight || count != this.count

    companion object {
        const val DEFAULT_COUNT = 60.toString()

        fun allObligatoryPartsPresent(trainingId: Long?, exerciseId: Long?, weight: Int?, count: Int?) =
            trainingId != null && exerciseId != null && weight != null
                    && count != null && count != INTEGER_NOT_PARSED

        fun getMock(): List<ExerciseInTraining> = listOf(
            ExerciseInTraining(1, Training.getMock()[0].id, Exercise.getMock()[0].id, Exercise.getMock()[0], 70, 45),
            ExerciseInTraining(2, Training.getMock()[0].id, Exercise.getMock()[1].id, Exercise.getMock()[1], 80, 60),
            ExerciseInTraining(3, Training.getMock()[0].id, Exercise.getMock()[2].id, Exercise.getMock()[2], 65, 45),
            ExerciseInTraining(4, Training.getMock()[0].id, Exercise.getMock()[3].id, Exercise.getMock()[3], 15, 45),
            ExerciseInTraining(5, Training.getMock()[0].id, Exercise.getMock()[4].id, Exercise.getMock()[4], 30, 45)
        )
    }
}