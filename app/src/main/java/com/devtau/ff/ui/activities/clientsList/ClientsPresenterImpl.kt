package com.devtau.ff.ui.activities.clientsList

import com.devtau.ff.db.DataLayer
import com.devtau.ff.rest.NetworkLayer
import com.devtau.ff.rest.model.Client
import com.devtau.ff.ui.DBSubscriber
import com.devtau.ff.util.PreferencesManager
import io.reactivex.functions.Consumer

class ClientsPresenterImpl(
    private val view: ClientsView,
    private val dataLayer: DataLayer,
    private val networkLayer: NetworkLayer,
    private val prefs: PreferencesManager?
): DBSubscriber(), ClientsPresenter {

    init {
        for (next in Client.getMock()) dataLayer.updateClient(next)
    }

    var clients: List<Client>? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(dataLayer.getClients(Consumer {
            clients = it
            view.updateClients(it)
        }))
    }
    //</editor-fold>
}