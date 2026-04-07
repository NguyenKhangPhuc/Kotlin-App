package com.example.mobilecomputing

data class ChatRequest(
    val query: String,
    val user_id: String
)

data class ChatResponse(
    val message: String,
    val type: String
)