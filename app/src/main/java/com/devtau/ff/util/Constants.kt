package com.devtau.ff.util

object Constants {
    const val PHONE_MASK = "+7 ([000]) [000]-[00]-[00]"
    const val UNMASKED_PHONE_LENGTH = 12
    const val STANDARD_DELAY_MS = 300L
    const val CLICKS_DEBOUNCE_RATE_MS = 700L
    const val EMPTY_OBJECT_ID = -1L
    const val OBJECT_ID_NA = 0L

    const val DATE_FORMATTER_TO_SHOW = "dd.MM.yyyy"
    const val DATE_FORMATTER_TO_STORE = "yyyy-MM-dd"

    const val INTERNAL_SERVER_ERROR = 500
    const val BAD_REQUEST = 400
    const val NOT_FOUND = 404
    const val UNAUTHORIZED = 401
    const val TOO_MANY_REQUESTS = 429
}