package com.devtau.ironHeroes.enums

import android.content.Context
import com.devtau.ironHeroes.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class StatisticsType(private val formatterId: Int) {
    WEEK(R.string.day_formatter),
    MONTH(R.string.day_formatter);


    fun getFormattedValue(context: Context, value: Int): String {
        val dayFormatter = SimpleDateFormat("EEE", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, 0)
        cal.set(Calendar.DAY_OF_MONTH, value)
        val dayOfWeek = dayFormatter.format(cal.time)

        return when (this) {
            WEEK -> String.format(Locale.getDefault(), context.getString(formatterId), value, dayOfWeek)
            MONTH -> String.format(Locale.getDefault(), context.getString(formatterId), value, dayOfWeek)
        }
    }
}
