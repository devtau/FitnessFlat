package com.devtau.ironHeroes.data.model

import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.util.Constants.INTEGER_NOT_PARSED

@Entity(
    tableName = "ExercisesInTraining",
    indices = [Index("trainingId", "exerciseId")],
    ignoredColumns = ["training", "exercise"]
)
class ExerciseInTraining(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "exerciseInTrainingId")
    var id: Long?,
    var trainingId: Long?,
    var training: Training?,
    var exerciseId: Long?,
    var exercise: Exercise?,
    var weight: Int,
    var repeats: Int,
    var count: Int,
    var comment: String? = null
) {

    constructor(id: Long?, trainingId: Long?, exerciseId: Long?, weight: Int, repeats: Int, count: Int, comment: String? = null):
            this(id, trainingId, null, exerciseId, null, weight, repeats, count, comment)

    override fun toString(): String = "training=($training), exercise=($exercise), " +
            "weight=$weight, $repeats*$count, comment=$comment"

    fun someFieldsChanged(exerciseId: Long?, weight: Int?, repeats: Int?, count: Int?, comment: String?) =
        exerciseId != this.exerciseId || weight != this.weight || repeats != this.repeats || count != this.count
                || !TextUtils.equals(comment, this.comment)

    fun calculateWork(): Int = if (weight == 0) repeats * count else weight * repeats * count


    companion object {
        const val DEFAULT_REPEATS = 15.toString()
        const val DEFAULT_COUNT = 15.toString()

        fun allObligatoryPartsPresent(trainingId: Long?, exerciseId: Long?, weight: Int?, count: Int?, repeats: Int?) =
            trainingId != null && exerciseId != null && weight != null
                    && repeats != null && repeats != INTEGER_NOT_PARSED
                    && count != null && count != INTEGER_NOT_PARSED

        fun getMock(): List<ExerciseInTraining> {
            var trainingId = Training.getMock()[0].id
            val list = ArrayList<ExerciseInTraining>()
            //08.10
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 41, 0, 3, 20),
                ExerciseInTraining(null, trainingId, 44, 30, 4, 12),
                ExerciseInTraining(null, trainingId, 2, 50, 3, 10),
                ExerciseInTraining(null, trainingId, 29, 6, 3, 20),
                ExerciseInTraining(null, trainingId, 32, 0, 3, 20)))

            //09.10
            trainingId = Training.getMock()[1].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 2, 55, 3, 15),
                ExerciseInTraining(null, trainingId, 30, 6, 3, 15),
                ExerciseInTraining(null, trainingId, 10, 10, 3, 15),
                ExerciseInTraining(null, trainingId, 29, 6, 3, 15),
                ExerciseInTraining(null, trainingId, 39, 0, 3, 40),
                ExerciseInTraining(null, trainingId, 40, 3, 3, 15)))

            //11.10
            trainingId = Training.getMock()[2].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 15, 7, 4, 20),
                ExerciseInTraining(null, trainingId, 18, 10, 4, 20),
                ExerciseInTraining(null, trainingId, 20, 4, 4, 20),
                ExerciseInTraining(null, trainingId, 26, 4, 4, 20),
                ExerciseInTraining(null, trainingId, 43, 7, 4, 20)))

            //--------------------------------------------------------------------------------------
            //14.10
            trainingId = Training.getMock()[3].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 46, 24, 4, 15),
                ExerciseInTraining(null, trainingId, 44, 32, 4, 15),
                ExerciseInTraining(null, trainingId, 37, 5, 4, 15),
                ExerciseInTraining(null, trainingId, 38, 14, 4, 20),
                ExerciseInTraining(null, trainingId, 32, 0, 4, 15)))

            //15.10
            trainingId = Training.getMock()[4].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 29, 7, 4, 15),
                ExerciseInTraining(null, trainingId, 36, 27, 4, 15),
                ExerciseInTraining(null, trainingId, 21, 10, 4, 15),
                ExerciseInTraining(null, trainingId, 31, 12, 4, 15),
                ExerciseInTraining(null, trainingId, 36, 7, 4, 15),
                ExerciseInTraining(null, trainingId, 22, 2, 4, 15),
                ExerciseInTraining(null, trainingId, 30, 6, 3, 15),
                ExerciseInTraining(null, trainingId, 47, 4, 3, 15)))

            //17.10
            trainingId = Training.getMock()[5].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 8, 7, 4, 12),
                ExerciseInTraining(null, trainingId, 9, 10, 3, 15),
                ExerciseInTraining(null, trainingId, 15, 7, 4, 18),
                ExerciseInTraining(null, trainingId, 31, 12, 4, 18),
                ExerciseInTraining(null, trainingId, 26, 2, 4, 18),
                ExerciseInTraining(null, trainingId, 28, 5, 4, 18)))

            //--------------------------------------------------------------------------------------
            //21.10
            trainingId = Training.getMock()[6].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 40, 4, 4, 20),
                ExerciseInTraining(null, trainingId, 37, 6, 4, 20),
                ExerciseInTraining(null, trainingId, 38, 0, 4, 20),
                ExerciseInTraining(null, trainingId, 39, 4, 3, 40),
                ExerciseInTraining(null, trainingId, 41, 0, 4, 20),
                ExerciseInTraining(null, trainingId, 32, 0, 5, 20)))

            //22.10
            trainingId = Training.getMock()[7].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 29, 7, 4, 15),
                ExerciseInTraining(null, trainingId, 19, 40, 4, 15),
                ExerciseInTraining(null, trainingId, 35, 30, 4, 15),
                ExerciseInTraining(null, trainingId, 30, 6, 4, 15),
                ExerciseInTraining(null, trainingId, 27, 2, 4, 15),
                ExerciseInTraining(null, trainingId, 20, 4, 4, 15)))

            //25.10
            trainingId = Training.getMock()[8].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 13, 0, 4, 10),
                ExerciseInTraining(null, trainingId, 1, 40, 4, 15),
                ExerciseInTraining(null, trainingId, 8, 6, 4, 15),
                ExerciseInTraining(null, trainingId, 17, 22, 4, 12),
                ExerciseInTraining(null, trainingId, 24, 10, 5, 8)))

            //--------------------------------------------------------------------------------------
            //28.10
            trainingId = Training.getMock()[9].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 37, 6, 4, 20),
                ExerciseInTraining(null, trainingId, 38, 0, 4, 20),
                ExerciseInTraining(null, trainingId, 39, 4, 3, 40),
                ExerciseInTraining(null, trainingId, 40, 3, 4, 20),
                ExerciseInTraining(null, trainingId, 41, 0, 4, 20),
                ExerciseInTraining(null, trainingId, 32, 0, 4, 20)))

            //29.10
            trainingId = Training.getMock()[10].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 29, 7, 4, 15, "5-6-7-8 разными хватами"),
                ExerciseInTraining(null, trainingId, 20, 3, 4, 15),
                ExerciseInTraining(null, trainingId, 30, 7, 4, 15, "5-6-7-8"),
                ExerciseInTraining(null, trainingId, 22, 4, 4, 15, "3-4 разными хватами"),
                ExerciseInTraining(null, trainingId, 31, 12, 4, 15),
                ExerciseInTraining(null, trainingId, 35, 40, 4, 15)))

            //1.11
            trainingId = Training.getMock()[11].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 2, 60, 4, 8),
                ExerciseInTraining(null, trainingId, 42, 28, 3, 27),
                ExerciseInTraining(null, trainingId, 8, 5, 3, 12),
                ExerciseInTraining(null, trainingId, 16, 20, 4, 13),
                ExerciseInTraining(null, trainingId, 23, 10, 3, 10)))

            //--------------------------------------------------------------------------------------
            //4.11
            trainingId = Training.getMock()[12].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 41, 0, 4, 20),
                ExerciseInTraining(null, trainingId, 45, 10, 4, 12),
                ExerciseInTraining(null, trainingId, 37, 8, 4, 20),
                ExerciseInTraining(null, trainingId, 49, 7, 4, 20),
                ExerciseInTraining(null, trainingId, 32, 0, 4, 20)))

            //5.11
            trainingId = Training.getMock()[13].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 29, 7, 4, 15),
                ExerciseInTraining(null, trainingId, 50, 75, 4, 12, "2 резинки"),
                ExerciseInTraining(null, trainingId, 31, 12, 4, 15),
                ExerciseInTraining(null, trainingId, 51, 20, 4, 10),
                ExerciseInTraining(null, trainingId, 20, 4, 4, 15)))

            //8.11
            trainingId = Training.getMock()[14].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 7, 20, 3, 15, "17-20-22 кг * 15-12-10"),
                ExerciseInTraining(null, trainingId, 16, 12, 3, 15, "15-12-10"),
                ExerciseInTraining(null, trainingId, 9, 12, 3, 15, "10-12-15 кг * 15-12-10"),
                ExerciseInTraining(null, trainingId, 26, 4, 3, 15, "2-4-7 кг * 15-12-10"),
                ExerciseInTraining(null, trainingId, 2, 40, 3, 15, "30-40-50 кг * 15-12-7"),
                ExerciseInTraining(null, trainingId, 48, 20, 3, 15, "10-12-12")))

            //--------------------------------------------------------------------------------------
            //11.11
            trainingId = Training.getMock()[15].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 52, 5, 3, 17),
                ExerciseInTraining(null, trainingId, 45, 40, 3, 15),
                ExerciseInTraining(null, trainingId, 46, 17, 4, 15),
                ExerciseInTraining(null, trainingId, 44, 40, 4, 15),
                ExerciseInTraining(null, trainingId, 53, 0, 3, 20)))

            //14.11
            trainingId = Training.getMock()[16].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 52, 5, 3, 17),
                ExerciseInTraining(null, trainingId, 50, 75, 3, 12, "2 резинки"),
                ExerciseInTraining(null, trainingId, 54, 15, 3, 12),
                ExerciseInTraining(null, trainingId, 19, 40, 2, 12, "+50*6 и 2 с помощью"),
                ExerciseInTraining(null, trainingId, 35, 20, 3, 12),
                ExerciseInTraining(null, trainingId, 22, 3, 3, 12),
                ExerciseInTraining(null, trainingId, 30, 4, 3, 12, "широким")))

            //15.11
            trainingId = Training.getMock()[17].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 1, 50, 3, 10, "третий 8"),
                ExerciseInTraining(null, trainingId, 15, 15, 3, 15),
                ExerciseInTraining(null, trainingId, 9, 12, 3, 15),
                ExerciseInTraining(null, trainingId, 55, 10, 3, 15),
                ExerciseInTraining(null, trainingId, 12, 4, 3, 11, "13-11-9")))

            //--------------------------------------------------------------------------------------
            //18.11
            trainingId = Training.getMock()[18].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 52, 5, 3, 17),
                ExerciseInTraining(null, trainingId, 46, 34, 4, 15),
                ExerciseInTraining(null, trainingId, 45, 50, 4, 10),
                ExerciseInTraining(null, trainingId, 56, 60, 4, 10),
                ExerciseInTraining(null, trainingId, 32, 20, 4, 20)))

            //21.11
            trainingId = Training.getMock()[19].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, 52, 5, 3, 17),
                ExerciseInTraining(null, trainingId, 50, 75, 3, 15, "2 резинки"),
                ExerciseInTraining(null, trainingId, 19, 45, 3, 12, "15-12-10"),
                ExerciseInTraining(null, trainingId, 30, 12, 3, 12),
                ExerciseInTraining(null, trainingId, 27, 4, 3, 12),
                ExerciseInTraining(null, trainingId, 54, 15, 3, 12)))

            //22.11
            trainingId = Training.getMock()[20].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, exerciseId = 52, weight = 10, repeats = 3, count = 17),
                ExerciseInTraining(null, trainingId, exerciseId = 2, weight = 60, repeats = 4, count = 8),
                ExerciseInTraining(null, trainingId, exerciseId = 7, weight = 14, repeats = 4, count = 10),
                ExerciseInTraining(null, trainingId, exerciseId = 8, weight = 6, repeats = 3, count = 15),
                ExerciseInTraining(null, trainingId, exerciseId = 18, weight = 14, repeats = 4, count = 12),
                ExerciseInTraining(null, trainingId, exerciseId = 17, weight = 30, repeats = 4, count = 10)))

            return list
        }
    }
}