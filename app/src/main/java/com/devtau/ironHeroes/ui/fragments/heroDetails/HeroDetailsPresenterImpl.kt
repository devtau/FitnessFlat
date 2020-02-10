package com.devtau.ironHeroes.ui.fragments.heroDetails

import android.app.DatePickerDialog
import android.content.Context
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.dao.HeroDao
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.subscribeDefault
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.ui.DBSubscriber
import com.devtau.ironHeroes.util.AppUtils
import com.devtau.ironHeroes.util.Logger
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.*

class HeroDetailsPresenterImpl(
    private val view: HeroDetailsContract.View,
    private val heroDao: HeroDao,
    private val heroId: Long?,
    private val humanType: HumanType
): DBSubscriber(), HeroDetailsContract.Presenter {

    private var hero: Hero? = null


    //<editor-fold desc="Interface overrides">
    override fun restartLoaders() {
        if (heroId == null) {
            view.showScreenTitle(true, humanType)
            view.showBirthdayNA()
            view.showDeleteHeroBtn(false)
            view.showHumanType(humanType)
        } else {
            var disposable: Disposable? = null
            disposable = heroDao.getById(heroId)
                .subscribeDefault(Consumer {
                    hero = it
                    val humanType = hero?.humanType ?: HumanType.HERO
                    view.showHeroDetails(hero)
                    view.showScreenTitle(hero == null, humanType)
                    view.showDeleteHeroBtn(hero != null)
                    view.showHumanType(humanType)
                    disposable?.dispose()
                }, "heroDao.getById")
        }
    }

    override fun updateHeroData(humanType: HumanType, firstName: String?, secondName: String?, phone: String?, gender: String?,
                                vkId: String?, email: String?, birthDay: String?,
                                avatarUrl: String?, avatarId: Int?) {
        val allPartsPresent = Hero.allObligatoryPartsPresent(firstName, secondName, phone, gender)
        val someFieldsChanged = hero?.someFieldsChanged(firstName, secondName, phone, gender, vkId, email,
            AppUtils.parseDate(birthDay).timeInMillis, avatarUrl, avatarId) ?: true
        Logger.d(LOG_TAG, "updateHeroData. allPartsPresent=$allPartsPresent, someFieldsChanged=$someFieldsChanged")
        if (allPartsPresent && someFieldsChanged) {
            hero = Hero(heroId, humanType, firstName!!, secondName!!, phone!!, gender!!, vkId, email,
                AppUtils.parseDate(birthDay).timeInMillis, avatarUrl, avatarId ?: hero?.avatarId)
            heroDao.insert(listOf(hero))
                .subscribeDefault("heroDao.insert")
        }
    }

    override fun showBirthDayDialog(context: Context?, selectedBirthday: String?) {
        context ?: return
        val nowMinusCentury = Calendar.getInstance()
        nowMinusCentury.add(Calendar.YEAR, -100)
        val heroBirthDay = hero?.birthDay
        val birthDay = if (heroBirthDay != null) {
            val date = Calendar.getInstance()
            date.timeInMillis = heroBirthDay
            date
        } else AppUtils.parseDate(selectedBirthday)

        val dialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> onDateSet(year, month, dayOfMonth) },
            birthDay.get(Calendar.YEAR), birthDay.get(Calendar.MONTH), birthDay.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = nowMinusCentury.timeInMillis
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    override fun onBackPressed(action: Action) {
        if (hero == null) {
            val msg = when (humanType) {
                HumanType.HERO -> R.string.hero_not_saved
                HumanType.CHAMPION -> R.string.champion_not_saved
            }
            view.showMsg(msg, action)
        } else {
            action.run()
        }
    }

    override fun deleteHero() {
        view.showMsg(R.string.confirm_delete, Action {
            heroDao.delete(listOf(hero))
                .subscribeDefault("heroDao.delete")
            view.closeScreen()
        })
    }
    //</editor-fold>


    private fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val date = Calendar.getInstance()
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)

        view.onDateSet(date)
    }


    companion object {
        private const val LOG_TAG = "HeroDetailsPresenter"
    }
}