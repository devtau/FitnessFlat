package com.devtau.ironHeroes.data.source.local.muscleGroup

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.devtau.ironHeroes.data.model.MuscleGroup
import com.devtau.ironHeroes.data.source.local.BaseDao
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface MuscleGroupDao: BaseDao<MuscleGroup> {

    //<editor-fold desc="Single object operations">
    @Query("SELECT * FROM MuscleGroups WHERE muscleGroupId = :id")
    fun getByIdAsFlowable(id: Long): Flowable<MuscleGroup>

    @Query("SELECT * FROM MuscleGroups WHERE muscleGroupId = :id")
    suspend fun getById(id: Long?): MuscleGroup?

    @Query("SELECT * FROM MuscleGroups WHERE muscleGroupId = :id")
    fun observeItem(id: Long?): LiveData<MuscleGroup?>
    //</editor-fold>


    //<editor-fold desc="Group operations">
    @Transaction
    @Query(queryAll)
    suspend fun getList(): List<MuscleGroup>

    @Transaction
    @Query(queryAll)
    fun getListAsFlowable(): Flowable<List<MuscleGroup>>

    @Transaction
    @Query(queryAll)
    fun observeList(): LiveData<List<MuscleGroup>>

    @Query("DELETE FROM MuscleGroups")
    fun deleteAsync(): Completable

    @Query("DELETE FROM MuscleGroups")
    suspend fun delete(): Int
    //</editor-fold>


    companion object {
        private const val queryAll = "SELECT * FROM MuscleGroups ORDER BY name"
    }
}