package com.devtau.ff.db

import android.content.Context
import com.devtau.ff.db.tables.ClientStored
import com.devtau.ff.rest.model.Client
import com.devtau.ff.util.Logger
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class DataLayerImpl(context: Context): DataLayer {

    private var db = DB.getInstance(context)


    override fun updateClient(client: Client?) = if (client == null) {
        Logger.e(LOG_TAG, "updateClient. client is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateClient. client=$client")
        db.clientDao().insert(ClientStored(client)).subscribeDefault("updateClient. inserted")
    }

    override fun updateClientExceptPhone(client: Client?) = if (client == null) {
        Logger.e(LOG_TAG, "updateClientExceptPhone. client is null. aborting")
    } else {
        Logger.d(LOG_TAG, "updateClientExceptPhone. client=$client")
        var disposable: Disposable? = null
        disposable = db.clientDao().getById(client.id)
            .subscribeOn(Schedulers.io())
            .map(ClientStored::convertToClient)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ oldClient ->
                Logger.d(LOG_TAG, "updateClientExceptPhone. found oldClient=$oldClient")
                if (!client.deepEquals(if (oldClient.isEmpty()) null else oldClient))
                    db.clientDao().insert(ClientStored(client)).subscribeDefault("updateClientExceptPhone. inserted")
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in updateClientExceptPhone: ${it.message}")}
    }

    override fun deleteClient(client: Client?) = if (client == null) {
        Logger.e(LOG_TAG, "deleteClient. client is null. aborting")
    } else {
        Logger.d(LOG_TAG, "deleteClient. client=$client")
        db.clientDao().delete(ClientStored(client)).subscribeDefault("deleteClient. deleted")
    }

    override fun clearDB() {
        Logger.w(LOG_TAG, "going to clearDB")
        db.clientDao().delete().subscribeDefault("clearDB. orders deleted")
    }

    override fun getClient(id: Long, listener: Consumer<Client?>): Disposable = db.clientDao().getById(id)
        .subscribeOn(Schedulers.io())
        .map { it.convertToClient() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(if (it.isEmpty()) null else it) })
        { Logger.e(LOG_TAG, "Error in getClient: ${it.message}") }

    override fun getClients(listener: Consumer<List<Client>?>): Disposable = db.clientDao().getList()
        .subscribeOn(Schedulers.io())
        .map { ClientStored.convertListToClients(it) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ listener.accept(it) })
        { Logger.e(LOG_TAG, "Error in getClients: ${it.message}") }

    override fun getClientByIdAndClose(id: Long, listener: Consumer<Client?>) {
        var disposable: Disposable? = null
        disposable = db.clientDao().getById(id)
            .subscribeOn(Schedulers.io())
            .map(ClientStored::convertToClient)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                listener.accept(if (it.isEmpty()) null else it)
                disposable?.dispose()
            }) { Logger.e(LOG_TAG, "Error in getClientByIdAndClose: ${it.message}") }
    }


    private fun Completable.subscribeDefault(msg: String?) {
        var disposable: Disposable? = null
        disposable = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Logger.d(LOG_TAG, msg)
            disposable?.dispose()
        }
    }


    companion object {
        private const val LOG_TAG = "DataLayer"
    }
}