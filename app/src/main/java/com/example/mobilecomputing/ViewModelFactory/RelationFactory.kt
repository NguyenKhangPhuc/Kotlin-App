package com.example.mobilecomputing.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobilecomputing.DAO.PostDAO
import com.example.mobilecomputing.DAO.RelationshipDAO
import com.example.mobilecomputing.DAO.UserProfileDao
import com.example.mobilecomputing.ViewModel.PostViewModel
import com.example.mobilecomputing.ViewModel.RelationViewModel
import com.example.mobilecomputing.ViewModel.UserProfileViewModel

class RelationFactory(private val dao: RelationshipDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RelationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RelationViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel relationViewModel class")
    }
}