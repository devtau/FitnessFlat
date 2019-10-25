package com.devtau.ironHeroes.rest

import com.devtau.ironHeroes.rest.listeners.HeroRegisteredListener
import com.devtau.ironHeroes.data.model.Hero
import io.reactivex.functions.Consumer

interface NetworkLayer {
    fun validatePhone(phone: String)
    fun registerNewHero(hero: Hero, smsValidationCode: Int?, listener: HeroRegisteredListener)
    fun getHero(token: String?, listener: Consumer<Hero?>)
    fun updateHero(hero: Hero?, token: String?)
}