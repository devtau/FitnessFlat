package com.devtau.ironHeroes.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MuscleGroups")
class MuscleGroup(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "muscleGroupId")
    var id: Long?,
    var name: String
) {

    override fun toString(): String = name

    companion object {
        fun getMock() = listOf(
            MuscleGroup(1, "Грудные"),
            MuscleGroup(2, "Бицепс"),
            MuscleGroup(3, "Трицепс"),
            MuscleGroup(4, "Дельта"),
            MuscleGroup(5, "Спина"),
            MuscleGroup(6, "Пресс"),
            MuscleGroup(7, "Трапеции (шраги)"),
            MuscleGroup(8, "Ноги")
        )
    }
}