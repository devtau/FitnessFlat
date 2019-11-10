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

    override fun toString(): String = "$name, muscleGroup=$muscleGroup"

    companion object {
        fun getMock() = listOf(
            //Грудные
            Exercise(1, "Жим штанги (верх)", 1),
            Exercise(2, "Жим штанги (центр)", 1),
            Exercise(3, "Жим штанги (низ)", 1),
            Exercise(4, "Жим на тренажере (верх)", 1),
            Exercise(5, "Жим на тренажере (центр)", 1),
            Exercise(6, "Жим на тренажере (низ)", 1),
            Exercise(7, "Жим гантелей лежа (верх)", 1),
            Exercise(42, "Жим гантелей лежа (центр)", 1),
            Exercise(43, "Жим гантелей лежа (низ)", 1),
            Exercise(8, "Жим орла", 1),
            Exercise(9, "Разводка гантелей (верх)", 1),
            Exercise(10, "Разводка гантелей (центр)", 1),
            Exercise(11, "Разводка гантелей (низ)", 1),
            Exercise(12, "Отжимания", 1),
            Exercise(13, "Отжимания на гантелях", 1),
            Exercise(14, "Отжимания в TRX", 1),

            //Бицепс
            Exercise(15, "Подъем гантелей сидя", 2),
            Exercise(16, "Подъем гантелей стоя", 2),
            Exercise(17, "Подъем штанги стоя", 2),
            Exercise(48, "Подъем штанги стоя обратным", 2),
            Exercise(18, "Молотковые сгибания", 2),

            //Дельта
            Exercise(23, "Кубинский жим", 4),
            Exercise(24, "Махи трио", 4),
            Exercise(25, "Махи на передний пучок", 4),
            Exercise(26, "Махи на центральный пучок", 4),
            Exercise(27, "Махи на задний пучок", 4),
            Exercise(28, "Подъем диска перед собой", 4),

            //Трапеции (шраги)
            Exercise(35, "Тяга штанги к подбородку", 7),
            Exercise(36, "Тяга гантелей", 7),


            //Спина
            Exercise(29, "Тяга верхнего блока", 5),
            Exercise(30, "Тяга нижнего блока", 5),
            Exercise(31, "Тяга гантели к поясу", 5),
            Exercise(47, "Пуловер", 5),
            Exercise(50, "Подтягивания", 5),

            //Трицепс
            Exercise(19, "Жим узким хватом", 3),
            Exercise(20, "Тяга верхнего блока", 3),
            Exercise(21, "Французский со свободным весом", 3),
            Exercise(22, "Французский в тренажере", 3),
            Exercise(51, "Треугольный жим", 3),

            //Ноги
            Exercise(37, "Разгибания на квадрицепс", 8),
            Exercise(38, "Наклоны в TRX", 8),
            Exercise(39, "Выпады", 8),
            Exercise(40, "Сгибания", 8),
            Exercise(41, "Присед в TRX", 8),
            Exercise(44, "Румынская тяга", 8),
            Exercise(45, "Присед со штангой", 8),
            Exercise(46, "Выпады с гантелями на месте", 8),

            //Пресс
            Exercise(32, "Пресс в TRX горизонтально", 6),
            Exercise(33, "Книжка", 6),
            Exercise(34, "Книжка в TRX", 6),
            Exercise(49, "Русские скручивания", 6)
        )
    }
}