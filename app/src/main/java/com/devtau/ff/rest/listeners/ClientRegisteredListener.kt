package com.devtau.ff.rest.listeners

import com.devtau.ff.data.model.Client

interface ClientRegisteredListener {
    fun processClientRegistered(token: String?, client: Client?)
}