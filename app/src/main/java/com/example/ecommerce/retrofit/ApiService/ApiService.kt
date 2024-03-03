package com.example.ecommerce.retrofit.ApiService

import com.example.ecommerce.retrofit.MyData
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    fun getAllData() : Call<MyData>
}