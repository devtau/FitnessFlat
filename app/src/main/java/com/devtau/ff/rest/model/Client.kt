package com.devtau.ff.rest.model

import android.os.Parcelable
import android.text.TextUtils
import com.devtau.ff.R
import com.devtau.ff.enums.Gender
import com.devtau.ff.util.Constants.DATE_FORMATTER_TO_SHOW
import com.devtau.ff.util.Constants.EMPTY_OBJECT_ID
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Client(
    @SerializedName("id") var id: Long = EMPTY_OBJECT_ID,
    @SerializedName("first_name") var firstName: String,
    @SerializedName("second_name") var secondName: String,
    @SerializedName("phone") var phone: String,
    @SerializedName("gender") var gender: String,

    @SerializedName("vk_id") var vkId: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("birth_day") var birthDay: String? = null,
    @SerializedName("avatar_url") var avatarUrl: String? = null,
    @SerializedName("avatar_id") var avatarId: Int? = null
): Parcelable {

    fun getName(): String = when {
        firstName.isNotEmpty() && secondName.isNotEmpty() -> "$firstName $secondName"
        firstName.isNotEmpty() -> firstName
        else -> secondName
    }

    fun isEmpty() = id == EMPTY_OBJECT_ID

    fun deepEquals(other: Client?) = id == other?.id
            && firstName == other.firstName
            && secondName == other.secondName
            && phone == other.phone
            && gender == other.gender
            && vkId == other.vkId
            && email == other.email
            && birthDay == other.birthDay
            && avatarUrl == other.avatarUrl
            && avatarId == other.avatarId

    fun someFieldsChanged(firstName: String?, secondName: String?, phone: String?, gender: String?,
                          vkId: String?, email: String?, birthDay: String?,
                          avatarUrl: String?, avatarId: Int?): Boolean {
        return firstName != this.firstName
                || secondName != this.secondName
                || phone != this.phone
                || gender != this.gender
                || vkId != this.vkId
                || email != this.email
                || birthDay != this.birthDay
                || avatarUrl != this.avatarUrl
                || avatarId != this.avatarId
    }


    companion object {
        fun getMock() = listOf(
            Client(1, "Денис", "Русских", "+79219781372", Gender.MALE.code, "devtau", "d29025@yandex.ru",
                "25.09.1983", null, R.drawable.denis),
            Client(2, "Рома", "Богданов", "+79111718219", Gender.MALE.code, "romanyurievich93", "d29026@yandex.ru",
                "20.07.1993", null, R.drawable.roma),
            Client(3, "Маша", "Черничкина", "+79219781373", Gender.FEMALE.code, "iiaquamarieii", "d29027@yandex.ru",
                "26.05.1993", null, R.drawable.masha))

//        "https://yadi.sk/i/hZhu1zg73GNum4"
//        "https://drive.google.com/open?id=0BwZwHCn4b4EseUZKYVB6eW1acTQ"
//        "https://img2.freepng.ru/20180425/jrq/kisspng-youtube-avatar-clip-art-5ae0f8f81d8590.5706172815246932401209.jpg"

        fun formatBirthday(cal: Calendar): String =
            SimpleDateFormat(DATE_FORMATTER_TO_SHOW, Locale.getDefault()).format(cal.time)

        fun parseBirthday(birthDay: String?): Calendar {
            val calendar = Calendar.getInstance()
            val inputDf = SimpleDateFormat(DATE_FORMATTER_TO_SHOW, Locale.getDefault())
            try {
                calendar.timeInMillis = inputDf.parse(birthDay).time
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return calendar
        }

        fun allObligatoryPartsPresent(firstName: String?, secondName: String?, phone: String?, gender: String?): Boolean {
            return !TextUtils.isEmpty(firstName)
                    && !TextUtils.isEmpty(secondName)
                    && !TextUtils.isEmpty(phone)
                    && !TextUtils.isEmpty(gender)
        }
    }
}