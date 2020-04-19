package com.devtau.ironHeroes.ui.fragments.heroDetails

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.devtau.ironHeroes.BaseViewModel
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.wrappers.DatePickerDialogDataWrapper
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.enums.Gender
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.DateUtils
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.Logger
import kotlinx.coroutines.launch
import java.util.*

class HeroDetailsViewModel(
    private val heroesRepository: HeroesRepository,
    private var heroId: Long?,
    humanType: HumanType
): BaseViewModel() {


    private val _hero = MutableLiveData<Hero?>(null)
    val hero: LiveData<Hero?> = _hero


    val firstName: MutableLiveData<String> = MutableLiveData()
    val secondName: MutableLiveData<String> = MutableLiveData()
    val phone: MutableLiveData<String> = MutableLiveData()
    val vkId: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val isChampion: MutableLiveData<Boolean> = MutableLiveData(humanType == HumanType.CHAMPION)


    private val _genderFemaleChecked = MutableLiveData(false)
    val genderFemaleChecked: LiveData<Boolean> = _genderFemaleChecked
    private val _genderMaleChecked = MutableLiveData(false)
    val genderMaleChecked: LiveData<Boolean> = _genderMaleChecked
    fun selectGender(gender: Gender) {
        Logger.d(LOG_TAG, "selectGender. gender=$gender")
        _genderFemaleChecked.value = gender == Gender.FEMALE
        _genderMaleChecked.value = gender == Gender.MALE
    }


    private val _formattedBirthday = MutableLiveData(Constants.VALUE_NA)
    val formattedBirthday: LiveData<String> = _formattedBirthday
    fun updateBirthday(year: Int, month: Int, dayOfMonth: Int) {
        val date = Calendar.getInstance()
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)

        _formattedBirthday.value = DateUtils.formatDate(date)
    }

    private val _toolbarTitle = isChampion.switchMap {
        MutableLiveData(Event(if (it) {
            if (heroId == null) R.string.champion_add else R.string.champion_edit
        } else {
            if (heroId == null) R.string.hero_add else R.string.hero_edit
        }))
    }
    val toolbarTitle: LiveData<Event<Int>> = _toolbarTitle


    private val _callHero = MutableLiveData<Event<String>>()
    val callHero: LiveData<Event<String>> = _callHero
    fun callHero() {
        val phoneValue = phone.value
        if (phoneValue == null || phoneValue.isEmpty()) {
            Logger.w(LOG_TAG, "callHero. phone is empty. aborting")
            _snackbarText.value = Event(R.string.phone_empty)
        } else {
            _callHero.value = Event(phoneValue)
        }
    }


    private val _openVk = MutableLiveData<Event<String>>()
    val openVk: LiveData<Event<String>> = _openVk
    fun openVk() {
        val vkIdValue = vkId.value
        if (vkIdValue == null || vkIdValue.isEmpty()) {
            Logger.w(LOG_TAG, "openVk. vkId is empty. aborting")
            _snackbarText.value = Event(R.string.vk_id_empty)
        } else {
            _openVk.value = Event(vkIdValue)
        }
    }


    private val _composeEmail = MutableLiveData<Event<String>>()
    val composeEmail: LiveData<Event<String>> = _composeEmail
    fun composeEmail() {
        val emailValue = email.value
        if (emailValue == null || emailValue.isEmpty()) {
            Logger.w(LOG_TAG, "composeEmail. email is empty. aborting")
            _snackbarText.value = Event(R.string.email_empty)
        } else {
            _composeEmail.value = Event(emailValue)
        }
    }


    private val _showBirthDayDialog = MutableLiveData<Event<DatePickerDialogDataWrapper>>()
    val showBirthDayDialog: LiveData<Event<DatePickerDialogDataWrapper>> = _showBirthDayDialog
    fun showBirthDayDialog() {
        val nowMinusCentury = Calendar.getInstance()
        nowMinusCentury.add(Calendar.YEAR, -100)
        val heroBirthDay = hero.value?.birthDay
        val birthDay = if (heroBirthDay != null) {
            val date = Calendar.getInstance()
            date.timeInMillis = heroBirthDay
            date
        } else DateUtils.parseDate(formattedBirthday.value)
        _showBirthDayDialog.value = Event(DatePickerDialogDataWrapper(birthDay, nowMinusCentury, Calendar.getInstance()))
    }


    private val _useVkAvatar = MutableLiveData<Event<Unit>>()
    val useVkAvatar: LiveData<Event<Unit>> = _useVkAvatar
    fun useVkAvatar() {
        _useVkAvatar.value = Event(Unit)
    }


    private val _closeScreenValidated = MutableLiveData<Event<Unit>>()
    val closeScreenValidated: LiveData<Event<Unit>> = _closeScreenValidated
    private val _confirmExit = MutableLiveData<Event<Int>>()
    val confirmExit: LiveData<Event<Int>> = _confirmExit
    fun tryCloseScreen() {
        if (hero.value == null) {
            val msgId = if (isChampion.value == true) R.string.champion_not_saved else R.string.hero_not_saved
            _confirmExit.value = Event(msgId)
        } else {
            _closeScreenValidated.value = Event(Unit)
        }
    }


    val deleteVisibility: LiveData<Int> = _hero.switchMap {
        MutableLiveData(if (it == null) View.INVISIBLE else View.VISIBLE)
    }
    private val _confirmDeleteHero = MutableLiveData<Event<Unit>>()
    val confirmDeleteHero: LiveData<Event<Unit>> = _confirmDeleteHero
    fun deleteHeroRequested() {
        _confirmDeleteHero.value = Event(Unit)
    }
    fun deleteHeroConfirmed() {
        viewModelScope.launch {
            hero.value?.let { heroesRepository.deleteItem(it) }
            _closeScreenValidated.value = Event(Unit)
        }
    }


    fun updateHero(avatarUrl: String?, avatarId: Int?) {
        val hero = _hero.value
        val gender = readGenderFromInputs()
        val humanType = readHumanTypeFromInputs()

        if (!Hero.allObligatoryPartsPresent(firstName.value, secondName.value, phone.value, gender)) {
            Logger.w(LOG_TAG, "updateHero. some data missing. aborting")
            return
        }

        val birthDayLong = if (_formattedBirthday.value == Constants.VALUE_NA) null else DateUtils.parseDate(_formattedBirthday.value).timeInMillis
        val avatarIdLoc = avatarId ?: hero?.avatarId

        if (heroId == null || hero == null) {
            viewModelScope.launch {
                val newHero = Hero(
                    null, humanType, firstName.value!!, secondName.value!!, phone.value!!, gender!!.code, vkId.value, email.value,
                    birthDayLong, avatarUrl, avatarIdLoc
                )
                heroId = heroesRepository.saveItem(newHero)
                newHero.id = heroId
                _hero.value = newHero
            }
            return
        }

        val someFieldsChanged = hero.someFieldsChanged(
            humanType, firstName.value, secondName.value, phone.value, gender?.code, vkId.value, email.value,
            birthDayLong, avatarUrl, avatarIdLoc
        )

        Logger.d(LOG_TAG, "updateHeroData. someFieldsChanged=$someFieldsChanged, phone=${phone.value}")
        if (someFieldsChanged) {
            viewModelScope.launch {
                val updatedHero = Hero(
                    heroId, humanType, firstName.value!!, secondName.value!!, phone.value!!, gender!!.code, vkId.value, email.value,
                    birthDayLong, avatarUrl, avatarIdLoc
                )
                heroesRepository.saveItem(updatedHero)
                _hero.value = updatedHero
            }
        }
    }

    private fun readGenderFromInputs(): Gender? = when {
        genderFemaleChecked.value == true -> Gender.FEMALE
        genderMaleChecked.value == true -> Gender.MALE
        else -> null
    }

    private fun readHumanTypeFromInputs(): HumanType =
        if (isChampion.value == true) HumanType.CHAMPION
        else HumanType.HERO

    private fun showHeroDetails(hero: Hero?) {
        firstName.value = hero?.firstName ?: ""
        secondName.value = hero?.secondName ?: ""
        phone.value = hero?.phone ?: ""
        vkId.value = hero?.vkId ?: ""
        email.value = hero?.email ?: ""
        isChampion.value = hero?.humanType == HumanType.CHAMPION
        _formattedBirthday.value = hero?.getFormattedBirthday() ?: Constants.VALUE_NA
        selectGender(hero?.getGenderAsEnum() ?: Gender.MALE)
    }


    init {
        if (heroId == null) {
            _hero.value = null
            showHeroDetails(_hero.value)
        } else {
            viewModelScope.launch {
                val heroResult = heroesRepository.getItem(heroId)
                if (heroResult is Result.Success && heroResult.data != null) {
                    _hero.value = heroResult.data
                    showHeroDetails(_hero.value)
                }
            }
        }
    }


    companion object {
        private const val LOG_TAG = "HeroDetailsViewModel"
    }
}