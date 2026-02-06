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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class UserProfileViewModel(private val dao: UserProfileDao) : ViewModel() {
    val userProfile: StateFlow<UserProfileEntity?> = dao.getProfileFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun saveProfile(entity: UserProfileEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(entity)
        }
    }
}