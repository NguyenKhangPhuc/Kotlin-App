package com.example.mobilecomputing.Home

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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.RectangleShape
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilecomputing.AppDatabase
import com.example.mobilecomputing.ViewModel.PostReactionViewModel
import com.example.mobilecomputing.ViewModel.RelationViewModel
import com.example.mobilecomputing.ViewModelFactory.PostReactionFactory
import com.example.mobilecomputing.ViewModelFactory.RelationFactory
import com.example.mobilecomputing.entity.PostReactionEntity

@Composable
fun Blog(post: PostWithUser, navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val idFromPrefs = sessionManager.getUserId()

    val database = AppDatabase.getInstance(context)
    val postReactionFactory = PostReactionFactory(database.postReactionDAO())
    val postReactionViewModel: PostReactionViewModel = viewModel(factory = postReactionFactory)
    postReactionViewModel.setUserId(idFromPrefs)
    val reactions by postReactionViewModel.reactions.collectAsState()
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
                    val postReaction = PostReactionEntity(userId = idFromPrefs, postId = post.post.id)
                    postReactionViewModel.deleteReaction(postReaction)
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
                Text("Comments", color = Color.Black)
            }
        }
    }
}