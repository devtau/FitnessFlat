package com.devtau.ironHeroes.data.source.local.hero

import androidx.lifecycle.LiveData
import androidx.room.*
import com.devtau.ironHeroes.data.model.Hero
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface HeroDao {

    //<editor-fold desc="Single object operations">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNow(training: Hero): Long

    @Query("SELECT * FROM Heroes WHERE heroId = :id")
    fun getByIdAsFlowable(id: Long): Flowable<Hero>

    @Query("SELECT * FROM Heroes WHERE heroId = :id")
    fun getById(id: Long): Hero?

    @Transaction
    @Query("SELECT * FROM Heroes WHERE heroId = :id")
    fun observeItem(id: Long): LiveData<Hero>

    @Query("DELETE FROM Heroes WHERE heroId = :id")
    suspend fun delete(id: Long): Int

    @Query("DELETE FROM Heroes WHERE heroId = :id")
    fun deleteAsync(id: Long): Completable
    //</editor-fold>


    //<editor-fold desc="Group operations">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListAsync(list: List<Hero?>?): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<Hero>)

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
    suspend fun delete(): Int

    @Query("DELETE FROM Heroes")
    fun deleteAsync(): Completable
    //</editor-fold>
}