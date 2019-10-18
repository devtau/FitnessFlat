package com.devtau.ff.rest.response

import com.devtau.ff.rest.model.Client

class RegistrationResponse : BaseResponse() {

    val token: String? get() = data?.token
    val client: Client? get() = data?.client
    private val data: Data? = null

    private class Data(val client: Client? = null, val token: String? = null)
}