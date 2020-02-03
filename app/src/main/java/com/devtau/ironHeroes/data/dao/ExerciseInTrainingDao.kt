package com.devtau.ironHeroes.data.dao

import androidx.room.*
import com.devtau.ironHeroes.data.model.ExerciseInTraining
import com.devtau.ironHeroes.data.relations.ExerciseInTrainingRelation
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ExerciseInTrainingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<ExerciseInTraining?>): Completable

    @Query("SELECT * FROM ExercisesInTraining WHERE exerciseInTrainingId = :id")
    fun getById(id: Long): Flowable<ExerciseInTrainingRelation>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining WHERE trainingId = :trainingId ORDER BY position ASC")
    fun getList(trainingId: Long): Flowable<List<ExerciseInTrainingRelation>>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining JOIN Trainings ON ExercisesInTraining.trainingId = Trainings.trainingId WHERE heroId = :heroId AND Trainings.date < :maxDate ORDER BY Trainings.date DESC")
    fun getListForHeroDesc(heroId: Long, maxDate: Long): Flowable<List<ExerciseInTrainingRelation>>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining JOIN Trainings ON ExercisesInTraining.trainingId = Trainings.trainingId WHERE heroId = :heroId AND Trainings.date < :maxDate ORDER BY Trainings.date ASC")
    fun getListForHeroAsc(heroId: Long, maxDate: Long): Flowable<List<ExerciseInTrainingRelation>>

    @Transaction
    @Query("SELECT * FROM ExercisesInTraining JOIN Trainings ON ExercisesInTraining.trainingId = Trainings.trainingId WHERE heroId = :heroId ORDER BY Trainings.date ASC")
    fun getListAsc(heroId: Long): Flowable<List<ExerciseInTrainingRelation>>

    @Delete
    fun delete(list: List<ExerciseInTraining?>): Completable

    @Query("DELETE FROM ExercisesInTraining")
    fun delete(): Completable
}