package com.devtau.ironHeroes.rest

import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.rest.listeners.HeroRegisteredListener
import com.devtau.ironHeroes.rest.response.BaseResponse
import com.devtau.ironHeroes.rest.response.HeroResponse
import com.devtau.ironHeroes.rest.response.RegistrationResponse
import com.devtau.ironHeroes.ui.StandardView
import com.devtau.ironHeroes.util.Logger
import com.google.gson.GsonBuilder
import io.reactivex.functions.Consumer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkLayerImpl(val view: StandardView): NetworkLayer {

    private var httpClientLogging = buildClient(true)
    private var httpClientNotLogging = buildClient(false)


    //<editor-fold desc="Interface overrides">
    override fun validatePhone(phone: String) {
        if (!view.isOnline()) return
        getBackendApiClient(true).validatePhone(phone)
            .enqueue(object: BaseCallback<BaseResponse>(view) {
                override fun processBody(responseBody: BaseResponse?) = view.showMsg(R.string.wait_for_sms)
            })
    }

    override fun registerNewHero(hero: Hero, smsValidationCode: Int?, listener: HeroRegisteredListener) {
        if (!view.isOnline()) return
        getBackendApiClient(true).registerNewHero(hero, smsValidationCode)
            .enqueue(object: BaseCallback<RegistrationResponse>(view) {
                override fun processBody(responseBody: RegistrationResponse?) =
                    listener.processHeroRegistered(responseBody?.token, responseBody?.hero)
            })
    }

    override fun getHero(token: String, listener: Consumer<Hero?>) {
        if (!view.isOnline() || token.isEmpty()) return
        getBackendApiClient(true).getHero("Bearer $token")
            .enqueue(object: BaseCallback<HeroResponse>(view) {
                override fun processBody(responseBody: HeroResponse?) = listener.accept(responseBody?.hero)
            })
    }

    override fun updateHero(hero: Hero?, token: String) {
        if (!view.isOnline() || token.isEmpty() || hero == null) return
        getBackendApiClient(true).updateHero("Bearer $token", hero)
            .enqueue(object: BaseCallback<HeroResponse>(view) {
                override fun processBody(responseBody: HeroResponse?)
                        = Logger.d(LOG_TAG, "hero updated. $responseBody")
            })
    }
    //</editor-fold>


    //<editor-fold desc="Private methods">
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
    //</editor-fold>


    companion object {
        private const val LOG_TAG = "NetworkLayer"
        private const val TIMEOUT_CONNECT = 10L
        private const val TIMEOUT_READ = 60L
        private const val TIMEOUT_WRITE = 120L
    }
}