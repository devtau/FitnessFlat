package com.devtau.ff.db

import com.devtau.ff.rest.model.Client
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

interface DataLayer {
    fun updateClient(client: Client?)
    fun updateClientExceptPhone(client: Client?)
    fun deleteClient(client: Client?)
    fun clearDB()

    //возвращают подписку
    fun getClient(id: Long, listener: Consumer<Client?>): Disposable
    fun getClients(listener: Consumer<List<Client>?>): Disposable

    //возвращают результат и закрывают подключение к бд
    fun getClientByIdAndClose(id: Long, listener: Consumer<Client?>)
}