package com.devtau.ironHeroes.data.dao

import androidx.room.*
import com.devtau.ironHeroes.data.model.Hero
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface HeroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Hero?>?): Completable

    @Query("SELECT * FROM Heroes WHERE id = :id")
    fun getById(id: Long): Flowable<Hero>

    @Query("SELECT * FROM Heroes")
    fun getList(): Flowable<List<Hero>>

    @Delete
    fun delete(list: List<Hero?>?): Completable

    @Query("DELETE FROM Heroes")
    fun delete(): Completable
}