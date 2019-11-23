package com.devtau.ironHeroes.util

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.PhoneNumberUtils
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.model.HourMinute
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.util.Constants.DATE_FORMATTER
import com.devtau.ironHeroes.util.Constants.DATE_TIME_FORMATTER
import com.devtau.ironHeroes.util.Constants.DATE_TIME_WITH_WEEK_DAY_FORMATTER
import com.devtau.ironHeroes.util.Constants.DATE_WITH_WEEK_DAY_FORMATTER
import com.devtau.ironHeroes.util.Constants.PHONE_MASK
import com.devtau.ironHeroes.util.Constants.STANDARD_DELAY_MS
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.reactivex.functions.Action
import org.jetbrains.annotations.Contract
import java.text.SimpleDateFormat
import java.util.*

object AppUtils {

    private const val LOG_TAG = "AppUtils"


    @Contract("null -> true")
    fun isEmpty(list: List<*>?): Boolean = list == null || list.isEmpty()
    @Contract("null -> true")
    fun isEmpty(map: Map<*, *>?): Boolean = map == null || map.isEmpty()
    @Contract("null -> false")
    fun notEmpty(list: List<*>?): Boolean = !isEmpty(list)
    @Contract("null -> false")
    fun notEmpty(map: Map<*, *>?): Boolean = !isEmpty(map)


    fun checkConnection(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnectedOrConnecting
    }

    fun initPhoneRMR(phoneInput: EditText?, savedPhone: String? = null) {
        phoneInput ?: return
        val mask = PHONE_MASK
        val maskedTextChangedListener = MaskedTextChangedListener(mask, false, phoneInput, null, null)
        phoneInput.addTextChangedListener(maskedTextChangedListener)
        phoneInput.setText(savedPhone)
    }

    fun clearPhoneFromMask(savedPhone: String?): String = PhoneNumberUtils.normalizeNumber(savedPhone)

    fun formatDate(timeInMillis: Long?): String {
        val date = Calendar.getInstance()
        if (timeInMillis != null) date.timeInMillis = timeInMillis
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

    fun initToolbar(activity: AppCompatActivity, titleId: Int, backArrowNeeded: Boolean) {
        activity.findViewById<TextView>(R.id.toolbarTitle).text = activity.getString(titleId)
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar ?: return
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(backArrowNeeded)
        toolbar.setNavigationOnClickListener { activity.onBackPressed() }
    }

    fun toggleSoftInput(show: Boolean, field: EditText?, activity: AppCompatActivity?) {
        Logger.d(LOG_TAG, "toggleSoftInput. " + (if (show) "show" else "hide")
                + ", field " + (if (field == null) "is null" else "ok")
                + ", activity " + (if (activity == null) "is null" else "ok"))
        if (show) {
            field?.requestFocus()
            field?.postDelayed({
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT)
            }, STANDARD_DELAY_MS)
        } else {
            field?.clearFocus()
            field?.postDelayed({
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                val windowToken = field.windowToken ?: return@postDelayed
                imm?.hideSoftInputFromWindow(windowToken, 0)
            }, STANDARD_DELAY_MS)
        }
    }

    fun alert(logTag: String?, msg: String, context: Context) {
        try {
            AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                .setMessage(msg)
                .show()
            Logger.e(logTag ?: LOG_TAG, msg)
        } catch (e: WindowManager.BadTokenException) {
            Logger.e(logTag ?: LOG_TAG, "in alert. cannot show dialog")
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun alert(logTag: String?, msg: String, context: Context, confirmedListener: Action? = null) {
        Logger.e(logTag ?: LOG_TAG, msg)
        try {
            val builder = AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    confirmedListener?.run()
                    dialog.dismiss()
                }
            if (confirmedListener != null)
                builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }

            builder.setMessage(msg).show()
        } catch (e: WindowManager.BadTokenException) {
            Logger.e(logTag ?: LOG_TAG, "in alert. cannot show dialog")
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun alertD(logTag: String?, @StringRes msgId: Int, context: Context, confirmedListener: Action? = null)
            = alertD(logTag, context.getString(msgId), context, confirmedListener)

    fun alertD(logTag: String?, msg: String, context: Context, confirmedListener: Action? = null) {
        Logger.d(logTag ?: LOG_TAG, msg)
        try {
            val builder = AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    confirmedListener?.run()
                    dialog.dismiss()
                }
            if (confirmedListener != null)
                builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }

            builder.setMessage(msg).show()
        } catch (e: WindowManager.BadTokenException) {
            Logger.e(logTag ?: LOG_TAG, "in alertD. cannot show dialog")
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun initSpinner(spinner: Spinner?, spinnerStrings: List<String>?, selectedIndex: Int, context: Context?) {
        if (spinner == null || spinnerStrings == null || context == null) {
            Logger.e(LOG_TAG, "initSpinner. bad data. aborting")
            return
        }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerStrings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(selectedIndex)
    }

    fun updateInputField(input: TextView?, value: String?) {
        if (input != null && input.text?.toString() != value) {
            input.setText(value)
            if (input is EditText) input.setSelection(value?.length ?: 0)
        }
    }

    fun roundMinutesInHalfHourIntervals(hour: Int, minute: Int): HourMinute =
        if (hour == 23 && minute > 44) HourMinute(hour, 30)
        else when (minute) {
            in 1..14 -> HourMinute(hour, 0)
            in 15..29 -> HourMinute(hour, 30)
            in 31..44 -> HourMinute(hour, 30)
            in 45..59 -> HourMinute(hour + 1, 0)
            else -> HourMinute(hour, minute)
        }

    fun getRoundDate() = getRoundDate(null, null, null, null, null)
    fun getRoundDate(year: Int?, month: Int?, dayOfMonth: Int?, hour: Int?, minute: Int?): Calendar {
        val date = Calendar.getInstance()
        if (year != null) date.set(Calendar.YEAR, year)
        if (month != null) date.set(Calendar.MONTH, month)
        if (dayOfMonth != null) date.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val hourLoc = hour ?: date.get(Calendar.HOUR_OF_DAY)
        val minuteLoc = minute ?: date.get(Calendar.MINUTE)
        val hourMinute = roundMinutesInHalfHourIntervals(hourLoc, minuteLoc)
        date.set(Calendar.HOUR_OF_DAY, hourMinute.hour)
        date.set(Calendar.MINUTE, hourMinute.minute)
        date.set(Calendar.SECOND, 0)
        return date
    }

    fun getMuscleGroupsSpinnerStrings(list: List<MuscleGroup>?): List<String> {
        val spinnerStrings = ArrayList<String>()
        if (list != null) for (next in list) spinnerStrings.add(next.name)
        return spinnerStrings
    }

    fun getExercisesSpinnerStrings(list: List<Exercise>?): List<String> {
        val spinnerStrings = ArrayList<String>()
        if (list != null) for (next in list) spinnerStrings.add(next.name)
        return spinnerStrings
    }

    fun getSelectedExerciseIndex(list: List<Exercise>?, selectedId: Long?): Int {
        var index = 0
        if (list != null) for (i in list.indices)
            if (list[i].id == selectedId) index = i
        return index
    }

    fun getSelectedMuscleGroupIndex(list: List<MuscleGroup>?, selectedId: Long?): Int {
        var index = 0
        if (list != null) for (i in list.indices)
            if (list[i].id == selectedId) index = i
        return index
    }
}