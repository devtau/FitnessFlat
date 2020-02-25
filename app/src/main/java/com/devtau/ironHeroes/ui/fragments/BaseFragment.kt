package com.devtau.ironHeroes.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.devtau.ironHeroes.IronHeroesApp
import com.devtau.ironHeroes.ViewModelFactory
import com.devtau.ironHeroes.ui.Coordinator
import com.devtau.ironHeroes.ui.CoordinatorImpl
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

abstract class BaseFragment: Fragment(), StandardView, Coordinator by CoordinatorImpl {

    private val compositeUiDisposable = CompositeDisposable()

    abstract fun getLogTag(): String
    abstract fun initActionbar(): Boolean?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionbar()
    }

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

fun FragmentManager.getCurrentNavigationFragment(): Fragment? =
    primaryNavigationFragment?.childFragmentManager?.fragments?.first()

fun FragmentActivity?.initActionBar(titleId: Int?, show: Boolean = true): Boolean {
    val actionbar = (this as AppCompatActivity?)?.supportActionBar
    if (this == null || actionbar == null) return false
    if (show) actionbar.show() else actionbar.hide()
    actionbar.title = if (titleId == null) "" else this.getString(titleId)
    return true
}

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val trainingsRepository = (requireContext().applicationContext as IronHeroesApp).trainingsRepository
    val heroesRepository = (requireContext().applicationContext as IronHeroesApp).heroesRepository
    return ViewModelFactory(trainingsRepository, heroesRepository, this)
}