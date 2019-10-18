package com.devtau.ff.rest

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.StringRes
import com.devtau.ff.BuildConfig
import com.devtau.ff.R
import com.devtau.ff.rest.listeners.ClientRegisteredListener
import com.devtau.ff.rest.model.Client
import com.devtau.ff.rest.response.BaseResponse
import com.devtau.ff.rest.response.RegistrationResponse
import com.devtau.ff.rest.response.ClientResponse
import com.devtau.ff.util.AppUtils
import com.devtau.ff.util.Constants.INTERNAL_SERVER_ERROR
import com.devtau.ff.util.Constants.TOO_MANY_REQUESTS
import com.devtau.ff.util.Constants.UNAUTHORIZED
import com.devtau.ff.util.Logger
import io.reactivex.functions.Consumer
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class NetworkLayerImpl(private val context: Context): NetworkLayer {

    private var httpClientLogging: OkHttpClient? = null
    private var httpClientNotLogging: OkHttpClient? = null


    override fun validatePhone(phone: String) {
        if (!AppUtils.checkConnection(context)) return
        getBackendAPIClient(true).validatePhone(phone)
            .enqueue(object: BaseCallback<BaseResponse>() {
                override fun processBody(responseBody: BaseResponse?) = showToast(R.string.wait_for_sms)
            })
    }

    override fun registerNewClient(client: Client, smsValidationCode: Int?, listener: ClientRegisteredListener) {
        if (!AppUtils.checkConnection(context)) return
        getBackendAPIClient(true).registerNewClient(client, smsValidationCode)
                .enqueue(object: BaseCallback<RegistrationResponse>() {
                    override fun processBody(responseBody: RegistrationResponse?) =
                            listener.processClientRegistered(responseBody?.token, responseBody?.client)
                })
    }

    override fun getClient(token: String?, listener: Consumer<Client?>) {
        if (!AppUtils.checkConnection(context) || TextUtils.isEmpty(token)) return
        getBackendAPIClient(true).getClient("Bearer $token")
                .enqueue(object: BaseCallback<ClientResponse>() {
                    override fun processBody(responseBody: ClientResponse?) = listener.accept(responseBody?.client)
                })
    }

    override fun updateClient(client: Client?, token: String?) {
        if (!AppUtils.checkConnection(context) || TextUtils.isEmpty(token) || client == null) return
        getBackendAPIClient(true).updateClient("Bearer $token", client)
                .enqueue(object: BaseCallback<ClientResponse>() {
                    override fun processBody(responseBody: ClientResponse?)
                            = Logger.d(LOG_TAG, "client updated. $responseBody")
                })
    }


    private fun showToast(@StringRes msgId: Int) = showToast(context.getString(msgId))
    private fun showToast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    private fun showDialog(@StringRes msgId: Int) = AppUtils.alertD(LOG_TAG, msgId, context)

    private fun getBackendAPIClient(loggerNeeded: Boolean): BackendAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER)
                .client(getClient(loggerNeeded))
                .build()
        return retrofit.create(BackendAPI::class.java)
    }

    private fun getClient(loggerNeeded: Boolean): OkHttpClient {
        fun buildClient(loggerNeeded: Boolean): OkHttpClient {
            val builder = OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
            if (BuildConfig.WITH_LOGS && loggerNeeded) {
                builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
            return builder.build()
        }

        var client: OkHttpClient? = if (loggerNeeded) httpClientLogging else httpClientNotLogging
        if (client == null) {
            synchronized(NetworkLayerImpl::class.java) {
                client = if (loggerNeeded) httpClientLogging else httpClientNotLogging
                if (client == null) {
                    if (loggerNeeded) {
                        httpClientLogging = buildClient(true)
                        client = httpClientLogging
                    } else {
                        httpClientNotLogging = buildClient(false)
                        client = httpClientNotLogging
                    }
                }
            }
        }
        return client!!
    }


    private abstract inner class BaseCallback<T>: Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val baseResponseBody = response.body()
            if (response.isSuccessful) {
                Logger.d(LOG_TAG, "retrofit response isSuccessful")
                processBody(baseResponseBody)
            } else {
                handleError(response.code(), response.errorBody(), response.body())
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Logger.e(LOG_TAG, "retrofit failure: " + t.localizedMessage)
            val localizedMessage = t.localizedMessage ?: return
            when {
                localizedMessage.contains("Unable to resolve host") -> showToast(R.string.check_internet_connection)
                localizedMessage.contains("Expected value") -> showToast(R.string.serializable_object_changed)
                else -> showToast(localizedMessage)
            }
        }


        private fun handleError(errorCode: Int, errorBody: ResponseBody?, responseBody: T?) {
            var errorMsg = "retrofit error code: $errorCode"
            when (errorCode) {
                INTERNAL_SERVER_ERROR -> showDialog(R.string.internal_server_error)
                TOO_MANY_REQUESTS -> showDialog(R.string.too_many_requests)
                else -> {
                    try {
                        errorMsg += "\nmessage: " + JSONObject(errorBody?.string()).getString("message")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    showToast(errorMsg)
                }
            }
            if (errorCode != UNAUTHORIZED) Logger.e(LOG_TAG, errorMsg)
        }

        abstract fun processBody(responseBody: T?): Unit?
    }


    companion object {
        private const val LOG_TAG = "NetworkLayer"
        private const val TIMEOUT_CONNECT = 10L
        private const val TIMEOUT_READ = 60L
        private const val TIMEOUT_WRITE = 120L
    }
}