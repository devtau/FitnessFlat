package com.devtau.ironHeroes.rest

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.rest.response.BaseResponse
import com.devtau.ironHeroes.rest.response.RegistrationResponse
import com.devtau.ironHeroes.rest.response.HeroResponse
import retrofit2.Call
import retrofit2.http.*

interface BackendAPI {

    @POST(VALIDATE_PHONE_ENDPOINT)
    fun validatePhone(
        @Query("phone") phone: String
    ): Call<BaseResponse>

    @POST(REGISTER_NEW_HERO_ENDPOINT)
    fun registerNewHero(
        @Body hero: Hero,
        @Query("code") smsValidationCode: Int?
    ): Call<RegistrationResponse>

    @GET(GET_HERO_ENDPOINT)
    fun getHero(
        @Header("token") token: String
    ): Call<HeroResponse>

    @POST(UPDATE_HERO_ENDPOINT)
    fun updateHero(
        @Header("token") token: String,
        @Body hero: Hero
    ): Call<HeroResponse>


    companion object {
        const val VALIDATE_PHONE_ENDPOINT = "/validate_phone"
        const val REGISTER_NEW_HERO_ENDPOINT = "/hero/register"
        const val GET_HERO_ENDPOINT = "/api/hero/get"
        const val UPDATE_HERO_ENDPOINT = "/api/hero/update"
    }
}