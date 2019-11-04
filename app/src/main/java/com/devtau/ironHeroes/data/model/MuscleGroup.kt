package com.devtau.ironHeroes.data.model

import androidx.room.Entity

@Entity(tableName = "MuscleGroups")
class MuscleGroup(
    id: Long?,
    var name: String
): DataObject(id) {


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