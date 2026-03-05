package com.example.mobilecomputing.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.mobilecomputing.DAO.RelationshipDAO
import com.example.mobilecomputing.SessionManager
import com.example.mobilecomputing.DAO.PostReactionDAO
import com.example.mobilecomputing.entity.PostEntity
import com.example.mobilecomputing.entity.PostReactionEntity
import com.example.mobilecomputing.entity.PostWithUser
import com.example.mobilecomputing.entity.RelationWithUser
import com.example.mobilecomputing.entity.RelationshipEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class PostReactionViewModel(private val dao: PostReactionDAO) : ViewModel() {

    private val _currentPostId = MutableStateFlow<Int?>(null)
    fun setUserId(id: Int) {
        _currentPostId.value = id
    }
    val reactions: StateFlow<List<PostReactionEntity>?> = _currentPostId
        .flatMapLatest { id ->
            if (id == null || id == -1) {
                kotlinx.coroutines.flow.flowOf(null)
            } else {
                dao.getReactionsByPostId(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun createReaction(entity: PostReactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("Bắt đầu insert: $entity")
                dao.insertReaction(entity)
                println("Insert thành công vào Database!")
            } catch (e: Exception) {
                println("LỖI INSERT: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun deleteReaction(entity: PostReactionEntity){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.deleteReactionByUserAndPost(userId = entity.userId, postId = entity.postId)
            } catch (e: Exception) {
                println("LỖI INSERT: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}