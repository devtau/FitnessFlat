package com.devtau.ironHeroes.util

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.devtau.ironHeroes.R
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import io.reactivex.functions.Action
import timber.log.Timber

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(@StringRes snackbarText: Int, timeLength: Int = LENGTH_LONG) {
    Snackbar.make(this, snackbarText, timeLength).run {
        val tv = view.findViewById(R.id.snackbar_text) as TextView
        val color = ContextCompat.getColor(this.context, R.color.colorWhite)
        tv.setTextColor(color)
        addCallback(object: Snackbar.Callback() {
            override fun onShown(sb: Snackbar?) = EspressoIdlingResource.increment()
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) = EspressoIdlingResource.decrement()
        })
        show()
    }
}


fun View?.showDialog(
    logTag: String, @StringRes msgId: Int?, confirmed: Action? = null, cancelled: Action? = null
) {
    if (this?.context == null || msgId == null) return
    showDialog(logTag, context?.getString(msgId), confirmed, cancelled)
}

fun View?.showDialog(
    logTag: String, msg: String?, confirmed: Action? = null, cancelled: Action? = null
) {
    if (this?.context == null || msg == null) return
    Timber.d("%s, %s", logTag, msg)
    try {
        val builder = AlertDialog.Builder(context)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                confirmed?.run()
                dialog.dismiss()
            }
        if (cancelled != null) {
            builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                cancelled.run()
                dialog.dismiss()
            }
        }

        builder.setMessage(msg).show()
    } catch (e: WindowManager.BadTokenException) {
        Timber.e("$logTag, showDialog. cannot show dialog")
        context.toast(msg)
    }
}

fun Context?.toast(@StringRes msgId: Int) { this?.toast(this.getString(msgId)) }
fun Context?.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context?.toastLong(@StringRes msgId: Int) { this?.toastLong(this.getString(msgId)) }
fun Context?.toastLong(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()