package com.devtau.ironHeroes.rest

import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.ui.StandardView
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.Logger
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BaseCallback<T>(private val view: StandardView): Callback<T> {
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
            localizedMessage.contains("Unable to resolve host") ->
                view.showMsg(R.string.check_internet_connection)
            localizedMessage.contains("Expected value") ->
                view.showMsg(R.string.serializable_object_changed)
            else -> view.showMsg(localizedMessage)
        }
    }


    private fun handleError(errorCode: Int, errorBody: ResponseBody?, responseBody: T?) {
        var errorMsg = "retrofit error code: $errorCode"
        when (errorCode) {
            Constants.INTERNAL_SERVER_ERROR ->
                view.showMsg(R.string.internal_server_error)
            Constants.TOO_MANY_REQUESTS ->
                view.showMsg(R.string.too_many_requests)
            else -> {
                try {
                    errorMsg += "\nmessage: " + JSONObject(errorBody?.string()).getString("message")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                view.showMsg(errorMsg)
            }
        }
        if (errorCode != Constants.UNAUTHORIZED) Logger.e(LOG_TAG, errorMsg)
    }

    abstract fun processBody(responseBody: T?): Unit?


    companion object {
        private const val LOG_TAG = "NetworkLayer"
    }
}