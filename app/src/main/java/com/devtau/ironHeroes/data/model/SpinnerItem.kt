package com.devtau.ironHeroes.data.model

interface SpinnerItem {
    var id: Long?
    fun getFormattedName(): String
}