package com.devtau.ironHeroes.ui

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class DBSubscriber {
    private val compositeDisposable = CompositeDisposable()
    open fun onStop() = compositeDisposable.clear()
    fun disposeOnStop(disposable: Disposable?) {
        disposable?.let { compositeDisposable.add(disposable) }
    }
}