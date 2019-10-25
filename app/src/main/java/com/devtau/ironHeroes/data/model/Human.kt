package com.devtau.ironHeroes.data.model

import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.util.Constants.EMPTY_OBJECT_ID

abstract class Human(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    var id: Long = EMPTY_OBJECT_ID,
    var firstName: String,
    var secondName: String,
    var phone: String,
    var gender: String,

    var vkId: String?,
    var email: String?,
    var birthDay: String?,
    var avatarUrl: String?,
    var avatarId: Int?
) {

    fun isEmpty() = id == EMPTY_OBJECT_ID

    fun deepEquals(other: Champion?) =
        id == other?.id
                && firstName == other.firstName
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