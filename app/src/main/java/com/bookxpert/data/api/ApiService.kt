package com.bookxpert.data.api

import com.bookxpert.data.db.entities.Device
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("objects")
    suspend fun getDevices(): Response<List<Device>>
}
