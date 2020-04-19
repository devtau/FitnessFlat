package com.devtau.ironHeroes.data.source.remote

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.source.remote.listeners.HeroRegisteredListener
import io.reactivex.functions.Consumer

interface NetworkLayer {
    fun validatePhone(phone: String)
    fun registerNewHero(hero: Hero, smsValidationCode: Int?, listener: HeroRegisteredListener)
    fun getHero(token: String, listener: Consumer<Hero?>)
    fun updateHero(hero: Hero?, token: String)
}