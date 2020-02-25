package com.devtau.ironHeroes

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.devtau.ironHeroes.data.source.local.DB
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSource
import com.devtau.ironHeroes.data.source.local.hero.HeroesLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSource
import com.devtau.ironHeroes.data.source.local.training.TrainingsLocalDataSourceImpl
import com.devtau.ironHeroes.data.source.remote.HeroesRemoteDataSource
import com.devtau.ironHeroes.data.source.remote.TrainingsRemoteDataSource
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.data.source.repositories.HeroesRepositoryImpl
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepository
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepositoryImpl
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

    fun provideTrainingsRepository(context: Context): TrainingsRepository = synchronized(this) {
        return trainingsRepository ?: createTrainingsRepository(context)
    }

    fun provideHeroesRepository(context: Context): HeroesRepository = synchronized(this) {
        return heroesRepository ?: createHeroesRepository(context)
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
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
    }


    private fun createTrainingsRepository(context: Context): TrainingsRepository {
        fun createDataSource(context: Context): TrainingsLocalDataSource {
            val database = database ?: createDataBase(context)
            return TrainingsLocalDataSourceImpl(database.trainingDao())
        }

        val newRepo = TrainingsRepositoryImpl(
            TrainingsRemoteDataSource(context),
            createDataSource(context)
        )
        trainingsRepository = newRepo
        return newRepo
    }

    private fun createHeroesRepository(context: Context): HeroesRepository {
        fun createDataSource(context: Context): HeroesLocalDataSource {
            val database = database ?: createDataBase(context)
            return HeroesLocalDataSourceImpl(database.heroDao())
        }

        val newRepo = HeroesRepositoryImpl(
            HeroesRemoteDataSource(context),
            createDataSource(context)
        )
        heroesRepository = newRepo
        return newRepo
    }

    private fun createDataBase(context: Context): DB {
        val result = DB.getInstance(context)
        database = result
        return result
    }
}