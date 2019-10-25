package com.devtau.ironHeroes.data.model

import androidx.room.Entity

@Entity(tableName = "Exercises")
class Exercise(
    id: Long?,
    var name: String,
    var muscleGroupId: Long
): DataObject(id) {


    companion object {
        fun getMock() = listOf(
            Exercise(1, "Жим штанги (верх)", 1),
            Exercise(2, "Жим штанги (центр)", 1),
            Exercise(3, "Жим штанги (низ)", 1),
            Exercise(4, "Жим гантелей сидя", 2),
            Exercise(5, "Жим штанги стоя", 2),
            Exercise(6, "Молотковые сгибания", 2),
            Exercise(7, "Жим узким хватом", 3),
            Exercise(8, "Жим груза из-за спины", 3),
            Exercise(9, "Тяга верхнего блока", 3))
    }
}