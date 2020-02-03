package com.devtau.ironHeroes.data.dao

import androidx.room.*
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.relations.ExerciseRelation
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<Exercise>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(exercise: Exercise?): Long

    @Query("SELECT * FROM Exercises WHERE exerciseId = :id")
    fun getById(id: Long?): Flowable<ExerciseRelation>

    @Transaction
    @Query("SELECT * FROM Exercises ORDER BY name")
    fun getList(): Flowable<List<ExerciseRelation>>

    @Delete
    fun delete(list: List<Exercise?>): Completable

    @Query("DELETE FROM Exercises")
    fun delete(): Completable
}