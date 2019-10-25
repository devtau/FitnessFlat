package com.devtau.ironHeroes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import com.devtau.ironHeroes.util.Constants.CLICKS_DEBOUNCE_RATE_MS
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

abstract class ViewSubscriberActivity: AppCompatActivity() {

    private val compositeUiDisposable = CompositeDisposable()


    public override fun onStop() {
        compositeUiDisposable.clear()
        super.onStop()
    }


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
}