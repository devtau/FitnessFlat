package com.devtau.ironHeroes.data.dao

import androidx.room.*
import com.devtau.ironHeroes.data.model.Champion
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ChampionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Champion?>): Completable

    @Query("SELECT * FROM Champions WHERE id = :id")
    fun getById(id: Long): Flowable<Champion>

    @Query("SELECT * FROM Champions")
    fun getList(): Flowable<List<Champion>>

    @Delete
    fun delete(list: List<Champion?>?): Completable

    @Query("DELETE FROM Champions")
    fun delete(): Completable
}