package com.devtau.ironHeroes.data.source.repositories

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType

interface HeroesRepository {

    suspend fun saveItem(item: Hero)
    suspend fun getItem(id: Long, forceUpdate: Boolean = false): Result<Hero>
    fun observeItem(id: Long): LiveData<Result<Hero>>
    suspend fun deleteItem(id: Long)

    suspend fun fetchItemsFromBackend()
    suspend fun getList(forceUpdate: Boolean = false): Result<List<Hero>>
    fun observeList(humanType: HumanType): LiveData<Result<List<Hero>>>
    suspend fun deleteAll()
}