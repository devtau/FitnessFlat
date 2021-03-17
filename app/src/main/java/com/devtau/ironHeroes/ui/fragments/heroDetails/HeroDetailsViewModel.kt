package com.devtau.ironHeroes.ui.fragments.heroDetails

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
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class HeroDetailsViewModel(
    private val heroesRepository: HeroesRepository,
    private var heroId: Long?,
    humanType: HumanType
): BaseViewModel() {

    val hero = MutableLiveData<Hero?>(null)


    val firstName = MutableLiveData<String>()
    val secondName = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val vkId = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val isChampion = MutableLiveData(humanType == HumanType.CHAMPION)

    val genderFemaleChecked = MutableLiveData(false)
    val genderMaleChecked = MutableLiveData(false)
    fun selectGender(gender: Gender) {
        Timber.d("selectGender. gender=$gender")
        genderFemaleChecked.value = gender == Gender.FEMALE
        genderMaleChecked.value = gender == Gender.MALE
    }


    val formattedBirthday = MutableLiveData(Constants.VALUE_NA)
    fun updateBirthday(year: Int, month: Int, dayOfMonth: Int) {
        val date = Calendar.getInstance()
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)

        formattedBirthday.value = DateUtils.formatDate(date)
    }


    val callHero = MutableLiveData<Event<String>>()
    fun callHero() {
        val phoneValue = phone.value
        if (phoneValue == null || phoneValue.isEmpty()) {
            Timber.w("callHero. phone is empty. aborting")
            snackbarText.value = Event(R.string.phone_empty)
        } else {
            callHero.value = Event(phoneValue)
        }
    }


    val openVk = MutableLiveData<Event<String>>()
    fun openVk() {
        val vkIdValue = vkId.value
        if (vkIdValue == null || vkIdValue.isEmpty()) {
            Timber.w("openVk. vkId is empty. aborting")
            snackbarText.value = Event(R.string.vk_id_empty)
        } else {
            openVk.value = Event(vkIdValue)
        }
    }


    val composeEmail = MutableLiveData<Event<String>>()
    fun composeEmail() {
        val emailValue = email.value
        if (emailValue == null || emailValue.isEmpty()) {
            Timber.w("composeEmail. email is empty. aborting")
            snackbarText.value = Event(R.string.email_empty)
        } else {
            composeEmail.value = Event(emailValue)
        }
    }


    val showBirthDayDialog = MutableLiveData<Event<DatePickerDialogDataWrapper>>()
    fun showBirthDayDialog() {
        val nowMinusCentury = Calendar.getInstance()
        nowMinusCentury.add(Calendar.YEAR, -100)
        val heroBirthDay = hero.value?.birthDay
        val birthDay = if (heroBirthDay != null) {
            val date = Calendar.getInstance()
            date.timeInMillis = heroBirthDay
            date
        } else DateUtils.parseDate(formattedBirthday.value)
        showBirthDayDialog.value = Event(DatePickerDialogDataWrapper(birthDay, nowMinusCentury, Calendar.getInstance()))
    }


    val useVkAvatar = MutableLiveData<Event<Unit>>()
    fun useVkAvatar() {
        useVkAvatar.value = Event(Unit)
    }


    val closeScreenValidated = MutableLiveData<Event<Unit>>()
    val confirmExit = MutableLiveData<Event<Int>>()
    fun tryCloseScreen() {
        if (hero.value == null) {
            confirmExit.value = Event(if (isChampion.value == true) {
                R.string.champion_not_saved
            } else {
                R.string.hero_not_saved
            })
        } else {
            closeScreenValidated.value = Event(Unit)
        }
    }


    val deleteIsVisible: LiveData<Boolean> = hero.switchMap { MutableLiveData(it != null) }
    val confirmDeleteHero = MutableLiveData<Event<Unit>>()
    fun deleteHeroRequested() {
        confirmDeleteHero.value = Event(Unit)
    }
    fun deleteHeroConfirmed() {
        viewModelScope.launch {
            hero.value?.let { heroesRepository.deleteItem(it) }
            closeScreenValidated.value = Event(Unit)
        }
    }


    fun updateHero(avatarUrl: String?, avatarId: Int?) {
        val hero = hero.value
        val gender = readGenderFromInputs()
        val humanType = readHumanTypeFromInputs()

        if (!Hero.allObligatoryPartsPresent(firstName.value, secondName.value, phone.value, gender)) {
            Timber.w("updateHero. some data missing. aborting")
            return
        }

        val birthDayLong = if (formattedBirthday.value == Constants.VALUE_NA) {
            null
        } else {
            DateUtils.parseDate(formattedBirthday.value).timeInMillis
        }
        val avatarIdLoc = avatarId ?: hero?.avatarId

        if (heroId == null || hero == null) {
            viewModelScope.launch {
                val newHero = Hero(
                    null, humanType, firstName.value!!, secondName.value!!, phone.value!!, gender!!.code, vkId.value, email.value,
                    birthDayLong, avatarUrl, avatarIdLoc
                )
                heroId = heroesRepository.saveItem(newHero)
                newHero.id = heroId
                this@HeroDetailsViewModel.hero.value = newHero
            }
            return
        }

        val someFieldsChanged = hero.someFieldsChanged(
            humanType, firstName.value, secondName.value, phone.value, gender?.code, vkId.value, email.value,
            birthDayLong, avatarUrl, avatarIdLoc
        )

        Timber.d("updateHeroData. someFieldsChanged=$someFieldsChanged, phone=${phone.value}")
        if (someFieldsChanged) {
            viewModelScope.launch {
                val updatedHero = Hero(
                    heroId, humanType, firstName.value!!, secondName.value!!, phone.value!!, gender!!.code, vkId.value, email.value,
                    birthDayLong, avatarUrl, avatarIdLoc
                )
                heroesRepository.saveItem(updatedHero)
                this@HeroDetailsViewModel.hero.value = updatedHero
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
        formattedBirthday.value = hero?.getFormattedBirthday() ?: Constants.VALUE_NA
        selectGender(hero?.getGenderAsEnum() ?: Gender.MALE)
    }


    init {
        if (heroId == null) {
            hero.value = null
            showHeroDetails(hero.value)
        } else {
            viewModelScope.launch {
                val heroResult = heroesRepository.getItem(heroId)
                if (heroResult is Result.Success && heroResult.data != null) {
                    hero.value = heroResult.data
                    showHeroDetails(hero.value)
                }
            }
        }
    }
}