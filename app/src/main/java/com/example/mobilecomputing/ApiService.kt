package com.example.mobilecomputing

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("chat")
    suspend fun sendChat(@Body request: ChatRequest): ChatResponse
}