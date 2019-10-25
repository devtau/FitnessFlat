package com.devtau.ff.rest

import com.devtau.ff.rest.listeners.ClientRegisteredListener
import com.devtau.ff.data.model.Client
import io.reactivex.functions.Consumer

interface NetworkLayer {
    fun validatePhone(phone: String)
    fun registerNewClient(client: Client, smsValidationCode: Int?, listener: ClientRegisteredListener)
    fun getClient(token: String?, listener: Consumer<Client?>)
    fun updateClient(client: Client?, token: String?)
}