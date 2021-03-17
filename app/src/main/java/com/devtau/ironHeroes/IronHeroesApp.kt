package com.devtau.ironHeroes

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.devtau.ironHeroes.data.source.repositories.*
import com.devtau.ironHeroes.enums.ChannelStats
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.prefs.PreferencesManager
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.vk.sdk.*
import com.vk.sdk.api.VKError
import timber.log.Timber

class IronHeroesApp: Application() {

    val trainingsRepository: TrainingsRepository
        get() = ServiceLocator.provideTrainingsRepository(this)

    val heroesRepository: HeroesRepository
        get() = ServiceLocator.provideHeroesRepository(this)

    val exercisesInTrainingsRepository: ExercisesInTrainingsRepository
        get() = ServiceLocator.provideExercisesInTrainingsRepository(this)

    val exercisesRepository: ExercisesRepository
        get() = ServiceLocator.provideExercisesRepository(this)

    val muscleGroupsRepository: MuscleGroupsRepository
        get() = ServiceLocator.provideMuscleGroupsRepository(this)


    override fun onCreate() {
        super.onCreate()
        PreferencesManager.init(this)
        Timber.plant(Timber.DebugTree())

        initFirebase()
        initVK()
        initNotificationChannels()
    }


    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Timber.w("getInstanceId. failed ${it.exception}")
                } else {
                    Timber.d("getInstanceId. firebase token=${it.result?.token}")
                }
            }
    }

    private fun initVK() {
        VKSdk.initialize(this)
        object: VKAccessTokenTracker() {
            override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
                if (newToken == null || newToken.isExpired) {
                    PreferencesManager.vkToken = null
                    Timber.d("vk token expired. logout")
                    val intent = Intent(LOGOUT)
                    sendBroadcast(intent)
                }
            }
        }.startTracking()
    }

    private fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            AppUtils.createChannelIfNeeded(notificationManager, ChannelStats.DEFAULT_SOUND)
            AppUtils.createChannelIfNeeded(notificationManager, ChannelStats.CUSTOM_SOUND)
        }
    }


    companion object {
        const val LOGOUT = "com.devtau.ironHeroes.action.LOGOUT"
        private const val LOG_TAG = "AppApplication"

        fun getVKAuthListener(activity: AppCompatActivity?) = object: VKCallback<VKAccessToken> {
            override fun onResult(token: VKAccessToken) {
                PreferencesManager.vkToken = token.accessToken
            }
            override fun onError(error: VKError?) {
                activity ?: return
                val msg = String.format(activity.getString(R.string.error_formatter), error)
                AppUtils.alert(LOG_TAG, msg, activity)
                if (error?.errorCode != VKError.VK_CANCELED) loginVK(activity)
            }
        }

        fun loginVK(activity: AppCompatActivity?) {
            activity ?: return
            VKSdk.logout()
            VKSdk.login(activity, VKScope.PHOTOS)
        }
    }
}