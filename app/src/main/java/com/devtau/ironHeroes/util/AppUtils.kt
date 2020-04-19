package com.devtau.ironHeroes.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.devtau.ironHeroes.data.model.HourMinute
import com.devtau.ironHeroes.enums.ChannelStats
import com.devtau.ironHeroes.util.Constants.PHONE_MASK
import com.devtau.ironHeroes.util.Constants.STANDARD_DELAY_MS
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.reactivex.functions.Action

object AppUtils {

    private const val LOG_TAG = "AppUtils"


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

    fun alert(logTag: String?, msg: String, context: Context?) {
        context ?: return
        try {
            AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                .setMessage(msg)
                .show()
            Logger.e(logTag ?: LOG_TAG, msg)
        } catch (e: WindowManager.BadTokenException) {
            Logger.e(logTag ?: LOG_TAG, "in alert. cannot show dialog")
            context.toast(msg)
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
            context.toast(msg)
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

    fun createChannelIfNeeded(notificationManager: NotificationManager?, channelStats: ChannelStats) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelStats.id, channelStats.channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = channelStats.description

            when (channelStats) {
                ChannelStats.DEFAULT_SOUND -> {/*NOP*/}
                ChannelStats.CUSTOM_SOUND -> {
                    val attrs = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                    channel.setSound(ChannelStats.getCustomNotificationSound(), attrs)
                }
            }

            channel.vibrationPattern = if (channelStats.withVibration)
                longArrayOf(300, 400, 300, 400, 300, 400)//pause-ring-pause...
            else null
            notificationManager?.createNotificationChannel(channel)
        }
    }
}

fun <T>List<T>.print(logTag: String): String {
    val string = this.joinToString("\n", "[\n", "\n]\n")
    Logger.d(logTag, string)
    return string
}

fun <T,R>Map<T,R>.print(logTag: String): String {
    val builder = StringBuilder()
    var delimiter = ""
    for (next in entries) {
        builder.append(delimiter)
        builder.append("key=${next.key}, value=${next.value}")
        delimiter = ",\n"
    }
    val string = builder.toString()
    Logger.d(logTag, string)
    return string
}

fun <T>List<T>.inBounds(index: Int): Boolean = size > index