package com.devtau.ironHeroes.data.dao

import androidx.room.*
import com.devtau.ironHeroes.data.model.Exercise
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Exercise?>): Completable

    @Query("SELECT * FROM Exercises WHERE id = :id")
    fun getById(id: Long): Flowable<Exercise>

    @Transaction
    @Query("SELECT * FROM Exercises")
    fun getList(): Flowable<List<Exercise>>

    @Delete
    fun delete(list: List<Exercise?>): Completable

    @Query("DELETE FROM Exercises")
    fun delete(): Completable
}