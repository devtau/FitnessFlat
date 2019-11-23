package com.devtau.ironHeroes.ui.activities.statistics

import com.devtau.ironHeroes.data.model.ExerciseInTraining

class Tag(
    val markerColorId: Int,
    val title: String,
    val trainingId: Long?,
    val exerciseInTrainingId: Long?
) {

    companion object {
        private const val LINE_1_2_LENGTH = 10
        private const val LINE_3_LENGTH = 13//includes 3 of ellipsis

        fun getTitle(exerciseInTraining: ExerciseInTraining): String {
            val exerciseName = exerciseInTraining.exercise?.name ?: ""
            var title: String
            if (exerciseName.length > LINE_1_2_LENGTH * 2 + LINE_3_LENGTH - 3) {
                title = exerciseName.substring(0, LINE_1_2_LENGTH * 2 + LINE_3_LENGTH - 3)
                title = title.trim()
                title += "..."
            } else {
                title = exerciseName
            }
            title += '\n'
            if (exerciseInTraining.weight != 0) title += "${exerciseInTraining.weight} * "
            title += "${exerciseInTraining.repeats} * ${exerciseInTraining.count}"
            return title
        }
    }
}