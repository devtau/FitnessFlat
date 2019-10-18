package com.devtau.ff.enums

enum class Gender(val code: String) {
    FEMALE("f"), MALE("m");


    companion object {
        fun getByCode(code: String?): Gender = when (code) {
            FEMALE.code -> FEMALE
            MALE.code -> MALE
            else -> MALE
        }
    }
}
