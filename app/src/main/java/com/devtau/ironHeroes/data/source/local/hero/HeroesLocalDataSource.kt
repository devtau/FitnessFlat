package com.devtau.ironHeroes.data.source.local.hero

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.source.local.BaseLocalDataSource
import com.devtau.ironHeroes.enums.HumanType

interface HeroesLocalDataSource: BaseLocalDataSource<Hero> {

    suspend fun getList(humanType: HumanType? = null): Result<List<Hero>>
    fun observeList(humanType: HumanType? = null): LiveData<Result<List<Hero>>>
}