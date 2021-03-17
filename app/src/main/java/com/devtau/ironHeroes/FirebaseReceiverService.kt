package com.devtau.ironHeroes

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class FirebaseReceiverService: FirebaseMessagingService() {

    //вызывается только если приложение в форграунде
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val msg = "onMessageReceived. " +
                "заголовок: " + remoteMessage.notification?.title + ", " +
                "текст сообщения: " + remoteMessage.notification?.body + ", " +
                "пользовательские данные: " + remoteMessage.data + ", " +
                "отправитель: " + remoteMessage.from
        Timber.d(msg)
    }

    override fun onNewToken(refreshedToken: String) {
        Timber.d("refreshedToken: $refreshedToken")
//        prefs.firebaseToken = refreshedToken
    }
}