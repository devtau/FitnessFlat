package com.devtau.ironHeroes.data.source.local.training

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.source.local.BaseDao
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface TrainingDao: BaseDao<Training> {

    //<editor-fold desc="Single object operations">
    @Transaction
    @Query("SELECT * FROM Trainings WHERE trainingId = :id")
    fun getByIdAsFlowable(id: Long): Flowable<TrainingRelation?>

    @Transaction
    @Query("SELECT * FROM Trainings WHERE trainingId = :id")
    suspend fun getById(id: Long?): TrainingRelation?

    @Transaction
    @Query("SELECT * FROM Trainings WHERE trainingId = :id")
    fun observeItem(id: Long?): LiveData<TrainingRelation?>
    //</editor-fold>


    //<editor-fold desc="Group operations">
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