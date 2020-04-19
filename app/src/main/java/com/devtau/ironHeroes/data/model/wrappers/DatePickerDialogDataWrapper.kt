package com.devtau.ironHeroes.data.model.wrappers

import java.util.*

data class DatePickerDialogDataWrapper(
    val selectedDate: Calendar, val minDate: Calendar, val maxDate: Calendar
)