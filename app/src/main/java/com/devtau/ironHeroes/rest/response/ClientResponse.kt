package com.devtau.ironHeroes.rest.response

import com.devtau.ironHeroes.data.model.Hero

class HeroResponse: BaseResponse() {

    val hero: Hero? get() = data?.hero
    private val data: Data? = null

    private class Data(val hero: Hero? = null)
}