package com.devtau.ff.rest.listeners

import com.devtau.ff.rest.model.Client

interface ClientRegisteredListener {
    fun processClientRegistered(token: String?, client: Client?)
}