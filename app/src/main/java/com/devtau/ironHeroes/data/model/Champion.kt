package com.devtau.ironHeroes.data.model

import androidx.room.Entity
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.enums.Gender

@Entity(tableName = "Champions")
class Champion(
    id: Long?, firstName: String, secondName: String, phone: String, gender: String,
    vkId: String? = null, email: String? = null, birthDay: String? = null, avatarUrl: String? = null, avatarId: Int? = null
): Human(id, firstName, secondName, phone, gender, vkId, email, birthDay, avatarUrl, avatarId) {


    companion object {
        fun getMock() = listOf(
            Champion(1, "Рома", "Богданов", "+79111718219", Gender.MALE.code, "romanyurievich93", "d29028@yandex.ru",
                "20.07.1993", null, R.drawable.roma),
            Champion(2, "Антон", "Щукин", "+79500400027", Gender.MALE.code, "shukin2007", "d29029@yandex.ru",
                "28.02.1993", null, R.drawable.anton))
    }
}