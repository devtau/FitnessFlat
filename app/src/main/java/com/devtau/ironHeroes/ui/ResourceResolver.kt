package com.devtau.ironHeroes.ui

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

interface ResourceResolver {
    fun resolveColor(@ColorRes colorResId: Int): Int
    fun resolveString(@StringRes stringResId: Int): String
}