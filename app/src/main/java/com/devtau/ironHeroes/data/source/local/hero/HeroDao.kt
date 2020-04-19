package com.devtau.ironHeroes.data.source.local.hero

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.source.local.BaseDao
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface HeroDao: BaseDao<Hero> {

    //<editor-fold desc="Single object operations">
    @Query("SELECT * FROM Heroes WHERE heroId = :id")
    fun getByIdAsFlowable(id: Long): Flowable<Hero>

    @Query("SELECT * FROM Heroes WHERE heroId = :id")
    suspend fun getById(id: Long?): Hero?

    @Transaction
    @Query("SELECT * FROM Heroes WHERE heroId = :id")
    fun observeItem(id: Long?): LiveData<Hero?>
    //</editor-fold>


    //<editor-fold desc="Group operations">
    @Query("SELECT * FROM Heroes WHERE humanType = :humanTypeCode ORDER BY firstName")
    fun getListAsFlowable(humanTypeCode: Int): Flowable<List<Hero>>

    @Transaction
    @Query("SELECT * FROM Heroes WHERE humanType = :humanTypeCode ORDER BY firstName")
    suspend fun getList(humanTypeCode: Int): List<Hero>

    @Transaction
    @Query("SELECT * FROM Heroes ORDER BY firstName")
    suspend fun getList(): List<Hero>

    @Query("SELECT * FROM Heroes WHERE humanType = :humanTypeCode ORDER BY firstName")
    fun observeList(humanTypeCode: Int): LiveData<List<Hero>>

    @Query("SELECT * FROM Heroes ORDER BY firstName")
    fun observeList(): LiveData<List<Hero>>

    @Query("DELETE FROM Heroes")
    fun deleteAsync(): Completable

    @Query("DELETE FROM Heroes")
    suspend fun delete(): Int
    //</editor-fold>
}