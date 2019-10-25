package com.devtau.ff.data.dao

import androidx.room.*
import com.devtau.ff.data.model.Trainer
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface TrainerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Trainer?>): Completable

    @Query("SELECT * FROM Trainers WHERE id = :id")
    fun getById(id: Long): Flowable<Trainer>

    @Query("SELECT * FROM Trainers")
    fun getList(): Flowable<List<Trainer>>

    @Delete
    fun delete(list: List<Trainer?>?): Completable

    @Query("DELETE FROM Trainers")
    fun delete(): Completable
}