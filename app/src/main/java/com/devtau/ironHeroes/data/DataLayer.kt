package com.devtau.ironHeroes.data

import com.devtau.ironHeroes.data.model.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

interface DataLayer {
    fun updateHeroes(list: List<Hero?>?)
    fun deleteHeroes(list: List<Hero?>?)

    fun updateTrainings(list: List<Training?>?)
    fun updateTraining(training: Training?, listener: Consumer<Long>? = null)
    fun deleteTrainings(list: List<Training?>?)

    fun updateExercises(list: List<Exercise>)
    fun deleteExercises(list: List<Exercise>?)

    fun updateExercisesInTraining(list: List<ExerciseInTraining?>?)
    fun deleteExercisesInTraining(list: List<ExerciseInTraining?>?)

    fun updateMuscleGroups(list: List<MuscleGroup?>?)
    fun deleteMuscleGroups(list: List<MuscleGroup?>?)

    fun clearDB()

    //возвращают подписку
    fun getHero(id: Long, listener: Consumer<Hero?>): Disposable
    fun getHeroes(listener: Consumer<List<Hero>?>): Disposable
    fun getChampions(listener: Consumer<List<Hero>?>): Disposable

    fun getTraining(id: Long, listener: Consumer<Training?>): Disposable
    fun getTrainings(listener: Consumer<List<Training>>): Disposable

    fun getExercise(id: Long, listener: Consumer<Exercise?>): Disposable
    fun getExercises(listener: Consumer<List<Exercise>?>): Disposable

    fun getExerciseInTraining(id: Long, listener: Consumer<ExerciseInTraining?>): Disposable
    fun getExercisesInTraining(trainingId: Long, listener: Consumer<List<ExerciseInTraining>?>): Disposable
    fun getAllExercisesInTrainings(heroId: Long, listener: Consumer<List<ExerciseInTraining>?>): Disposable

    fun getMuscleGroup(id: Long, listener: Consumer<MuscleGroup?>): Disposable
    fun getMuscleGroups(listener: Consumer<List<MuscleGroup>?>): Disposable

    //возвращают результат и закрывают подключение к бд
    fun getHeroByIdAndClose(id: Long, listener: Consumer<Hero?>)
    fun getTrainingByIdAndClose(id: Long, listener: Consumer<Training?>)
    fun getExerciseAndClose(id: Long, listener: Consumer<Exercise?>)
    fun getExercisesAndClose(listener: Consumer<List<Exercise>?>)
    fun getExercisesInTrainingAndClose(trainingId: Long, listener: Consumer<List<ExerciseInTraining>?>)
    fun getExerciseInTrainingAndClose(id: Long, listener: Consumer<ExerciseInTraining?>)
    fun getAllExercisesInTrainingsAndClose(heroId: Long, maxRelevantDate: Long, sortAscending: Boolean, listener: Consumer<List<ExerciseInTraining>?>)
    fun getAllTrainingsAndClose(listener: Consumer<List<Training>?>)
}