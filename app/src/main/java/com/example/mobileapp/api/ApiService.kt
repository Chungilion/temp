package com.example.mobileapp.api

import com.example.mobileapp.model.LoginRequest
import com.example.mobileapp.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/api/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
