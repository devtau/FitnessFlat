package com.devtau.ironHeroes.data.source.remote.listeners

import com.devtau.ironHeroes.data.model.Hero

interface HeroRegisteredListener {
    fun processHeroRegistered(token: String?, hero: Hero?)
}