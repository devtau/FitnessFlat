package com.devtau.ironHeroes.data.dao

import androidx.room.*
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.relations.TrainingRelation
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface TrainingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Training?>): Completable

    @Query("SELECT * FROM Trainings WHERE id = :id")
    fun getById(id: Long): Flowable<TrainingRelation>

    @Transaction
    @Query("SELECT * FROM Trainings ORDER BY date")
    fun getList(): Flowable<List<TrainingRelation>>

    @Delete
    fun delete(list: List<Training?>): Completable

    @Query("DELETE FROM Trainings")
    fun delete(): Completable
}