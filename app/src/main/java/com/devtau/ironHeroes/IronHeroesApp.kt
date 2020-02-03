package com.devtau.ironHeroes

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.devtau.ironHeroes.data.DataLayerImpl
import com.devtau.ironHeroes.enums.ChannelStats
import com.devtau.ironHeroes.rest.NetworkLayerImpl
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.vk.sdk.*
import com.vk.sdk.api.VKError

class IronHeroesApp: Application() {

    override fun onCreate() {
        super.onCreate()
        DataLayerImpl.init(this)
        NetworkLayerImpl.init(this)
        PreferencesManager.init(this)

        FirebaseApp.initializeApp(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Logger.w(LOG_TAG, "getInstanceId. failed ${it.exception}")
                } else {
                    Logger.d(LOG_TAG, "getInstanceId. firebase token=${it.result?.token}")
                }
            }

        //vk
        VKSdk.initialize(this)
        object: VKAccessTokenTracker() {
            override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
                if (newToken == null || newToken.isExpired) {
                    PreferencesManager.vkToken = null
                    Logger.d(LOG_TAG, "vk token expired. logout")
                    val intent = Intent(LOGOUT)
                    sendBroadcast(intent)
                }
            }
        }.startTracking()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            AppUtils.createChannelIfNeeded(notificationManager, ChannelStats.DEFAULT_SOUND)
            AppUtils.createChannelIfNeeded(notificationManager, ChannelStats.CUSTOM_SOUND)
        }
    }

    companion object {
        const val LOGOUT = "com.devtau.ironHeroes.action.LOGOUT"
        private const val LOG_TAG = "AppApplication"

        fun getVKAuthListener(activity: AppCompatActivity) = object: VKCallback<VKAccessToken> {
            override fun onResult(token: VKAccessToken) {
                PreferencesManager.vkToken = token.accessToken
            }
            override fun onError(error: VKError?) {
                val msg = String.format(activity.getString(R.string.error_formatter), error)
                AppUtils.alert(LOG_TAG, msg, activity)
                if (error?.errorCode != VKError.VK_CANCELED) loginVK(activity)
            }
        }

        fun loginVK(activity: AppCompatActivity) {
            VKSdk.logout()
            VKSdk.login(activity, VKScope.PHOTOS)
        }
    }
}