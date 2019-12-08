package com.devtau.ironHeroes

import com.devtau.ironHeroes.util.Logger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseReceiverService: FirebaseMessagingService() {

    //вызывается только если приложение в форграунде
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val msg = "onMessageReceived. " +
                "заголовок: " + remoteMessage.notification?.title + ", " +
                "текст сообщения: " + remoteMessage.notification?.body + ", " +
                "пользовательские данные: " + remoteMessage.data + ", " +
                "отправитель: " + remoteMessage.from
        Logger.d(LOG_TAG, msg)
    }

    override fun onNewToken(refreshedToken: String) {
        Logger.d(LOG_TAG, "refreshedToken: $refreshedToken")
//        prefs.firebaseToken = refreshedToken
    }


    companion object {
        private const val LOG_TAG = "FirebaseReceiverService"
    }
}