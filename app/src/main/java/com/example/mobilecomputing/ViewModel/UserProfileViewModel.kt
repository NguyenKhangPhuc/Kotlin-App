package com.example.mobilecomputing.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputing.DAO.UserProfileDao
import com.example.mobilecomputing.entity.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mobilecomputing.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class UserProfileViewModel(private val dao: UserProfileDao) : ViewModel() {
    val userProfile: StateFlow<UserProfileEntity?> = dao.getProfileFlow(0)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun signup(entity: UserProfileEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(entity)
        }
    }

    fun login(emailInput: String, passwordInput: String, onResult: (Boolean) -> Unit, sessionManager: SessionManager) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Tìm user trong DB theo username
            val user = dao.getUserProfile(emailInput)

            // 2. Kiểm tra xem user có tồn tại và password có khớp không
            val isSuccess = if (user != null) {
                user.password == passwordInput // So sánh text (hoặc so sánh Hash nếu có)
            } else {
                false
            }

            withContext(Dispatchers.Main) {
                sessionManager.saveUserId(user.id)
                onResult(isSuccess) // Bên trong onResult này bạn có gọi navController.navigate
            }
        }
    }
}