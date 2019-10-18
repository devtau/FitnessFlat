package com.devtau.ff.ui

import io.reactivex.functions.Action

interface StandardView {
    fun showMsg(msgId: Int, confirmedListener: Action? = null)
    fun showMsg(msg: String, confirmedListener: Action? = null)
}