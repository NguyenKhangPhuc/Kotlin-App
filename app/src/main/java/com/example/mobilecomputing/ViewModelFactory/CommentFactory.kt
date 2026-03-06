package com.example.mobilecomputing.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobilecomputing.DAO.CommentDAO
import com.example.mobilecomputing.ViewModel.CommentViewModel

class CommentFactory(private val dao: CommentDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommentViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}