package com.devtau.ironHeroes.util

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.devtau.ironHeroes.R
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int = LENGTH_LONG) {
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

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int = LENGTH_LONG
) {
    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(context.getString(it), timeLength)
        }
    })
}