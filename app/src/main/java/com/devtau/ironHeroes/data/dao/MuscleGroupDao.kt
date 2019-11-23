package com.devtau.ironHeroes.data.dao

import androidx.room.*
import com.devtau.ironHeroes.data.model.MuscleGroup
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface MuscleGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<MuscleGroup?>): Completable

    @Query("SELECT * FROM MuscleGroups WHERE muscleGroupId = :id")
    fun getById(id: Long): Flowable<MuscleGroup>

    @Transaction
    @Query("SELECT * FROM MuscleGroups ORDER BY name")
    fun getList(): Flowable<List<MuscleGroup>>

    @Delete
    fun delete(list: List<MuscleGroup?>): Completable

    @Query("DELETE FROM MuscleGroups")
    fun delete(): Completable
}