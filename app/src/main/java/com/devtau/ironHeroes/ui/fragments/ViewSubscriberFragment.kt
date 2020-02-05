package com.devtau.ironHeroes.ui.fragments

import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devtau.ironHeroes.ui.StandardView
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Constants.CLICKS_DEBOUNCE_RATE_MS
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

abstract class ViewSubscriberFragment: Fragment(), StandardView {

    private val compositeUiDisposable = CompositeDisposable()

    abstract fun getLogTag(): String

    override fun onStop() {
        compositeUiDisposable.clear()
        super.onStop()
    }

    override fun showMsg(msgId: Int, confirmedListener: Action?, cancelledListener: Action?)
            = showMsg(getString(msgId), confirmedListener)
    override fun showMsg(msg: String, confirmedListener: Action?, cancelledListener: Action?)
            = AppUtils.alertD(getLogTag(), msg, context, confirmedListener)
    override fun resolveString(@StringRes stringId: Int): String = getString(stringId)
    override fun resolveColor(@ColorRes colorId: Int): Int {
        val context = context
        return if (context != null) ContextCompat.getColor(context, colorId)
        else 0
    }
    override fun isOnline(): Boolean = AppUtils.checkConnection(context)

    //использование этого слоя не обязательно, но subscribeField следует вызывать в onStart, если слой нужен
    fun subscribeField(field: EditText?, onNext: Consumer<String>) {
        field ?: return
        compositeUiDisposable.add(RxTextView.textChanges(field)
            .debounce(CLICKS_DEBOUNCE_RATE_MS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map(CharSequence::toString)
            .skip(1)
            .subscribe(onNext))
    }

    fun subscribeField(spinner: Spinner?, onNext: Consumer<Int>) {
        spinner ?: return
        compositeUiDisposable.add(RxAdapterView.itemSelections(spinner)
            .subscribeOn(AndroidSchedulers.mainThread())
            .skip(1)
            .subscribe(onNext))
    }
}