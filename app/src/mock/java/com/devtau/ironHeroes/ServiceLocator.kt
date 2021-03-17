package com.devtau.ironHeroes

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.devtau.ironHeroes.data.FakeTrainingsRemoteDataSource
import com.devtau.ironHeroes.data.source.local.DB
import com.devtau.ironHeroes.data.source.local.exercise.ExerciseLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.exerciseInTraining.ExerciseInTrainingLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.muscleGroup.MuscleGroupLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.repositories.*
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * A Service Locator for repositories. This is the mock version, with fake RemoteDataSources
 */
object ServiceLocator {

    private val lock = Any()
    private var database: DB? = null

    @Volatile
    var trainingsRepository: TrainingsRepository? = null
        @VisibleForTesting set

    @Volatile
    var heroesRepository: HeroesRepository? = null
        @VisibleForTesting set

    @Volatile
    var exercisesInTrainingsRepository: ExercisesInTrainingsRepository? = null
        @VisibleForTesting set

    @Volatile
    var exercisesRepository: ExercisesRepository? = null
        @VisibleForTesting set

    @Volatile
    var muscleGroupsRepository: MuscleGroupsRepository? = null
        @VisibleForTesting set


    fun provideTrainingsRepository(context: Context): TrainingsRepository = synchronized(this) {
        return trainingsRepository ?: createTrainingsRepository(context)
    }

    fun provideHeroesRepository(context: Context): HeroesRepository = synchronized(this) {
        return heroesRepository ?: createHeroesRepository(context)
    }

    fun provideExercisesInTrainingsRepository(context: Context): ExercisesInTrainingsRepository =
        synchronized(this) {
            return exercisesInTrainingsRepository ?: createExercisesInTrainingsRepository(context)
        }

    fun provideExercisesRepository(context: Context): ExercisesRepository = synchronized(this) {
        return exercisesRepository ?: createExercisesRepository(context)
    }

    fun provideMuscleGroupsRepository(context: Context): MuscleGroupsRepository =
        synchronized(this) {
            return muscleGroupsRepository ?: createMuscleGroupsRepository(context)
        }

    @VisibleForTesting
    fun resetRepository() = synchronized(lock) {
        runBlocking {
            FakeTrainingsRemoteDataSource.deleteAll()
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
        val db = provideDataBase(context)
        val newRepo = TrainingsRepositoryImpl(
            TrainingsLocalDataSourceImpl(db.trainingDao())
        )
        trainingsRepository = newRepo
        return newRepo
    }

    private fun createHeroesRepository(context: Context): HeroesRepository {
        val db = provideDataBase(context)
        val newRepo = HeroesRepositoryImpl(
            HeroesLocalDataSourceImpl(db.heroDao())
        )
        heroesRepository = newRepo
        return newRepo
    }

    private fun createExercisesInTrainingsRepository(context: Context): ExercisesInTrainingsRepository {
        val db = provideDataBase(context)
        val newRepo = ExercisesInTrainingsRepositoryImpl(
            ExerciseInTrainingLocalDataSourceImpl(db.exerciseInTrainingDao())
        )
        exercisesInTrainingsRepository = newRepo
        return newRepo
    }

    private fun createExercisesRepository(context: Context): ExercisesRepository {
        val db = provideDataBase(context)
        val newRepo = ExercisesRepositoryImpl(
            ExerciseLocalDataSourceImpl(db.exerciseDao())
        )
        exercisesRepository = newRepo
        return newRepo
    }

    private fun createMuscleGroupsRepository(context: Context): MuscleGroupsRepository {
        val db = provideDataBase(context)
        val newRepo = MuscleGroupsRepositoryImpl(
            MuscleGroupLocalDataSourceImpl(db.muscleGroupDao())
        )
        muscleGroupsRepository = newRepo
        return newRepo
    }

    private fun provideDataBase(context: Context): DB = database ?: with(DB.getInstance(context)) {
        Timber.d("db initialized")
        database = this
        this
    }
}