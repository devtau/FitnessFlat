package com.devtau.ironHeroes

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import com.vk.sdk.*
import com.vk.sdk.api.VKError

class IronHeroesApp: Application() {

    private var prefs: PreferencesManager? = null


    override fun onCreate() {
        super.onCreate()
        prefs = PreferencesManager(this)
        //vk
        VKSdk.initialize(this)
        object: VKAccessTokenTracker() {
            override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
                if (newToken == null || newToken.isExpired) {
                    prefs?.vkToken = null
                    Logger.d(LOG_TAG, "vk token expired. logout")
                    val intent = Intent(LOGOUT)
                    sendBroadcast(intent)
                }
            }
        }.startTracking()
    }

    companion object {
        const val LOGOUT = "com.devtau.ironHeroes.action.LOGOUT"
        private const val LOG_TAG = "AppApplication"

        fun getVKAuthListener(activity: AppCompatActivity, prefs: PreferencesManager?) = object:
            VKCallback<VKAccessToken> {
            override fun onResult(token: VKAccessToken) = handleToken(prefs, token.accessToken)
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

        fun handleToken(prefs: PreferencesManager?, token: String?) {
            prefs?.vkToken = token
        }
    }
}