package com.devtau.ironHeroes.data.model

import androidx.room.Entity
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.enums.Gender

@Entity(tableName = "Heroes")
class Hero(
    id: Long, firstName: String, secondName: String, phone: String, gender: String,
    vkId: String? = null, email: String? = null, birthDay: String? = null, avatarUrl: String? = null, avatarId: Int? = null
): Human(id, firstName, secondName, phone, gender, vkId, email, birthDay, avatarUrl, avatarId) {


    companion object {
        fun getMock() = listOf(
            Hero(1, "Денис", "Русских", "+79219781372", Gender.MALE.code, "devtau", "d29025@yandex.ru",
                "25.09.1983", null, R.drawable.denis),
            Hero(2, "Маша", "Черничкина", "+79219781373", Gender.FEMALE.code, "iiaquamarieii", "d29026@yandex.ru",
                "26.05.1993", null, R.drawable.masha),
            Hero(3, "Евгений", "Стирманов", "+79210000000", Gender.MALE.code, "uginstarr", "d29027@yandex.ru",
                "25.10.1993", null, R.drawable.evgen))

//        "https://yadi.sk/i/hZhu1zg73GNum4"
//        "https://drive.google.com/open?id=0BwZwHCn4b4EseUZKYVB6eW1acTQ"
//        "https://img2.freepng.ru/20180425/jrq/kisspng-youtube-avatar-clip-art-5ae0f8f81d8590.5706172815246932401209.jpg"
    }
}