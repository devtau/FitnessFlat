package com.devtau.ironHeroes.data.model

import android.text.TextUtils

abstract class Human(
    id: Long?,
    var firstName: String,
    var secondName: String,
    var phone: String,
    var gender: String,

    var vkId: String?,
    var email: String?,
    var birthDay: String?,
    var avatarUrl: String?,
    var avatarId: Int?
): DataObject(id) {

    fun deepEquals(other: Champion?) =
        id == other?.id
                && firstName == other?.firstName
                && secondName == other.secondName
                && phone == other.phone
                && gender == other.gender
                && vkId == other.vkId
                && email == other.email
                && birthDay == other.birthDay
                && avatarUrl == other.avatarUrl
                && avatarId == other.avatarId

    fun getName(): String = when {
        firstName.isNotEmpty() && secondName.isNotEmpty() -> "$firstName $secondName"
        firstName.isNotEmpty() -> firstName
        else -> secondName
    }

    fun someFieldsChanged(firstName: String?, secondName: String?, phone: String?, gender: String?,
                          vkId: String?, email: String?, birthDay: String?,
                          avatarUrl: String?, avatarId: Int?) =
        firstName != this.firstName
                || secondName != this.secondName
                || phone != this.phone
                || gender != this.gender
                || vkId != this.vkId
                || email != this.email
                || birthDay != this.birthDay
                || avatarUrl != this.avatarUrl
                || avatarId != this.avatarId


    companion object {
        fun allObligatoryPartsPresent(firstName: String?, secondName: String?, phone: String?, gender: String?) =
            !TextUtils.isEmpty(firstName)
                    && !TextUtils.isEmpty(secondName)
                    && !TextUtils.isEmpty(phone)
                    && !TextUtils.isEmpty(gender)
    }
}