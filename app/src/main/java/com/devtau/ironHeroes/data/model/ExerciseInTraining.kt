package com.devtau.ironHeroes.data.model

import android.text.TextUtils
import androidx.room.Entity
import androidx.room.Index
import com.devtau.ironHeroes.util.Constants.INTEGER_NOT_PARSED

@Entity(
    tableName = "ExercisesInTraining",
    indices = [Index("exerciseId")],
    ignoredColumns = ["training", "exercise"]
)
class ExerciseInTraining(
    id: Long?,
    var trainingId: Long?,
    var training: Training?,
    var exerciseId: Long?,
    var exercise: Exercise?,
    var weight: Int,
    var count: Int,
    var comment: String? = null
): DataObject(id) {

    constructor(id: Long?, trainingId: Long?, exerciseId: Long?, weight: Int, count: Int, comment: String? = null):
            this(id, trainingId, null, exerciseId, null, weight, count, comment)

    override fun toString(): String = "training=($training), exercise=($exercise), weight=$weight, count=$count, comment=$comment"

    fun someFieldsChanged(exerciseId: Long?, weight: Int?, count: Int?, comment: String?) =
        exerciseId != this.exerciseId || weight != this.weight || count != this.count || !TextUtils.equals(comment, this.comment)

    fun calculateWork(): Int = weight * count


    companion object {
        const val DEFAULT_COUNT = 80.toString()

        fun allObligatoryPartsPresent(trainingId: Long?, exerciseId: Long?, weight: Int?, count: Int?) =
            trainingId != null && exerciseId != null && weight != null && count != null && count != INTEGER_NOT_PARSED

        fun getMock(): List<ExerciseInTraining> = listOf(
            //08.10
            ExerciseInTraining(null, Training.getMock()[0].id, 41, 0, 60),
            ExerciseInTraining(null, Training.getMock()[0].id, 44, 30, 48),
            ExerciseInTraining(null, Training.getMock()[0].id, 2, 50, 30),
            ExerciseInTraining(null, Training.getMock()[0].id, 29, 6, 60),
            ExerciseInTraining(null, Training.getMock()[0].id, 32, 0, 60),

            //09.10
            ExerciseInTraining(null, Training.getMock()[1].id, 2, 55, 45),
            ExerciseInTraining(null, Training.getMock()[1].id, 30, 6, 45),
            ExerciseInTraining(null, Training.getMock()[1].id, 10, 10, 45),
            ExerciseInTraining(null, Training.getMock()[1].id, 29, 6, 45),
            ExerciseInTraining(null, Training.getMock()[1].id, 39, 0, 100),
            ExerciseInTraining(null, Training.getMock()[1].id, 40, 3, 45),

            //11.10
            ExerciseInTraining(null, Training.getMock()[2].id, 15, 7, 80),
            ExerciseInTraining(null, Training.getMock()[2].id, 18, 10, 80),
            ExerciseInTraining(null, Training.getMock()[2].id, 20, 4, 80),
            ExerciseInTraining(null, Training.getMock()[2].id, 26, 4, 80),
            ExerciseInTraining(null, Training.getMock()[2].id, 43, 7, 80),

            //14.10
            ExerciseInTraining(null, Training.getMock()[3].id, 46, 24, 60),
            ExerciseInTraining(null, Training.getMock()[3].id, 44, 32, 60),
            ExerciseInTraining(null, Training.getMock()[3].id, 37, 5, 60),
            ExerciseInTraining(null, Training.getMock()[3].id, 38, 0, 80),
            ExerciseInTraining(null, Training.getMock()[3].id, 32, 0, 60),

            //15.10
            ExerciseInTraining(null, Training.getMock()[4].id, 29, 7, 80),
            ExerciseInTraining(null, Training.getMock()[4].id, 36, 27, 80),
            ExerciseInTraining(null, Training.getMock()[4].id, 21, 10, 80),
            ExerciseInTraining(null, Training.getMock()[4].id, 31, 12, 80),
            ExerciseInTraining(null, Training.getMock()[4].id, 36, 7, 80),
            ExerciseInTraining(null, Training.getMock()[4].id, 22, 2, 80),
            ExerciseInTraining(null, Training.getMock()[4].id, 30, 6, 60),
            ExerciseInTraining(null, Training.getMock()[4].id, 47, 4, 60),

            //17.10
            ExerciseInTraining(null, Training.getMock()[5].id, 8, 7, 48),
            ExerciseInTraining(null, Training.getMock()[5].id, 9, 10, 80),
            ExerciseInTraining(null, Training.getMock()[5].id, 15, 7, 80),
            ExerciseInTraining(null, Training.getMock()[5].id, 31, 12, 80),
            ExerciseInTraining(null, Training.getMock()[5].id, 26, 2, 80),
            ExerciseInTraining(null, Training.getMock()[5].id, 28, 5, 80),

            //21.10
            ExerciseInTraining(null, Training.getMock()[6].id, 40, 4, 80),
            ExerciseInTraining(null, Training.getMock()[6].id, 37, 6, 80),
            ExerciseInTraining(null, Training.getMock()[6].id, 38, 0, 80),
            ExerciseInTraining(null, Training.getMock()[6].id, 39, 4, 100),
            ExerciseInTraining(null, Training.getMock()[6].id, 41, 0, 80),
            ExerciseInTraining(null, Training.getMock()[6].id, 32, 0, 100),

            //22.10
            ExerciseInTraining(null, Training.getMock()[7].id, 29, 7, 80),
            ExerciseInTraining(null, Training.getMock()[7].id, 19, 40, 80),
            ExerciseInTraining(null, Training.getMock()[7].id, 35, 30, 80),
            ExerciseInTraining(null, Training.getMock()[7].id, 30, 6, 80),
            ExerciseInTraining(null, Training.getMock()[7].id, 27, 2, 80),
            ExerciseInTraining(null, Training.getMock()[7].id, 20, 4, 80),

            //25.10
            ExerciseInTraining(null, Training.getMock()[8].id, 13, 0, 40),
            ExerciseInTraining(null, Training.getMock()[8].id, 1, 40, 60),
            ExerciseInTraining(null, Training.getMock()[8].id, 8, 6, 60),
            ExerciseInTraining(null, Training.getMock()[8].id, 17, 22, 48),
            ExerciseInTraining(null, Training.getMock()[8].id, 24, 10, 8),

            //28.10
            ExerciseInTraining(null, Training.getMock()[9].id, 37, 6, 80),
            ExerciseInTraining(null, Training.getMock()[9].id, 38, 0, 80),
            ExerciseInTraining(null, Training.getMock()[9].id, 39, 4, 100),
            ExerciseInTraining(null, Training.getMock()[9].id, 40, 3, 80),
            ExerciseInTraining(null, Training.getMock()[9].id, 41, 0, 80),
            ExerciseInTraining(null, Training.getMock()[9].id, 32, 0, 80),

            //29.10
            ExerciseInTraining(null, Training.getMock()[10].id, 29, 7, 80, "5-6-7-8 разными хватами"),
            ExerciseInTraining(null, Training.getMock()[10].id, 20, 3, 80),
            ExerciseInTraining(null, Training.getMock()[10].id, 30, 7, 80, "5-6-7-8"),
            ExerciseInTraining(null, Training.getMock()[10].id, 22, 4, 80, "3-4 разными хватами"),
            ExerciseInTraining(null, Training.getMock()[10].id, 31, 12, 80),
            ExerciseInTraining(null, Training.getMock()[10].id, 35, 40, 80),

            //1.11
            ExerciseInTraining(null, Training.getMock()[11].id, 2, 60, 32),
            ExerciseInTraining(null, Training.getMock()[11].id, 42, 28, 81),
            ExerciseInTraining(null, Training.getMock()[11].id, 8, 5, 36),
            ExerciseInTraining(null, Training.getMock()[11].id, 16, 20, 52),
            ExerciseInTraining(null, Training.getMock()[11].id, 23, 10, 30),

            //4.11
            ExerciseInTraining(null, Training.getMock()[12].id, 41, 0, 80),
            ExerciseInTraining(null, Training.getMock()[12].id, 45, 10, 48),
            ExerciseInTraining(null, Training.getMock()[12].id, 37, 8, 80),
            ExerciseInTraining(null, Training.getMock()[12].id, 49, 7, 80),
            ExerciseInTraining(null, Training.getMock()[12].id, 32, 0, 80),

            //5.11
            ExerciseInTraining(null, Training.getMock()[13].id, 29, 7, 60),
            ExerciseInTraining(null, Training.getMock()[13].id, 50, 75, 60, "2 резинки"),
            ExerciseInTraining(null, Training.getMock()[13].id, 31, 12, 60),
            ExerciseInTraining(null, Training.getMock()[13].id, 51, 20, 40),
            ExerciseInTraining(null, Training.getMock()[13].id, 20, 4, 60),

            //8.11
            ExerciseInTraining(null, Training.getMock()[14].id, 7, 20, 37, "17-20-22 кг * 15-12-10"),
            ExerciseInTraining(null, Training.getMock()[14].id, 16, 12, 37, "15-12-10"),
            ExerciseInTraining(null, Training.getMock()[14].id, 9, 12, 37, "10-12-15 кг * 15-12-10"),
            ExerciseInTraining(null, Training.getMock()[14].id, 26, 4, 37, "2-4-7 кг * 15-12-10"),
            ExerciseInTraining(null, Training.getMock()[14].id, 2, 40, 34, "30-40-50 кг * 15-12-7"),
            ExerciseInTraining(null, Training.getMock()[14].id, 48, 20, 34, "10-12-12")
        )
    }
}