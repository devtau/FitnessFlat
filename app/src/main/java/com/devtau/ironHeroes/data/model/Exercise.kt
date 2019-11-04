package com.devtau.ironHeroes.data.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "Exercises",
    indices = [Index("muscleGroupId")],
    ignoredColumns = ["muscleGroup"]
)
class Exercise(
    id: Long?,
    var name: String,
    var muscleGroupId: Long,
    var muscleGroup: MuscleGroup?
): DataObject(id) {

    constructor(id: Long?, name: String, muscleGroupId: Long): this(id, name, muscleGroupId, null)

    companion object {
        fun getMock() = listOf(
            //Грудные
            Exercise(1, "Жим штанги (верх)", 1),
            Exercise(2, "Жим штанги (центр)", 1),
            Exercise(3, "Жим штанги (низ)", 1),
            Exercise(4, "Жим на тренажере (верх)", 1),
            Exercise(5, "Жим на тренажере (центр)", 1),
            Exercise(6, "Жим на тренажере (низ)", 1),
            Exercise(7, "Жим гантелей лежа", 1),
            Exercise(8, "Жим орла", 1),
            Exercise(9, "Обнимашки с гантелями (верх)", 1),
            Exercise(10, "Обнимашки с гантелями (центр)", 1),
            Exercise(11, "Обнимашки с гантелями (низ)", 1),
            Exercise(12, "Отжимания", 1),
            Exercise(13, "Отжимания на гантелях", 1),
            Exercise(14, "Отжимания в TRX", 1),

            //Бицепс
            Exercise(15, "Жим гантелей сидя", 2),
            Exercise(16, "Жим гантелей стоя", 2),
            Exercise(17, "Жим штанги стоя", 2),
            Exercise(18, "Молотковые сгибания", 2),

            //Трицепс
            Exercise(19, "Жим узким хватом", 3),
            Exercise(20, "Тяга верхнего блока", 3),
            Exercise(21, "Французский со свободным весом", 3),
            Exercise(22, "Французский в тренажере", 3),

            //Дельта
            Exercise(23, "Кубинский жим", 4),
            Exercise(24, "Махи трио", 4),
            Exercise(25, "Махи на передний пучок", 4),
            Exercise(26, "Махи на центральный пучок", 4),
            Exercise(27, "Махи на задний пучок", 4),
            Exercise(28, "Жим диска перед собой", 4),

            //Спина
            Exercise(29, "Тяга верхнего блока", 5),
            Exercise(30, "Тяга нижнего блока", 5),
            Exercise(31, "Тяга гантели к поясу", 5),

            //Пресс
            Exercise(32, "Пресс в TRX горизонтально", 6),
            Exercise(33, "Книжка", 6),
            Exercise(34, "Книжка в TRX", 6),

            //Трапеции (шраги)
            Exercise(35, "Тяга штанги (к подбородку)", 7),
            Exercise(36, "Тяга гантелей", 7),

            //Ноги
            Exercise(37, "Разгибания на квадрицепс", 8),
            Exercise(38, "Наклоны в TRX", 8),
            Exercise(39, "Выпады", 8),
            Exercise(40, "Сгибания", 8),
            Exercise(41, "Присед в TRX", 8)
        )
    }
}