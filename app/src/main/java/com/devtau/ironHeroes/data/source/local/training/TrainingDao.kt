package com.devtau.ironHeroes.data.source.local.training

import androidx.lifecycle.LiveData
import androidx.room.*
import com.devtau.ironHeroes.data.model.Training
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface TrainingDao {

    //<editor-fold desc="Single object operations">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNow(training: Training?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: Training): Long

    @Transaction
    @Query("SELECT * FROM Trainings WHERE trainingId = :id")
    fun getByIdAsFlowable(id: Long): Flowable<TrainingRelation?>

    @Transaction
    @Query("SELECT * FROM Trainings WHERE trainingId = :id")
    fun getById(id: Long): TrainingRelation?

    @Transaction
    @Query("SELECT * FROM Trainings WHERE trainingId = :id")
    fun observeItem(id: Long): LiveData<TrainingRelation>

    @Query("DELETE FROM Trainings WHERE trainingId = :id")
    fun deleteAsync(id: Long): Completable

    @Query("DELETE FROM Trainings WHERE trainingId = :id")
    suspend fun delete(id: Long): Int
    //</editor-fold>


    //<editor-fold desc="Group operations">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListAsync(list: List<Training?>?): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<Training>)

    @Transaction
    @Query("SELECT * FROM Trainings ORDER BY date DESC")
    suspend fun getList(): List<TrainingRelation>

    @Transaction
    @Query("SELECT * FROM Trainings ORDER BY date DESC")
    fun getListAsFlowable(): Flowable<List<TrainingRelation>>

    @Transaction
    @Query("SELECT * FROM Trainings ORDER BY date DESC")
    fun observeList(): LiveData<List<TrainingRelation>>

    @Query("DELETE FROM Trainings")
    fun deleteAsync(): Completable

    @Query("DELETE FROM Trainings")
    suspend fun delete(): Int
    //</editor-fold>
}