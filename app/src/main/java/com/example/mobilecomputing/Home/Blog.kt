package com.example.mobilecomputing.Home

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.mobilecomputing.Screen
import com.example.mobilecomputing.SessionManager
import com.example.mobilecomputing.entity.PostWithUser
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.RectangleShape
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilecomputing.AppDatabase
import com.example.mobilecomputing.ViewModel.CommentViewModel
import com.example.mobilecomputing.ViewModel.PostReactionViewModel
import com.example.mobilecomputing.ViewModel.RelationViewModel
import com.example.mobilecomputing.ViewModelFactory.CommentFactory
import com.example.mobilecomputing.ViewModelFactory.PostReactionFactory
import com.example.mobilecomputing.ViewModelFactory.RelationFactory
import com.example.mobilecomputing.entity.PostReactionEntity
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.ui.text.font.FontWeight
import com.example.mobilecomputing.entity.CommentEntity
import com.example.mobilecomputing.entity.CommentWithUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Blog(post: PostWithUser, navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val idFromPrefs = sessionManager.getUserId()

    val database = AppDatabase.getInstance(context)
    val postReactionFactory = PostReactionFactory(database.postReactionDAO())
    val postReactionViewModel: PostReactionViewModel = viewModel(key = "reaction_${post.post.id}",factory = postReactionFactory)
    postReactionViewModel.setPostId(post.post.id)
    val reactions by postReactionViewModel.reactions.collectAsState()

    val commentFactory = CommentFactory(database.commentDAO())
    val commentViewModel: CommentViewModel = viewModel(key = "comment_${post.post.id}",factory = commentFactory)
    commentViewModel.setPostId(post.post.id)
    val comments by commentViewModel.comments.collectAsState()

    var showCommentModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable {
                        if (idFromPrefs == post.user.id){
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }else {

                            navController.navigate(Screen.UserProfile.createRoute(post.user.id)){
                                restoreState = true
                                launchSingleTop = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (post.user.imagePath != null) {

                    AsyncImage(
                        model = post.user.imagePath,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            println("This is username ${post.user.username}")
            Column {
                Text(post.user.username!!, style = MaterialTheme.typography.titleSmall)
                Text(post.post.createdAt!!, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = post.post.content,
            style = MaterialTheme.typography.bodyMedium
        )

            if (!post.post.imagePath.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.post.imagePath,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        println("This is reactions from id ${post} --- ${reactions}")
        val isLiked = reactions?.any { it.userId == idFromPrefs }
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLiked == true){
                TextButton(
                    onClick = {
                        val postReaction = PostReactionEntity(userId = idFromPrefs, postId = post.post.id)
                        postReactionViewModel.deleteReaction(postReaction)
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RectangleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Like (${reactions?.size ?: 0})", color = Color.Black)
                }
            }else {
                TextButton(
                    onClick = {
                        val postReaction = PostReactionEntity(userId = idFromPrefs, postId = post.post.id)
                        postReactionViewModel.createReaction(postReaction)
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RectangleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Like (${reactions?.size ?: 0})", color = Color.Black)
                }
            }

            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = Color.Black
            )

            TextButton(
                onClick = {
                    showCommentModal = true
                },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = RectangleShape
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Comments",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Comments (${comments?.size ?: 0})", color = Color.Black)
            }
        }
        if (showCommentModal) {
            ModalBottomSheet(
                onDismissRequest = { showCommentModal = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .imePadding()
                        .padding(bottom = 16.dp)
                ) {

                    Box(modifier = Modifier.weight(1f)) {
                        if (comments.isNullOrEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No comments yet", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                itemsIndexed(comments!!) { index, comment ->
                                    CommentItem(comment, commentViewModel, idFromPrefs)
                                    if (index < comments!!.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            thickness = 0.5.dp,
                                            color = Color.LightGray.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        CommentInputSection(
                            imageModel = post.user.imagePath,
                            post = post,
                            currentUserId = idFromPrefs,
                            commentViewModel = commentViewModel
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CommentItem(item: CommentWithUser, commentViewModel: CommentViewModel, currentUserId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),

            contentAlignment = Alignment.Center
        ) {
            if (item.user.imagePath != null) {
                println("Taken image -- ${item.user.imagePath}")

                AsyncImage(
                    model = item.user.imagePath,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.user.username ?: "Unknown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.comment.createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = item.comment.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        if (currentUserId == item.user.id){
            IconButton(
                onClick = {
                    commentViewModel.deleteComment(item.comment.id)
                },
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Comment",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CommentInputSection(
    imageModel: Any?,
    post: PostWithUser,
    currentUserId: Int,
    commentViewModel: CommentViewModel
) {
    var commentText by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp), tint = Color.White)
            }
        }

        TextField(
            value = commentText,
            onValueChange = {commentText = it},
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .heightIn(min = 44.dp),
            placeholder = { Text("Write a comment...", color = Color.Gray) },
            singleLine = false,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(20.dp),
            maxLines = 4
        )
        IconButton(onClick = {
            val comment = CommentEntity(userId = currentUserId, postId = post.post.id, content = commentText, createdAt = getCurrentFormattedDateTime())
            commentViewModel.insertComment(comment)
            commentText = ""
        }) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}