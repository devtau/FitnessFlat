package com.devtau.ironHeroes.data.source.local.hero

import androidx.lifecycle.LiveData
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType

/**
 * Main entry point for accessing heroes data.
 */
interface HeroesLocalDataSource {

    //<editor-fold desc="Single object operations">
    suspend fun saveItem(item: Hero): Long
    suspend fun getItem(id: Long): Result<Hero>
    fun observeItem(id: Long): LiveData<Result<Hero>>
    suspend fun deleteItem(id: Long): Int
    //</editor-fold>


    //<editor-fold desc="Group operations">
    suspend fun saveList(list: List<Hero>)
    suspend fun getList(humanType: HumanType? = null): Result<List<Hero>>
    fun observeList(humanType: HumanType? = null): LiveData<Result<List<Hero>>>
    suspend fun deleteAll(): Int
    //</editor-fold>
}