package com.devtau.ff.rest

import com.devtau.ff.data.model.Client
import com.devtau.ff.rest.response.BaseResponse
import com.devtau.ff.rest.response.RegistrationResponse
import com.devtau.ff.rest.response.ClientResponse
import retrofit2.Call
import retrofit2.http.*

interface BackendAPI {

    @POST(VALIDATE_PHONE_ENDPOINT)
    fun validatePhone(
        @Query("phone") phone: String
    ): Call<BaseResponse>

    @POST(REGISTER_NEW_CLIENT_ENDPOINT)
    fun registerNewClient(
        @Body client: Client,
        @Query("code") smsValidationCode: Int?
    ): Call<RegistrationResponse>

    @GET(GET_CLIENT_ENDPOINT)
    fun getClient(
        @Header("token") token: String
    ): Call<ClientResponse>

    @POST(UPDATE_CLIENT_ENDPOINT)
    fun updateClient(
        @Header("token") token: String,
        @Body client: Client
    ): Call<ClientResponse>


    companion object {
        const val VALIDATE_PHONE_ENDPOINT = "/validate_phone"
        const val REGISTER_NEW_CLIENT_ENDPOINT = "/client/register"
        const val GET_CLIENT_ENDPOINT = "/api/client/get"
        const val UPDATE_CLIENT_ENDPOINT = "/api/client/update"
    }
}