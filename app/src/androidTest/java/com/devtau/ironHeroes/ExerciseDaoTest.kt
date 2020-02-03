package com.devtau.ironHeroes

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.devtau.ironHeroes.data.DB
import com.devtau.ironHeroes.data.dao.ExerciseDao
import com.devtau.ironHeroes.data.model.Exercise
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExerciseDaoTest {

    private lateinit var db: DB
    private lateinit var dao: ExerciseDao


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DB::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.exerciseDao()
    }

    @After fun closeDb() = db.close()


    @Test fun insertAndGet() {
        dao.insert(listOf(exerciseA)).blockingAwait()
        dao.getById(exerciseA.id).test().assertValue {
            exerciseA.id == it.exercise.id
        }
    }


    @Test fun testQtyAndSortOrder() {
        dao.insert(listOf(exerciseA, exerciseC, exerciseB)).blockingAwait()
        dao.getList().test().assertValue { list ->
            val sorted = arrayListOf<Exercise>()
            for (next in list) sorted.add(next.exercise)
            sorted.sortBy { it.name }
            3 == list.size
                    && sorted[0].name == list[0].exercise.name
                    && sorted[1].name == list[1].exercise.name
                    && sorted[2].name == list[2].exercise.name
        }
    }

    @Test fun deleteAllAndGet() {
        dao.insert(listOf(exerciseA)).blockingAwait()
        dao.delete().blockingAwait()
        dao.getById(exerciseA.id).test().assertNoValues()
    }

    @Test fun deleteListAndGet() {
        dao.insert(listOf(exerciseA, exerciseC, exerciseB)).blockingAwait()
        dao.delete(listOf(exerciseA, exerciseC, exerciseB)).blockingAwait()
        dao.getById(exerciseA.id).test().assertNoValues()
    }


    companion object {
        private val exerciseA = Exercise(1, "A", 1)
        private val exerciseB = Exercise(2, "B", 1)
        private val exerciseC = Exercise(3, "C", 1)
    }
}