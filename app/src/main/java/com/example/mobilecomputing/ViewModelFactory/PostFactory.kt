package com.example.mobilecomputing.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobilecomputing.DAO.PostDAO
import com.example.mobilecomputing.DAO.UserProfileDao
import com.example.mobilecomputing.ViewModel.PostViewModel
import com.example.mobilecomputing.ViewModel.UserProfileViewModel

class PostFactory(private val dao: PostDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}