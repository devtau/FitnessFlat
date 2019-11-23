package com.devtau.ironHeroes.data.model

import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devtau.ironHeroes.BuildConfig
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.enums.Gender
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.AppUtils

@Entity(tableName = "Heroes")
class Hero(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "heroId")
    var id: Long?,
    var humanType: HumanType,
    var firstName: String,
    var secondName: String,
    var phone: String,
    var gender: String,

    var vkId: String?,
    var email: String?,
    var birthDay: Long?,
    var avatarUrl: String?,
    var avatarId: Int?
) {
    fun deepEquals(other: Hero?) =
        id == other?.id
                && humanType == other?.humanType
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
                          vkId: String?, email: String?, birthDay: Long?,
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

        fun getMockChampions(): List<Hero> {
            val romaPhone = if (BuildConfig.DEBUG) "+79111718219" else "+79210000000"
            val antonPhone = if (BuildConfig.DEBUG) "+79500400027" else "+79210000000"
            return listOf(
                Hero(1, HumanType.CHAMPION, "Рома", "Богданов", romaPhone, Gender.MALE.code, "romanyurievich93", "d29028@yandex.ru",
                    AppUtils.parseDate("20.07.1993").timeInMillis, null, R.drawable.roma),
                Hero(2, HumanType.CHAMPION, "Антон", "Щукин", antonPhone, Gender.MALE.code, "shukin2007", "d29029@yandex.ru",
                    AppUtils.parseDate("28.02.1993").timeInMillis, null, R.drawable.anton))
        }

        fun getMockHeroes(): List<Hero> {
            val denisPhone = if (BuildConfig.DEBUG) "+79219781372" else "+79210000000"
            val mashaPhone = if (BuildConfig.DEBUG) "+79210000000" else "+79210000000"
            val eugenPhone = if (BuildConfig.DEBUG) "+79210000000" else "+79210000000"
            return listOf(
                Hero(3, HumanType.HERO, "Денис", "Русских", denisPhone, Gender.MALE.code, "devtau", "d29025@yandex.ru",
                    AppUtils.parseDate("25.09.1983").timeInMillis, null, R.drawable.denis),
                Hero(4, HumanType.HERO, "Маша", "Черничкина", mashaPhone, Gender.FEMALE.code, "iiaquamarieii", "d29026@yandex.ru",
                    AppUtils.parseDate("26.05.1993").timeInMillis, null, R.drawable.masha),
                Hero(5, HumanType.HERO, "Евгений", "Стирманов", eugenPhone, Gender.MALE.code, "uginstarr", "d29027@yandex.ru",
                    AppUtils.parseDate("25.10.1993").timeInMillis, null, R.drawable.evgen))
        }

//        "https://yadi.sk/i/hZhu1zg73GNum4"
//        "https://drive.google.com/open?id=0BwZwHCn4b4EseUZKYVB6eW1acTQ"
//        "https://img2.freepng.ru/20180425/jrq/kisspng-youtube-avatar-clip-art-5ae0f8f81d8590.5706172815246932401209.jpg"
    }
}