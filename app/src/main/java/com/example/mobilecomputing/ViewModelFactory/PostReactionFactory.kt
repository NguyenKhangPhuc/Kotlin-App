package com.example.mobilecomputing.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobilecomputing.DAO.RelationshipDAO
import com.example.mobilecomputing.ViewModel.PostReactionViewModel
import com.example.mobilecomputing.ViewModel.RelationViewModel
import com.example.mobilecomputing.DAO.PostReactionDAO

class PostReactionFactory(private val dao: PostReactionDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostReactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostReactionViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel relationViewModel class")
    }
}