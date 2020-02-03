package com.devtau.ironHeroes.util

import android.os.Handler
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

object Threading {

    fun dispatchMain(block: Action) {
        Handler(Looper.getMainLooper()).post {
            try {
                block.run()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun dispatchMainDelayed(block: Consumer<Long>, initDelayMS: Long): Disposable =
            Observable.timer(initDelayMS, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(block)

    fun dispatchMainLoopDelayed(block: Consumer<Long>, initDelayMS: Long, rerunDelayMS: Long): Disposable =
            Observable.interval(initDelayMS, rerunDelayMS, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(block)

    fun asyncDelayed(block: Consumer<Long>, initDelayMS: Long): Disposable =
            Observable.timer(initDelayMS, TimeUnit.MILLISECONDS, Schedulers.io()).subscribe(block)


    fun <T> async(task: Callable<T>, finished: Consumer<T>? = null, onError: Consumer<Throwable>? = null): Disposable =
            Single.fromCallable(task)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(finished ?: Consumer { /*NOP*/ }, onError ?: Consumer { /*NOP*/ })
}