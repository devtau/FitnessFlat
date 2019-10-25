package com.devtau.ff.data

import com.devtau.ff.data.model.Client
import com.devtau.ff.data.model.Trainer
import com.devtau.ff.data.model.Training
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

interface DataLayer {
    fun updateTrainers(list: List<Trainer?>?)
    fun deleteTrainers(list: List<Trainer?>?)

    fun updateClients(list: List<Client?>?)
    fun deleteClients(list: List<Client?>?)

    fun updateTrainings(list: List<Training?>?)
    fun deleteTrainings(list: List<Training?>?)

    fun clearDB()

    //возвращают подписку
    fun getClient(id: Long, listener: Consumer<Client?>): Disposable
    fun getClients(listener: Consumer<List<Client>?>): Disposable
    fun getTraining(id: Long, listener: Consumer<Training?>): Disposable
    fun getTrainings(listener: Consumer<List<Training>?>): Disposable

    //возвращают результат и закрывают подключение к бд
    fun getClientByIdAndClose(id: Long, listener: Consumer<Client?>)
    fun getTrainingByIdAndClose(id: Long, listener: Consumer<Training?>)
}