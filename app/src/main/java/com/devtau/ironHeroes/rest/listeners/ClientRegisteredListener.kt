package com.devtau.ironHeroes.rest.listeners

import com.devtau.ironHeroes.data.model.Hero

interface HeroRegisteredListener {
    fun processHeroRegistered(token: String?, hero: Hero?)
}