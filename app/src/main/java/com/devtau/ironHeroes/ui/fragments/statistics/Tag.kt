package com.devtau.ironHeroes.ui.fragments.statistics

import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants

class Tag(
    val markerColorId: Int,
    val title: String,
    val trainingId: Long?,
    val trainingDate: Long?,
    val exerciseInTrainingId: Long?
) {

    companion object {
        private const val DATE_SEPARATOR = " - "
        private const val LINES_LENGTH = 13
        private const val LINES_COUNT = 3
        private const val ELLIPSIS = "..."

        fun getTitle(exerciseInTraining: ExerciseInTraining): String {
            val exerciseName = exerciseInTraining.exercise?.name ?: ""
            var title: String
            val datePrefix = AppUtils.formatShortDate(exerciseInTraining.training?.date?.toString()) + DATE_SEPARATOR
            val maxTitleLength = LINES_LENGTH * LINES_COUNT
            val maxExerciseNameLength = maxTitleLength - datePrefix.length
            if (exerciseName.length > maxExerciseNameLength) {
                title = datePrefix + exerciseName.substring(0, maxExerciseNameLength - ELLIPSIS.length)
                title = title.trim()
                title += ELLIPSIS
            } else {
                title = datePrefix + exerciseName
            }
            title += '\n'
            if (exerciseInTraining.weight != 0) title += "${exerciseInTraining.weight} * "
            title += "${exerciseInTraining.repeats} * ${exerciseInTraining.count}"
            return title
        }
    }
}