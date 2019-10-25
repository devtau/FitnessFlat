package com.devtau.ff.data.dao

import androidx.room.*
import com.devtau.ff.data.model.Client
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Client?>?): Completable

    @Query("SELECT * FROM Clients WHERE id = :id")
    fun getById(id: Long): Flowable<Client>

    @Query("SELECT * FROM Clients")
    fun getList(): Flowable<List<Client>>

    @Delete
    fun delete(list: List<Client?>?): Completable

    @Query("DELETE FROM Clients")
    fun delete(): Completable
}