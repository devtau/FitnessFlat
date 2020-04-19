package com.devtau.ironHeroes.data.source.local.exerciseInTraining

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.source.local.BaseDao
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ExerciseInTrainingDao: BaseDao<ExerciseInTraining> {

    //<editor-fold desc="Single object operations">
    @Transaction
    @Query("SELECT * FROM ExercisesInTraining WHERE exerciseInTrainingId = :id")
    fun getByIdAsFlowable(id: Long?): Flowable<ExerciseInTrainingRelation?>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining WHERE exerciseInTrainingId = :id")
    suspend fun getById(id: Long?): ExerciseInTrainingRelation?

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining WHERE exerciseInTrainingId = :id")
    fun observeItem(id: Long?): LiveData<ExerciseInTrainingRelation?>
    //</editor-fold>


    //<editor-fold desc="Group operations">
    @Transaction
    @Query("SELECT * FROM ExercisesInTraining JOIN Trainings ON ExercisesInTraining.trainingId = Trainings.trainingId ORDER BY Trainings.date ASC")
    suspend fun getList(): List<ExerciseInTrainingRelation>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining JOIN Trainings ON ExercisesInTraining.trainingId = Trainings.trainingId ORDER BY Trainings.date ASC")
    fun getListAsFlowable(): Flowable<List<ExerciseInTrainingRelation>>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining JOIN Trainings ON ExercisesInTraining.trainingId = Trainings.trainingId ORDER BY Trainings.date ASC")
    fun observeList(): LiveData<List<ExerciseInTrainingRelation>>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining WHERE trainingId = :trainingId ORDER BY position ASC")
    suspend fun getListForTraining(trainingId: Long): List<ExerciseInTrainingRelation>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining WHERE trainingId = :trainingId ORDER BY position ASC")
    fun getListForTrainingAsFlowable(trainingId: Long): Flowable<List<ExerciseInTrainingRelation>>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining WHERE trainingId = :trainingId ORDER BY position ASC")
    fun observeListForTraining(trainingId: Long?): LiveData<List<ExerciseInTrainingRelation>>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining JOIN Trainings ON ExercisesInTraining.trainingId = Trainings.trainingId WHERE heroId = :heroId ORDER BY Trainings.date DESC")
    suspend fun getListForHero(heroId: Long?): List<ExerciseInTrainingRelation>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining JOIN Trainings ON ExercisesInTraining.trainingId = Trainings.trainingId WHERE heroId = :heroId ORDER BY Trainings.date DESC")
    fun getListForHeroAsFlowable(heroId: Long): Flowable<List<ExerciseInTrainingRelation>>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining JOIN Trainings ON ExercisesInTraining.trainingId = Trainings.trainingId WHERE heroId = :heroId ORDER BY Trainings.date DESC")
    fun observeListForHero(heroId: Long?): LiveData<List<ExerciseInTrainingRelation>>

    @Query("DELETE FROM ExercisesInTraining")
    fun deleteAsync(): Completable

    @Query("DELETE FROM ExercisesInTraining WHERE trainingId = :trainingId")
    suspend fun deleteListForTraining(trainingId: Long): Int

    @Query("DELETE FROM ExercisesInTraining")
    suspend fun delete(): Int
    //</editor-fold>
}