package com.devtau.ironHeroes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.devtau.ironHeroes.data.DB
import com.devtau.ironHeroes.data.DataLayer
import com.devtau.ironHeroes.data.DataLayerImpl
import com.devtau.ironHeroes.data.model.Exercise
import io.reactivex.functions.Consumer
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ExerciseDaoTest {

    private lateinit var database: DB
    private lateinit var dataLayer: DataLayer

    private val exerciseA = Exercise(1, "A", 1)
    private val exerciseB = Exercise(2, "B", 1)
    private val exerciseC = Exercise(3, "C", 1)

    private var lock: CountDownLatch? = null
    private var exercises: List<Exercise?>? = null
    private var exercise: Exercise? = null


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            database = Room.inMemoryDatabaseBuilder(context, DB::class.java).build()
            dataLayer = DataLayerImpl(context, database)

            //non-alphabetical order to test that results are sorted by name
            lock = CountDownLatch(1)
            dataLayer.updateExercises(listOf(exerciseB, exerciseC, exerciseA))
            lock?.countDown()
            lock?.await(2000, TimeUnit.MILLISECONDS)
        }
    }

    @After fun closeDb() = database.close()


    @Test fun testGetExercises() {
        lock = CountDownLatch(1)
        dataLayer.getExercises(Consumer {
            exercises = it
            lock?.countDown()
        })
        lock?.await(2000, TimeUnit.MILLISECONDS)
        assertNotNull(exercises)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals(exercises?.size, Exercise.getMock(context).size)

        // Ensure list is sorted by name
        assertEquals(exercises?.get(0)?.id, exerciseA.id)
        assertEquals(exercises?.get(1)?.id, exerciseB.id)
        assertEquals(exercises?.get(2)?.id, exerciseC.id)
    }

    @Test fun testGetExercise() {
        lock = CountDownLatch(1)
        val idA = exerciseA.id
        if (idA != null) dataLayer.getExercise(idA, Consumer {
            exercise = it
            lock?.countDown()
        })
        lock?.await(2000, TimeUnit.MILLISECONDS)
        assertNotNull(exercise)
        assertEquals(exercise?.id, exerciseA.id)
    }
}