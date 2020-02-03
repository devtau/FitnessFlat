package com.devtau.ironHeroes.ui.activities.heroDetails

import android.content.Context
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.StandardView
import io.reactivex.functions.Action
import java.util.*

interface HeroDetailsContract {
    interface Presenter {
        fun onStop()
        fun restartLoaders()
        fun updateHeroData(humanType: HumanType, firstName: String?, secondName: String?, phone: String?, gender: String?,
                           vkId: String?, email: String?, birthDay: String?, avatarUrl: String?, avatarId: Int?)
        fun showBirthDayDialog(context: Context, selectedBirthday: String?)
        fun onBackPressed(action: Action)
        fun deleteHero()
    }

    interface View: StandardView {
        fun showScreenTitle(newHero: Boolean, humanType: HumanType)
        fun showBirthdayNA()
        fun showHeroDetails(hero: Hero?)
        fun onDateSet(date: Calendar)
        fun showDeleteHeroBtn(show: Boolean)
        fun showHumanType(humanType: HumanType)
        fun closeScreen()
    }
}