package com.devtau.ironHeroes.data.model.wrappers

import com.devtau.ironHeroes.enums.HumanType

data class HeroIdWithHumanType(
    val heroId: Long,
    val humanType: HumanType
)