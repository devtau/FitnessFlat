package com.devtau.ironHeroes.rest

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.StringRes
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.rest.listeners.HeroRegisteredListener
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.rest.response.BaseResponse
import com.devtau.ironHeroes.rest.response.RegistrationResponse
import com.devtau.ironHeroes.rest.response.HeroResponse
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import com.google.gson.GsonBuilder
import io.reactivex.functions.Consumer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object NetworkLayerImpl: NetworkLayer {

    private const val LOG_TAG = "NetworkLayer"
    private const val TIMEOUT_CONNECT = 10L
    private const val TIMEOUT_READ = 60L
    private const val TIMEOUT_WRITE = 120L

    private lateinit var context: Context
    private lateinit var httpClientLogging: OkHttpClient
    private lateinit var httpClientNotLogging: OkHttpClient


    @Synchronized
    @Throws(Exception::class)
    fun init(context: Context) {
        if (context !is Application) throw Exception("don't call init() other than from Application.onCreate() for this leads to memory leaks")
        this.context = context
        httpClientLogging = buildClient(true)
        httpClientNotLogging = buildClient(false)
    }


    //<editor-fold desc="interface overrides">
    override fun validatePhone(phone: String) {
        if (!AppUtils.checkConnection(context)) return
        getBackendApiClient(true).validatePhone(phone)
            .enqueue(object: BaseCallback<BaseResponse>() {
                override fun processBody(responseBody: BaseResponse?) = showToast(R.string.wait_for_sms)
            })
    }

    override fun registerNewHero(hero: Hero, smsValidationCode: Int?, listener: HeroRegisteredListener) {
        if (!AppUtils.checkConnection(context)) return
        getBackendApiClient(true).registerNewHero(hero, smsValidationCode)
            .enqueue(object: BaseCallback<RegistrationResponse>() {
                override fun processBody(responseBody: RegistrationResponse?) =
                    listener.processHeroRegistered(responseBody?.token, responseBody?.hero)
            })
    }

    override fun getHero(token: String?, listener: Consumer<Hero?>) {
        if (!AppUtils.checkConnection(context) || TextUtils.isEmpty(token)) return
        getBackendApiClient(true).getHero("Bearer $token")
            .enqueue(object: BaseCallback<HeroResponse>() {
                override fun processBody(responseBody: HeroResponse?) = listener.accept(responseBody?.hero)
            })
    }

    override fun updateHero(hero: Hero?, token: String?) {
        if (!AppUtils.checkConnection(context) || TextUtils.isEmpty(token) || hero == null) return
        getBackendApiClient(true).updateHero("Bearer $token", hero)
            .enqueue(object: BaseCallback<HeroResponse>() {
                override fun processBody(responseBody: HeroResponse?)
                        = Logger.d(LOG_TAG, "hero updated. $responseBody")
            })
    }
    //</editor-fold>


    fun showToast(@StringRes msgId: Int) = showToast(context.getString(msgId))
    fun showToast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    fun showDialog(@StringRes msgId: Int) = AppUtils.alertD(LOG_TAG, msgId, context)

    private fun getBackendApiClient(loggerNeeded: Boolean): BackendAPI = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .client(if (loggerNeeded) httpClientLogging else httpClientNotLogging)
        .build()
        .create(BackendAPI::class.java)

    private fun buildClient(loggerNeeded: Boolean): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
        if (BuildConfig.WITH_LOGS && loggerNeeded) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        return builder.build()
    }
}