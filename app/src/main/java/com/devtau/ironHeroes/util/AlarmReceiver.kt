package com.devtau.ironHeroes.util

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("onReceive")
        goAsync()
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = intent?.getParcelableExtra(NOTIFICATION) as Notification?
        val id = intent?.getIntExtra(NOTIFICATION_ID, 0)
        if (id != null) notificationManager.notify(id, notification)
    }


    companion object {
        const val NOTIFICATION_ID = "notification-id"
        const val NOTIFICATION = "notification"
    }
}