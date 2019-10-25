package com.devtau.ironHeroes.ui.activities.heroDetails

import android.content.Context
import io.reactivex.functions.Action

interface HeroDetailsPresenter {
    fun onStop()
    fun restartLoaders()
    fun updateHeroData(firstName: String?, secondName: String?, phone: String?, gender: String?,
                         vkId: String?, email: String?, birthDay: String?, avatarUrl: String?, avatarId: Int?)
    fun showBirthDayDialog(context: Context, selectedBirthday: String?)
    fun onBackPressed(action: Action)
    fun deleteHero()
}