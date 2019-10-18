package com.devtau.ff.ui

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class DBSubscriber {
    private val compositeDisposable = CompositeDisposable()
    fun onStop() = compositeDisposable.clear()
    fun disposeOnStop(disposable: Disposable) = compositeDisposable.add(disposable)
}