package com.devtau.ironHeroes

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.devtau.ironHeroes.data.source.local.DB
import com.devtau.ironHeroes.data.source.local.exercise.ExerciseLocalDataSource
import com.devtau.ironHeroes.data.source.local.exercise.ExerciseLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.exerciseInTraining.ExerciseInTrainingLocalDataSource
import com.devtau.ironHeroes.data.source.local.exerciseInTraining.ExerciseInTrainingLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSource
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.muscleGroup.MuscleGroupLocalDataSource
import com.devtau.ironHeroes.data.source.local.muscleGroup.MuscleGroupLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSource
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.remote.HeroesRemoteDataSource
import com.devtau.ironHeroes.data.source.remote.TrainingsRemoteDataSource
import com.devtau.ironHeroes.data.source.repositories.*
import kotlinx.coroutines.runBlocking
/**
 * A Service Locator for repositories. This is the prod version, with a the "real" RemoteDataSources.
 */
object ServiceLocator {

    private val lock = Any()
    private var database: DB? = null

    @Volatile var trainingsRepository: TrainingsRepository? = null
        @VisibleForTesting set

    @Volatile var heroesRepository: HeroesRepository? = null
        @VisibleForTesting set

    @Volatile var exercisesInTrainingsRepository: ExercisesInTrainingsRepository? = null
        @VisibleForTesting set

    @Volatile var exercisesRepository: ExercisesRepository? = null
        @VisibleForTesting set

    @Volatile var muscleGroupsRepository: MuscleGroupsRepository? = null
        @VisibleForTesting set


    fun provideTrainingsRepository(context: Context): TrainingsRepository = synchronized(this) {
        return trainingsRepository ?: createTrainingsRepository(context)
    }

    fun provideHeroesRepository(context: Context): HeroesRepository = synchronized(this) {
        return heroesRepository ?: createHeroesRepository(context)
    }

    fun provideExercisesInTrainingsRepository(context: Context): ExercisesInTrainingsRepository = synchronized(this) {
        return exercisesInTrainingsRepository ?: createExercisesInTrainingsRepository(context)
    }

    fun provideExercisesRepository(context: Context): ExercisesRepository = synchronized(this) {
        return exercisesRepository ?: createExercisesRepository(context)
    }

    fun provideMuscleGroupsRepository(context: Context): MuscleGroupsRepository = synchronized(this) {
        return muscleGroupsRepository ?: createMuscleGroupsRepository(context)
    }

    @VisibleForTesting
    fun resetRepository() = synchronized(lock) {
        runBlocking {
            trainingsRepository?.deleteAll()
        }
        // Clear all data to avoid test pollution.
        database?.apply {
            clearAllTables()
            close()
        }
        database = null
        trainingsRepository = null
    }


    private fun createTrainingsRepository(context: Context): TrainingsRepository {
        fun createDataSource(context: Context): TrainingsLocalDataSource {
            val database = database ?: createDataBase(context)
            return TrainingsLocalDataSourceImpl(database.trainingDao())
        }

        val newRepo = TrainingsRepositoryImpl(null, createDataSource(context))
        trainingsRepository = newRepo
        return newRepo
    }

    private fun createHeroesRepository(context: Context): HeroesRepository {
        fun createDataSource(context: Context): HeroesLocalDataSource {
            val database = database ?: createDataBase(context)
            return HeroesLocalDataSourceImpl(database.heroDao())
        }

        val newRepo = HeroesRepositoryImpl(null, createDataSource(context))
        heroesRepository = newRepo
        return newRepo
    }

    private fun createExercisesInTrainingsRepository(context: Context): ExercisesInTrainingsRepository {
        fun createDataSource(context: Context): ExerciseInTrainingLocalDataSource {
            val database = database ?: createDataBase(context)
            return ExerciseInTrainingLocalDataSourceImpl(database.exerciseInTrainingDao())
        }

        val newRepo = ExercisesInTrainingsRepositoryImpl(null, createDataSource(context))
        exercisesInTrainingsRepository = newRepo
        return newRepo
    }

    private fun createExercisesRepository(context: Context): ExercisesRepository {
        fun createDataSource(context: Context): ExerciseLocalDataSource {
            val database = database ?: createDataBase(context)
            return ExerciseLocalDataSourceImpl(database.exerciseDao())
        }

        val newRepo = ExercisesRepositoryImpl(null, createDataSource(context))
        exercisesRepository = newRepo
        return newRepo
    }

    private fun createMuscleGroupsRepository(context: Context): MuscleGroupsRepository {
        fun createDataSource(context: Context): MuscleGroupLocalDataSource {
            val database = database ?: createDataBase(context)
            return MuscleGroupLocalDataSourceImpl(database.muscleGroupDao())
        }

        val newRepo = MuscleGroupsRepositoryImpl(null, createDataSource(context))
        muscleGroupsRepository = newRepo
        return newRepo
    }

    private fun createDataBase(context: Context): DB {
        val result = DB.getInstance(context)
        database = result
        return result
    }
}