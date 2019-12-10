package com.devtau.ironHeroes.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.R

@Entity(
    tableName = "Exercises",
    indices = [Index("muscleGroupId")],
    ignoredColumns = ["muscleGroup"]
)
class Exercise(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "exerciseId")
    var id: Long?,
    var name: String,
    var muscleGroupId: Long,
    var muscleGroup: MuscleGroup?
) {

    constructor(id: Long?, name: String, muscleGroupId: Long): this(id, name, muscleGroupId, null)

    override fun toString(): String = "$name, muscleGroup=$muscleGroup"

    companion object {
        fun getMock(c: Context) = listOf(
            //Грудные
            Exercise(1, c.getString(R.string.barbell_bench_press_top), 1),
            Exercise(2, c.getString(R.string.barbell_bench_press_mid), 1),
            Exercise(3, c.getString(R.string.barbell_bench_press_low), 1),
            Exercise(4, c.getString(R.string.machine_bench_press_top), 1),
            Exercise(5, c.getString(R.string.machine_bench_press_mid), 1),
            Exercise(6, c.getString(R.string.machine_bench_press_low), 1),
            Exercise(7, c.getString(R.string.dumbbells_bench_press_top), 1),
            Exercise(42, c.getString(R.string.dumbbells_bench_press_mid), 1),
            Exercise(43, c.getString(R.string.dumbbells_bench_press_low), 1),
            Exercise(8, c.getString(R.string.eagle_bench_press), 1),
            Exercise(9, c.getString(R.string.dumbbells_fly_top), 1),
            Exercise(10, c.getString(R.string.dumbbells_fly_mid), 1),
            Exercise(11, c.getString(R.string.dumbbells_fly_low), 1),
            Exercise(12, c.getString(R.string.push_ups), 1),
            Exercise(13, c.getString(R.string.dumbbells_push_ups), 1),
            Exercise(14, c.getString(R.string.trx_push_ups), 1),

            //Бицепс
            Exercise(15, c.getString(R.string.sitting_dumbbell_curl), 2),
            Exercise(55, c.getString(R.string.sitting_dumbbell_curl_scott), 2),
            Exercise(16, c.getString(R.string.standing_dumbbell_curl), 2),
            Exercise(17, c.getString(R.string.standing_barbell_curl), 2),
            Exercise(48, c.getString(R.string.reverse_barbell_curl), 2),
            Exercise(18, c.getString(R.string.hammer_curls), 2),

            //Дельта
            Exercise(23, c.getString(R.string.dumbbell_cuban_press), 4),
            Exercise(24, c.getString(R.string.trio_swings), 4),
            Exercise(25, c.getString(R.string.front_muscle_bundle_swings), 4),
            Exercise(26, c.getString(R.string.mid_muscle_bundle_swings), 4),
            Exercise(27, c.getString(R.string.rear_muscle_bundle_swings), 4),
            Exercise(28, c.getString(R.string.front_plate_raise), 4),
            Exercise(58, c.getString(R.string.dumbbell_press_above_head), 4),

            //Трапеции (шраги)
            Exercise(35, c.getString(R.string.upright_barbell_rows), 7),
            Exercise(36, c.getString(R.string.dumbbells_shrug), 7),

            //Спина
            Exercise(29, c.getString(R.string.upper_block_pull), 5),
            Exercise(30, c.getString(R.string.lower_block_pull), 5),
            Exercise(31, c.getString(R.string.belt_dumbbell_pull), 5),
            Exercise(54, c.getString(R.string.belt_dumbbells_pull_on_bench), 5),
            Exercise(47, c.getString(R.string.pullover), 5),
            Exercise(50, c.getString(R.string.pull_ups), 5),
            Exercise(52, c.getString(R.string.hyperextension), 5),

            //Трицепс
            Exercise(19, c.getString(R.string.close_grip_bench_press), 3),
            Exercise(20, c.getString(R.string.cable_push_down), 3),
            Exercise(21, c.getString(R.string.french_press), 3),
            Exercise(22, c.getString(R.string.french_press_in_machine), 3),
            Exercise(51, c.getString(R.string.triangle_press), 3),

            //Ноги
            Exercise(37, c.getString(R.string.leg_extension), 8),
            Exercise(38, c.getString(R.string.bend_over_in_trx),8),
            Exercise(39, c.getString(R.string.lunges), 8),
            Exercise(40, c.getString(R.string.biceps_flexion_while_standing), 8),
            Exercise(57, c.getString(R.string.biceps_flexion_while_lying), 8),
            Exercise(41, c.getString(R.string.squats_in_TRX), 8),
            Exercise(44, c.getString(R.string.romanian_deadlift), 8),
            Exercise(45, c.getString(R.string.barbell_squat), 8),
            Exercise(46, c.getString(R.string.lunges_standing), 8),
            Exercise(53, c.getString(R.string.bridge), 8),
            Exercise(56, c.getString(R.string.deadlift), 8),

            //Пресс
            Exercise(32, c.getString(R.string.press_in_trx_horizontally), 6),
            Exercise(33, c.getString(R.string.book_press), 6),
            Exercise(34, c.getString(R.string.book_in_trx), 6),
            Exercise(49, c.getString(R.string.russian_twisting), 6)
        )
    }
}