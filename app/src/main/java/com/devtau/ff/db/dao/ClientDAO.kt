package com.devtau.ff.db.dao

import androidx.room.*
import com.devtau.ff.db.tables.ClientStored
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ClientDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg list: ClientStored): Completable

    @Query("SELECT * FROM Clients WHERE _id = :id")
    fun getById(id: Long): Flowable<ClientStored>

    @Query("SELECT * FROM Clients")
    fun getList(): Flowable<List<ClientStored>>

    @Delete
    fun delete(vararg list: ClientStored): Completable

    @Query("DELETE FROM Clients")
    fun delete(): Completable
}