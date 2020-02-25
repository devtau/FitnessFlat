package com.devtau.ironHeroes.data.source.remote

import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.source.remote.response.BaseResponse
import com.devtau.ironHeroes.data.source.remote.response.HeroResponse
import com.devtau.ironHeroes.data.source.remote.response.RegistrationResponse
import com.devtau.ironHeroes.data.source.remote.response.ResultsResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface BackendAPI {

    @POST("/validate_phone")
//    suspend
    fun validatePhone(
        @Query("phone") phone: String
    ): Call<BaseResponse>

    @POST("/hero/register")
//    suspend
    fun registerNewHero(
        @Body hero: Hero,
        @Query("code") smsValidationCode: Int?
    ): Call<RegistrationResponse>

    @GET("/api/hero/get")
//    suspend
    fun getHero(
        @Header("token") token: String
    ): Call<HeroResponse>

    @POST("/api/hero/update")
//    suspend
    fun updateHero(
        @Header("token") token: String,
        @Body hero: Hero
    ): Call<HeroResponse>

    @GET("/api/hero/trainings")
    suspend fun getTrainings(
        @Header("token") token: String
    ): Response<ResultsResponse<Training>>

    companion object {
        const val ENDPOINT = "https://yandex.ru/"
    }
}