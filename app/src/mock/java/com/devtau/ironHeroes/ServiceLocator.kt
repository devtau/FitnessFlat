package com.devtau.ironHeroes

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.devtau.ironHeroes.data.FakeHeroesRemoteDataSource
import com.devtau.ironHeroes.data.FakeTrainingsRemoteDataSource
import com.devtau.ironHeroes.data.source.local.DB
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSource
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSource
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.data.source.repositories.HeroesRepositoryImpl
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepository
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepositoryImpl
import kotlinx.coroutines.runBlocking
/**
 * A Service Locator for the [TrainingsRepository]. This is the mock version, with a
 * [FakeTrainingsRemoteDataSource] and [FakeHeroesRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: DB? = null

    @Volatile var trainingsRepository: TrainingsRepository? = null
        @VisibleForTesting set
    @Volatile var heroesRepository: HeroesRepository? = null
        @VisibleForTesting set


    fun provideTrainingsRepository(context: Context): TrainingsRepository {
        synchronized(this) {
            return trainingsRepository ?: createTrainingsRepository(context)
        }
    }

    fun provideHeroesRepository(context: Context): HeroesRepository {
        synchronized(this) {
            return heroesRepository ?: createHeroesRepository(context)
        }
    }


    private fun createTrainingsRepository(context: Context): TrainingsRepository {
        val newRepo = TrainingsRepositoryImpl(FakeTrainingsRemoteDataSource, createTrainingsLocalDataSource(context))
        trainingsRepository = newRepo
        return newRepo
    }

    private fun createTrainingsLocalDataSource(context: Context): TrainingsLocalDataSource {
        val database = database ?: createDataBase(context)
        return TrainingsLocalDataSourceImpl(database.trainingDao())
    }

    private fun createDataBase(context: Context): DB {
        val result = DB.getInstance(context)
        database = result
        return result
    }

    private fun createHeroesRepository(context: Context): HeroesRepository {
        val newRepo = HeroesRepositoryImpl(FakeHeroesRemoteDataSource, createHeroesLocalDataSource(context))
        heroesRepository = newRepo
        return newRepo
    }

    private fun createHeroesLocalDataSource(context: Context): HeroesLocalDataSource {
        val database = database ?: createDataBase(context)
        return HeroesLocalDataSourceImpl(database.heroDao())
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
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
    }
}