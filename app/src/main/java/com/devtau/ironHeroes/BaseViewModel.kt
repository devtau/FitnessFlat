package com.devtau.ironHeroes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devtau.ironHeroes.util.Event

open class BaseViewModel: ViewModel() {

    val snackbarText = MutableLiveData<Event<Int>>()
}