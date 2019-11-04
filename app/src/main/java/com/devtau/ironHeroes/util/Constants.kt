package com.devtau.ironHeroes.util

object Constants {
    const val PHONE_MASK = "+7 ([000]) [000]-[00]-[00]"
    const val UNMASKED_PHONE_LENGTH = 12
    const val STANDARD_DELAY_MS = 300L
    const val CLICKS_DEBOUNCE_RATE_MS = 700L
    const val EMPTY_OBJECT_ID = -1L
    const val OBJECT_ID_NA = 0L
    const val INTEGER_NOT_PARSED = -1

    const val DATE_FORMATTER = "dd.MM.yyyy"
    const val DATE_TIME_FORMATTER = "dd.MM HH:mm"
    const val DATE_WITH_WEEK_DAY_FORMATTER = "dd.MM, EE"
    const val DATE_TIME_WITH_WEEK_DAY_FORMATTER = "dd.MM HH:mm, EE"

    const val INTERNAL_SERVER_ERROR = 500
    const val BAD_REQUEST = 400
    const val NOT_FOUND = 404
    const val UNAUTHORIZED = 401
    const val TOO_MANY_REQUESTS = 429

    const val HERO_ID = "heroId"
    const val HUMAN_TYPE = "humanType"
    const val TRAINING_ID = "trainingId"
    const val EXERCISE_IN_TRAINING_ID = "exerciseInTrainingId"
}