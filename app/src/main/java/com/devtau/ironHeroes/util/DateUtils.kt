package com.devtau.ironHeroes.util

import com.devtau.ironHeroes.data.model.HourMinute
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val LOG_TAG = "DateUtils"

    private const val DATE_FORMATTER = "dd.MM.yyyy"
    private const val DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm"
    private const val DATE_WITH_WEEK_DAY_FORMATTER = "dd.MM (EE)"
    private const val SHORT_DATE_FORMATTER = "dd.MM"
    private const val DATE_TIME_WITH_WEEK_DAY_FORMATTER = "dd.MM HH:mm (EE)"


    fun formatDate(timeInMillis: Long?): String? {
        timeInMillis ?: return null
        val date = Calendar.getInstance()
        date.timeInMillis = timeInMillis
        return formatDate(date)
    }
    fun formatDate(cal: Calendar): String = formatAnyDate(cal, DATE_FORMATTER)

    fun formatDateTimeWithWeekDay(timeInMillis: Long?): String {
        val date = Calendar.getInstance()
        if (timeInMillis != null) date.timeInMillis = timeInMillis
        return formatDateTimeWithWeekDay(date)
    }
    fun formatDateTimeWithWeekDay(cal: Calendar): String = formatAnyDate(cal, DATE_TIME_WITH_WEEK_DAY_FORMATTER)

    fun formatDateWithWeekDay(timeInMillis: Long?): String {
        val date = Calendar.getInstance()
        if (timeInMillis != null) date.timeInMillis = timeInMillis
        return formatDateWithWeekDay(date)
    }
    fun formatDateWithWeekDay(cal: Calendar): String = formatAnyDate(cal, DATE_WITH_WEEK_DAY_FORMATTER)

    fun formatShortDate(timeInMillis: String?): String {
        val timeTrimmed = timeInMillis?.replace(",", "")?.replace(" ", "")
        val date = Calendar.getInstance()
        try {
            if (timeTrimmed != null) date.timeInMillis = timeTrimmed.toLong()
        } catch (e: NumberFormatException) {
            Logger.e(LOG_TAG, "formatShortDate. bad input=$timeInMillis")
        }
        return formatAnyDate(date, SHORT_DATE_FORMATTER)
    }

    fun formatShortDate(date: Calendar): String = formatAnyDate(date, SHORT_DATE_FORMATTER)

    private fun formatAnyDate(cal: Calendar, formatter: String): String =
        SimpleDateFormat(formatter, Locale.getDefault()).format(cal.time)

    fun parseDate(date: String?): Calendar {
        val calendar = Calendar.getInstance()
        val inputDf = SimpleDateFormat(DATE_FORMATTER, Locale.getDefault())
        try {
            calendar.timeInMillis = inputDf.parse(date).time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return calendar
    }

    fun parseDateTime(date: String?): Calendar {
        val calendar = Calendar.getInstance()
        val inputDf = SimpleDateFormat(DATE_TIME_FORMATTER, Locale.getDefault())
        try {
            calendar.timeInMillis = inputDf.parse(date).time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return calendar
    }

    fun parseDateTimeWithWeekDay(date: String?): Calendar? {
        date ?: return null
        val calendar = Calendar.getInstance()
        val inputDf = SimpleDateFormat(DATE_TIME_WITH_WEEK_DAY_FORMATTER, Locale.getDefault())
        try {
            calendar.timeInMillis = inputDf.parse(date).time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return calendar
    }

    fun getRoundDate() = getRoundDate(null, null, null, null, null)
    fun getRoundDate(year: Int?, month: Int?, dayOfMonth: Int?, hour: Int?, minute: Int?): Calendar {
        val date = Calendar.getInstance()
        if (year != null) date.set(Calendar.YEAR, year)
        if (month != null) date.set(Calendar.MONTH, month)
        if (dayOfMonth != null) date.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val hourLoc = hour ?: date.get(Calendar.HOUR_OF_DAY)
        val minuteLoc = minute ?: date.get(Calendar.MINUTE)
        val hourMinute = roundMinutesIn10MinutesIntervals(hourLoc, minuteLoc)
        date.set(Calendar.HOUR_OF_DAY, hourMinute.hour)
        date.set(Calendar.MINUTE, hourMinute.minute)
        date.set(Calendar.SECOND, 0)
        return date
    }


    private fun roundMinutesInHalfHourIntervals(hour: Int, minute: Int): HourMinute =
        if (hour == 23 && minute > 44) HourMinute(hour, 30)
        else when (minute) {
            in 1..14 -> HourMinute(hour, 0)
            in 15..29 -> HourMinute(hour, 30)
            in 31..44 -> HourMinute(hour, 30)
            in 45..59 -> HourMinute(hour + 1, 0)
            else -> HourMinute(hour, minute)
        }


    private fun roundMinutesIn10MinutesIntervals(hour: Int, minute: Int): HourMinute =
        if (hour == 23 && minute > 54) HourMinute(hour, 50)
        else when (minute) {
            in 1..4 -> HourMinute(hour, 0)
            in 5..14 -> HourMinute(hour, 10)
            in 15..24 -> HourMinute(hour, 20)
            in 25..34 -> HourMinute(hour, 30)
            in 35..44 -> HourMinute(hour, 40)
            in 45..54 -> HourMinute(hour, 50)
            in 55..59 -> HourMinute(hour + 1, 0)
            else -> HourMinute(hour, minute)
        }
}