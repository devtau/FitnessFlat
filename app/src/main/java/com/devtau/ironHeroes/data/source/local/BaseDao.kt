package com.devtau.ironHeroes.data.source.local

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import io.reactivex.Completable

interface BaseDao<T> {

    //<editor-fold desc="Single object operations">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNow(item: T?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: T?): Long

    @Delete
    fun deleteAsync(item: T?): Completable

    @Delete
    suspend fun delete(item: T?): Int
    //</editor-fold>


    //<editor-fold desc="Group operations">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListAsync(list: List<T?>?): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<T?>?)

    @Delete
    fun deleteAsync(list: List<T>): Completable
    //</editor-fold>
}