package com.devtau.ff.data.dao

import androidx.room.*
import com.devtau.ff.data.model.Training
import com.devtau.ff.data.relations.TrainingRelation
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface TrainingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Training?>): Completable

    @Query("SELECT * FROM Trainings WHERE _id = :id")
    fun getById(id: Long): Flowable<Training>

    @Transaction
    @Query("SELECT * FROM Trainings")
    fun getList(): Flowable<List<TrainingRelation>>

    @Delete
    fun delete(list: List<Training?>): Completable

    @Query("DELETE FROM Trainings")
    fun delete(): Completable
}