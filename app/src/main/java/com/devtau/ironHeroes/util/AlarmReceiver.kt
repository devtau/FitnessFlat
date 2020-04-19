package com.devtau.ironHeroes.util

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.d(LOG_TAG, "onReceive")
        goAsync()
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = intent?.getParcelableExtra(NOTIFICATION) as Notification?
        val id = intent?.getIntExtra(NOTIFICATION_ID, 0)
        if (id != null) notificationManager.notify(id, notification)
    }


    companion object {
        const val LOG_TAG = "AlarmReceiver"
        const val NOTIFICATION_ID = "notification-id"
        const val NOTIFICATION = "notification"
    }
}