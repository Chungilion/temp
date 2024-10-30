package com.example.mobileapp.api

import com.example.mobileapp.model.CreateAccountRequest
import com.example.mobileapp.model.LoginRequest
import com.example.mobileapp.model.LoginResponse
import com.example.mobileapp.model.UserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface AccountApi {
    @POST("account/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("account/create")
    fun createAccount(@Body request: CreateAccountRequest): Call<Void>

    @GET("account/userinfo")
    fun getUserInfo(): Call<UserInfo>
}




