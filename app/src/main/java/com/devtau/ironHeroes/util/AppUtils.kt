package com.devtau.ironHeroes.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.devtau.ironHeroes.enums.ChannelStats
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.reactivex.functions.Action
import timber.log.Timber

object AppUtils {

    private const val PHONE_MASK = "+7 ([000]) [000]-[00]-[00]"

    fun initPhoneRMR(phoneInput: EditText?, savedPhone: String? = null) {
        phoneInput ?: return
        val mask = PHONE_MASK
        val maskedTextChangedListener = MaskedTextChangedListener(mask, false, phoneInput, null, null)
        phoneInput.addTextChangedListener(maskedTextChangedListener)
        phoneInput.setText(savedPhone)
    }

    fun clearPhoneFromMask(savedPhone: String?): String = PhoneNumberUtils.normalizeNumber(savedPhone)

    fun alert(logTag: String?, msg: String, context: Context, confirmedListener: Action? = null) {
        Timber.e("%s, %s", logTag, msg)
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
            Timber.e("%s, %s", logTag, "in alert. cannot show dialog")
            context.toast(msg)
        }
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
    Timber.d("%s, %s", logTag, string)
    return string
}