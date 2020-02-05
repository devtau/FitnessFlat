package com.devtau.ironHeroes.ui

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import io.reactivex.functions.Action

interface StandardView {
    fun showMsg(msgId: Int, confirmedListener: Action? = null, cancelledListener: Action? = null)
    fun showMsg(msg: String, confirmedListener: Action? = null, cancelledListener: Action? = null)
    fun resolveString(@StringRes stringId: Int): String
    fun resolveColor(@ColorRes colorId: Int): Int
    fun isOnline(): Boolean
}