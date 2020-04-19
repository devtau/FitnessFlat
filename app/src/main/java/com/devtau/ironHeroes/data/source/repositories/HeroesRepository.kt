package com.devtau.ironHeroes.data.source.repositories

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType

interface HeroesRepository: BaseRepository<Hero> {

    fun observeList(humanType: HumanType): LiveData<Result<List<Hero>>>
}