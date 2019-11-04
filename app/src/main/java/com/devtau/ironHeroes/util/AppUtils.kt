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
import com.devtau.ironHeroes.util.Constants.DATE_FORMATTER
import com.devtau.ironHeroes.util.Constants.DATE_TIME_FORMATTER
import com.devtau.ironHeroes.util.Constants.DATE_TIME_WITH_WEEK_DAY_FORMATTER
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
    fun formatDate(cal: Calendar): String =
        SimpleDateFormat(DATE_FORMATTER, Locale.getDefault()).format(cal.time)

    fun formatDateTimeWithWeekDay(timeInMillis: Long?): String {
        val date = Calendar.getInstance()
        if (timeInMillis != null) date.timeInMillis = timeInMillis
        return formatDateTimeWithWeekDay(date)
    }
    fun formatDateTimeWithWeekDay(cal: Calendar): String =
        SimpleDateFormat(DATE_TIME_WITH_WEEK_DAY_FORMATTER, Locale.getDefault()).format(cal.time)

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

    fun alert(logTag: String?, msg: String, context: Context, confirmedListener: Action) {
        try {
            AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    confirmedListener.run()
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setMessage(msg).show()
            Logger.e(logTag ?: LOG_TAG, msg)
        } catch (e: WindowManager.BadTokenException) {
            Logger.e(logTag ?: LOG_TAG, "in alert. cannot show dialog")
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun alertD(logTag: String?, @StringRes msgId: Int, context: Context, confirmedListener: Action? = null)
            = alertD(logTag, context.getString(msgId), context, confirmedListener)

    fun alertD(logTag: String?, msg: String, context: Context, confirmedListener: Action? = null) {
        try {
            AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    confirmedListener?.run()
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setMessage(msg).show()
        } catch (e: WindowManager.BadTokenException) {
            Logger.e(logTag ?: LOG_TAG, "in alert. cannot show dialog")
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
}