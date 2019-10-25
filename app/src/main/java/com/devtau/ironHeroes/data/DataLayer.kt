package com.devtau.ironHeroes.data

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Champion
import com.devtau.ironHeroes.data.model.Training
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

interface DataLayer {
    fun updateChampions(list: List<Champion?>?)
    fun deleteChampions(list: List<Champion?>?)

    fun updateHeroes(list: List<Hero?>?)
    fun deleteHeroes(list: List<Hero?>?)

    fun updateTrainings(list: List<Training?>?)
    fun deleteTrainings(list: List<Training?>?)

    fun clearDB()

    //возвращают подписку
    fun getHero(id: Long, listener: Consumer<Hero?>): Disposable
    fun getHeroes(listener: Consumer<List<Hero>?>): Disposable
    fun getTraining(id: Long, listener: Consumer<Training?>): Disposable
    fun getTrainings(listener: Consumer<List<Training>?>): Disposable

    //возвращают результат и закрывают подключение к бд
    fun getHeroByIdAndClose(id: Long, listener: Consumer<Hero?>)
    fun getTrainingByIdAndClose(id: Long, listener: Consumer<Training?>)
}