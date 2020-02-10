package com.devtau.ironHeroes.ui.activities.main

import com.devtau.ironHeroes.ui.StandardView

interface MainContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
    }

    interface View: StandardView {

    }
}