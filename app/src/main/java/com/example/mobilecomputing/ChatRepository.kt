package com.example.mobilecomputing


class ChatRepository {
    private val apiService = RetrofitClient.instance

    // Hàm suspend để có thể đợi kết quả mà không chặn thread
    suspend fun postMessage(queryData: String, userId: String): String {
        val response = apiService.sendChat(ChatRequest(queryData, userId))
        return response.message
    }
}