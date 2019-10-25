package com.devtau.ff.ui.activities.clientsList

import com.devtau.ff.data.model.Client
import com.devtau.ff.ui.StandardView

interface ClientsView: StandardView {
    fun updateClients(list: List<Client>?): Unit?
}