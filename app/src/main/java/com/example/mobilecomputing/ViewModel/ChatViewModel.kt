package com.example.mobilecomputing.ViewModel
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputing.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _chatReply = MutableStateFlow<String?>(null)
    val chatReply = _chatReply.asStateFlow()

    fun sendChat(query: String, uid: String) {
        viewModelScope.launch {
            try {
                Log.d("ChatBot", "Sending query: $query for uid: $uid")

                val result = repository.postMessage(query, uid)

                // Log kết quả trả về
                Log.d("ChatBot", "Response received: $result")
                _chatReply.value = result
            } catch (e: Exception) {
                _chatReply.value = "Error: ${e.message}"
            }
        }
    }
}