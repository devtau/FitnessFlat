package com.devtau.ironHeroes.data

import com.devtau.ironHeroes.data.model.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

interface DataLayer {
    fun updateChampions(list: List<Champion?>?)
    fun deleteChampions(list: List<Champion?>?)

    fun updateHeroes(list: List<Hero?>?)
    fun deleteHeroes(list: List<Hero?>?)

    fun updateTrainings(list: List<Training?>?)
    fun deleteTrainings(list: List<Training?>?)

    fun updateExercises(list: List<Exercise?>?)
    fun deleteExercises(list: List<Exercise?>?)

    fun updateMuscleGroups(list: List<MuscleGroup?>?)
    fun deleteMuscleGroups(list: List<MuscleGroup?>?)

    fun clearDB()

    //возвращают подписку
    fun getHero(id: Long, listener: Consumer<Hero?>): Disposable
    fun getHeroes(listener: Consumer<List<Hero>?>): Disposable
    fun getTraining(id: Long, listener: Consumer<Training?>): Disposable
    fun getTrainings(listener: Consumer<List<Training>?>): Disposable
    fun getExercise(id: Long, listener: Consumer<Exercise?>): Disposable
    fun getExercises(listener: Consumer<List<Exercise>?>): Disposable
    fun getMuscleGroup(id: Long, listener: Consumer<MuscleGroup?>): Disposable
    fun getMuscleGroups(listener: Consumer<List<MuscleGroup>?>): Disposable

    //возвращают результат и закрывают подключение к бд
    fun getHeroByIdAndClose(id: Long, listener: Consumer<Hero?>)
    fun getTrainingByIdAndClose(id: Long, listener: Consumer<Training?>)
}