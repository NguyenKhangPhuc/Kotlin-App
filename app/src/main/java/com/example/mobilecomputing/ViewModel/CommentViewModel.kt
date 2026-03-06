package com.example.mobilecomputing.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputing.DAO.CommentDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.mobilecomputing.DAO.RelationshipDAO
import com.example.mobilecomputing.SessionManager
import com.example.mobilecomputing.DAO.PostReactionDAO
import com.example.mobilecomputing.entity.CommentEntity
import com.example.mobilecomputing.entity.CommentWithUser
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

class CommentViewModel(private val dao: CommentDAO) : ViewModel() {

    private val _currentPostId = MutableStateFlow<Int?>(null)
    fun setPostId(id: Int) {
        _currentPostId.value = id
    }
    val comments: StateFlow<List<CommentWithUser>?> = _currentPostId
        .flatMapLatest { id ->
            if (id == null || id == -1) {
                kotlinx.coroutines.flow.flowOf(null)
            } else {
                dao.getCommentsWithUserByPostId(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun insertComment(entity: CommentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("Bắt đầu insert: $entity")
                dao.insertComment(entity)
                println("Insert thành công vào Database!")
            } catch (e: Exception) {
                println("LỖI INSERT: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun deleteComment(commentId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.deleteComment(commentId)
            } catch (e: Exception) {
                println("LỖI INSERT: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}