package com.devtau.ironHeroes.enums

import androidx.room.TypeConverter

enum class HumanType {
    HERO, CHAMPION;

    class Converter {
        @TypeConverter
        fun fromHumanType(value: HumanType): Int = value.ordinal

        @TypeConverter
        fun toHumanType(value: Int): HumanType = when (value) {
            0 -> HERO
            1 -> CHAMPION
            else -> HERO
        }
    }
}