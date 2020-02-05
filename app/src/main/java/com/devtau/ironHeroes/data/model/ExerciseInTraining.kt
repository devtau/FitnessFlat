package com.devtau.ironHeroes.data.model

import android.content.Context
import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.util.Constants.INTEGER_NOT_PARSED
import java.util.*
import kotlin.collections.ArrayList

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
    var position: Int = MAX_POSITION,
    var comment: String? = null
) {

    constructor(id: Long?, trainingId: Long?, exerciseId: Long?, weight: Int, repeats: Int, count: Int,
                position: Int = MAX_POSITION, comment: String? = null):
            this(id, trainingId, null, exerciseId, null, weight, repeats, count, position, comment)

    override fun toString(): String = "training=($training), exercise=($exercise), " +
            "weight=$weight, $repeats*$count, comment=$comment"

    fun someFieldsChanged(exerciseId: Long?, weight: Int?, repeats: Int?, count: Int?, comment: String?) =
        exerciseId != this.exerciseId || weight != this.weight || repeats != this.repeats || count != this.count
                || !TextUtils.equals(comment, this.comment)

    fun calculateWork(): Int = if (weight == 0) repeats * count else weight * repeats * count

    override fun equals(other: Any?): Boolean = when {
        other !is ExerciseInTraining -> false
        other.id != this.id -> false
        other.exerciseId != this.exerciseId -> false
        other.weight != this.weight -> false
        other.repeats != this.repeats -> false
        other.count != this.count -> false
        else -> true
    }

    override fun hashCode(): Int = Objects.hash(id, exerciseId, weight, repeats, count)


    companion object {
        const val DEFAULT_REPEATS = 3.toString()
        const val DEFAULT_COUNT = 15.toString()
        const val MAX_POSITION = 100

        fun allObligatoryPartsPresent(trainingId: Long?, exerciseId: Long?, repeats: Int, count: Int) =
            trainingId != null && exerciseId != null && repeats != INTEGER_NOT_PARSED && count != INTEGER_NOT_PARSED

        fun getMock(c: Context, localeIsRu: Boolean): List<ExerciseInTraining> {
            val list = ArrayList<ExerciseInTraining>()
            val firstMockExerciseId = if (localeIsRu) 12L else 52L
            val secondMockExerciseId = if (localeIsRu) 2L else 29L
            val thirdMockExerciseId = if (localeIsRu) 10L else 30L
            val fourthMockExerciseId = if (localeIsRu) 16L else 19L
            val fifthMockExerciseId = if (localeIsRu) 48L else 20L

            //27.01
            var trainingId = Training.getMock(c)[0].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, firstMockExerciseId, 90, 3, 20, 0),
                ExerciseInTraining(null, trainingId, secondMockExerciseId, 80, 4, 12, 1),
                ExerciseInTraining(null, trainingId, thirdMockExerciseId, 40, 4, 12, 2),
                ExerciseInTraining(null, trainingId, fourthMockExerciseId, 50, 3, 8, 3),
                ExerciseInTraining(null, trainingId, fifthMockExerciseId, 40, 3, 8, 4)))

            //29.01
            trainingId = Training.getMock(c)[1].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, firstMockExerciseId, 90, 3, 20, 0),
                ExerciseInTraining(null, trainingId, secondMockExerciseId, 86, 4, 12, 1),
                ExerciseInTraining(null, trainingId, thirdMockExerciseId, 44, 4, 12, 2),
                ExerciseInTraining(null, trainingId, fourthMockExerciseId, 56, 3, 8, 3),
                ExerciseInTraining(null, trainingId, fifthMockExerciseId, 44, 3, 8, 4)))

            //31.01
            trainingId = Training.getMock(c)[2].id
            list.addAll(listOf(
                ExerciseInTraining(null, trainingId, firstMockExerciseId, 90, 3, 20, 0),
                ExerciseInTraining(null, trainingId, secondMockExerciseId, 90, 4, 12, 1),
                ExerciseInTraining(null, trainingId, thirdMockExerciseId, 48, 4, 12, 2),
                ExerciseInTraining(null, trainingId, fourthMockExerciseId, 60, 3, 8, 3),
                ExerciseInTraining(null, trainingId, fifthMockExerciseId, 48, 3, 8, 4)))

            return list
        }
    }
}