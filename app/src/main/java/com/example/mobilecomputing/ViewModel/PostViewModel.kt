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
import com.example.mobilecomputing.SessionManager
import com.example.mobilecomputing.entity.PostEntity
import com.example.mobilecomputing.entity.PostWithUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class PostViewModel(private val dao: PostDAO) : ViewModel() {

    val allPosts: StateFlow<List<PostWithUser>?> = dao.getAllPostsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _currentUserId = MutableStateFlow<Int?>(null)
    fun setUserId(id: Int) {
        _currentUserId.value = id
    }
    val userPosts: StateFlow<List<PostWithUser>?> = _currentUserId
        .flatMapLatest { id ->
            if (id == null || id == -1) {
                kotlinx.coroutines.flow.flowOf(null)
            } else {
                dao.getPostsByUserId(id) // Room trả về Flow<UserProfileEntity?>
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun createPost(entity: PostEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("Bắt đầu insert: $entity")
                dao.insertPost(entity)
                println("Insert thành công vào Database!")
            } catch (e: Exception) {
                // Nếu có lỗi ForeignKey hay bất cứ lỗi gì, nó sẽ hiện ở đây
                println("LỖI INSERT: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}