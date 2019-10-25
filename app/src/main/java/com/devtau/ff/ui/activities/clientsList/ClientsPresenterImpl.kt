package com.devtau.ff.ui.activities.clientsList

import com.devtau.ff.data.DataLayer
import com.devtau.ff.rest.NetworkLayer
import com.devtau.ff.data.model.Client
import com.devtau.ff.data.model.Trainer
import com.devtau.ff.data.model.Training
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
        dataLayer.updateTrainers(Trainer.getMock())
        dataLayer.updateClients(Client.getMock())
        dataLayer.updateTrainings(Training.getMock())
    }

    var clients: List<Client>? = null
    var trainings: List<Training>? = null


    //<editor-fold desc="Presenter overrides">
    override fun restartLoaders() {
        disposeOnStop(dataLayer.getClients(Consumer {
            clients = it
            view.updateClients(it)
        }))
        disposeOnStop(dataLayer.getTrainings(Consumer {
            trainings = it
        }))
    }
    //</editor-fold>
}