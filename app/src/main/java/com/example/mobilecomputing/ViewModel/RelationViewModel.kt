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
import com.example.mobilecomputing.DAO.PostDAO
import com.example.mobilecomputing.DAO.RelationshipDAO
import com.example.mobilecomputing.SessionManager
import com.example.mobilecomputing.entity.PostEntity
import com.example.mobilecomputing.entity.PostWithUser
import com.example.mobilecomputing.entity.RelationWithUser
import com.example.mobilecomputing.entity.RelationshipEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class RelationViewModel(private val dao: RelationshipDAO) : ViewModel() {

    private val _currentUserId = MutableStateFlow<Int?>(null)
    fun setUserId(id: Int) {
        _currentUserId.value = id
    }
    val userFollowings: StateFlow<List<RelationWithUser>?> = _currentUserId
        .flatMapLatest { id ->
            if (id == null || id == -1) {
                kotlinx.coroutines.flow.flowOf(null)
            } else {
                dao.getFollowings(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    val userFollowers: StateFlow<List<RelationWithUser>?> = _currentUserId
        .flatMapLatest { id ->
            if (id == null || id == -1) {
                kotlinx.coroutines.flow.flowOf(null)
            } else {
                dao.getFollowers(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun createRelationship(entity: RelationshipEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("Bắt đầu insert: $entity")
                dao.insert(entity)
                println("Insert thành công vào Database!")
            } catch (e: Exception) {
                println("LỖI INSERT: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun deleteRelationship(entity: RelationshipEntity){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.unfollow(followerId = entity.followerId, followingId = entity.followingId)
            } catch (e: Exception) {
                println("LỖI INSERT: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}