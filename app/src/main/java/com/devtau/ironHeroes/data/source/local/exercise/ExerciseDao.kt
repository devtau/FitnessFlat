package com.devtau.ironHeroes.data.source.local.exercise

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.devtau.ironHeroes.data.model.Exercise
import com.devtau.ironHeroes.data.source.local.BaseDao
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ExerciseDao: BaseDao<Exercise> {

    //<editor-fold desc="Single object operations">
    @Transaction
    @Query("SELECT * FROM Exercises WHERE exerciseId = :id")
    fun getByIdAsFlowable(id: Long?): Flowable<ExerciseRelation?>

    @Transaction
    @Query("SELECT * FROM Exercises WHERE exerciseId = :id")
    suspend fun getById(id: Long?): ExerciseRelation?

    @Transaction
    @Query("SELECT * FROM Exercises WHERE exerciseId = :id")
    fun observeItem(id: Long?): LiveData<ExerciseRelation?>
    //</editor-fold>


    //<editor-fold desc="Group operations">
    @Transaction
    @Query("SELECT * FROM Exercises ORDER BY name")
    suspend fun getList(): List<ExerciseRelation>

    @Transaction
    @Query("SELECT * FROM Exercises ORDER BY name")
    fun getListAsFlowable(): Flowable<List<ExerciseRelation>>

    @Transaction
    @Query("SELECT * FROM Exercises ORDER BY name")
    fun observeList(): LiveData<List<ExerciseRelation>>

    @Query("DELETE FROM Exercises")
    fun deleteAsync(): Completable

    @Query("DELETE FROM Exercises")
    suspend fun delete(): Int
    //</editor-fold>
}