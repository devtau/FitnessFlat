package com.devtau.ff.util

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.PhoneNumberUtils
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.devtau.ff.R
import com.devtau.ff.util.Constants.DATE_FORMATTER_TO_SHOW
import com.devtau.ff.util.Constants.DATE_FORMATTER_TO_STORE
import com.devtau.ff.util.Constants.PHONE_MASK
import com.devtau.ff.util.Constants.STANDARD_DELAY_MS
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

    fun formatDate(storedDate: String?): String {
        storedDate ?: return ""
        val inputDf = SimpleDateFormat(DATE_FORMATTER_TO_STORE, Locale.getDefault())
        val outputDf = SimpleDateFormat(DATE_FORMATTER_TO_SHOW, Locale.getDefault())
        var outputDate = ""
        try {
            val parsed = inputDf.parse(storedDate)
            outputDate = outputDf.format(parsed)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputDate
    }

    fun formatBirthday(cal: Calendar): String =
        SimpleDateFormat(DATE_FORMATTER_TO_SHOW, Locale.getDefault()).format(cal.time)

    fun parseBirthday(birthDay: String?): Calendar {
        val calendar = Calendar.getInstance()
        val inputDf = SimpleDateFormat(DATE_FORMATTER_TO_SHOW, Locale.getDefault())
        try {
            calendar.timeInMillis = inputDf.parse(birthDay).time
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
}