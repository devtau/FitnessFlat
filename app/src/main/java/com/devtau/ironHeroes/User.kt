package com.devtau.ironHeroes

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class User: BaseObservable() {
    @get:Bindable
    var firstName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstName)
        }

    @get:Bindable
    var lastName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastName)
        }
}