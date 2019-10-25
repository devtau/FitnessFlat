package com.devtau.ff.ui

import com.devtau.ff.data.DataLayerImpl
import com.devtau.ff.rest.NetworkLayerImpl
import com.devtau.ff.ui.activities.clientsList.ClientsActivity
import com.devtau.ff.ui.activities.clientsList.ClientsPresenterImpl
import com.devtau.ff.ui.activities.clientDetails.ClientDetailsActivity
import com.devtau.ff.ui.activities.clientDetails.ClientDetailsPresenterImpl
import com.devtau.ff.util.PreferencesManager

class DependencyRegistry {

    fun inject(activity: ClientsActivity) {
        val dataLayer = DataLayerImpl(activity)
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager.getInstance(activity)
        activity.presenter = ClientsPresenterImpl(
            activity,
            dataLayer,
            networkLayer,
            prefs
        )
    }

    fun inject(activity: ClientDetailsActivity) {
        val dataLayer = DataLayerImpl(activity)
        val networkLayer = NetworkLayerImpl(activity)
        val prefs = PreferencesManager.getInstance(activity)
        val clientId = activity.intent?.extras?.getLong(ClientDetailsActivity.CLIENT_ID)
        activity.presenter = ClientDetailsPresenterImpl(
            activity,
            dataLayer,
            networkLayer,
            prefs,
            clientId
        )
    }
}