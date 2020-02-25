package com.devtau.ironHeroes.data.source.local.hero

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Error
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.enums.HumanType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db
 */
class HeroesLocalDataSourceImpl internal constructor(
    private val dao: HeroDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): HeroesLocalDataSource {

    //<editor-fold desc="Single object operations">
    override suspend fun saveItem(item: Hero) = withContext(ioDispatcher) {
        dao.insertNow(item)
    }

    override suspend fun getItem(id: Long): Result<Hero> = withContext(ioDispatcher) {
        try {
            val item = dao.getById(id)
            if (item != null) {
                return@withContext Success(item)
            } else {
                return@withContext Error(Exception("Hero not found"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override fun observeItem(id: Long): LiveData<Result<Hero>> =
        dao.observeItem(id).map {
            Success(it)
        }

    override suspend fun deleteItem(id: Long) = withContext(ioDispatcher) {
        dao.delete(id)
    }
    //</editor-fold>


    //<editor-fold desc="Group operations">
    override suspend fun saveList(list: List<Hero>) = withContext(ioDispatcher) {
        dao.insertList(list)
    }

    override suspend fun getList(humanType: HumanType?): Result<List<Hero>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(if (humanType == null)
                dao.getList()
            else
                dao.getList(humanType.ordinal)
            )
        } catch (e: Exception) {
            Error(e)
        }
    }

    override fun observeList(humanType: HumanType?): LiveData<Result<List<Hero>>> {
        val liveData = if (humanType == null) dao.observeList() else dao.observeList(humanType.ordinal)
        return liveData.map {
            Success(it)
        }
    }

    override suspend fun deleteAll() = withContext(ioDispatcher) {
        dao.delete()
    }
    //</editor-fold>
}