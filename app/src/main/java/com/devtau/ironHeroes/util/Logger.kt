package com.devtau.ironHeroes.util

import android.util.Log
import com.devtau.ironHeroes.BuildConfig

object Logger {

    fun v(tag: String, msg: String?) { if (BuildConfig.WITH_LOGS) Log.v(tag, msg) }
    fun d(tag: String, msg: String?) { if (BuildConfig.WITH_LOGS) Log.d(tag, msg) }
    fun i(tag: String, msg: String?) { if (BuildConfig.WITH_LOGS) Log.i(tag, msg) }
    fun w(tag: String, msg: String?) { if (BuildConfig.WITH_LOGS) Log.w(tag, msg) }
    fun e(tag: String, msg: String?) { Log.e(tag, msg) }
}