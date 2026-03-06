package com.example.mobilecomputing.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilecomputing.entity.PostWithUser
import com.example.mobilecomputing.entity.UserProfileEntity

@Composable
fun Posts(posts: List<PostWithUser>?, navController: NavController, currentUserProfile: UserProfileEntity) {
    val context = LocalContext.current

    if ( !posts.isNullOrEmpty()){
        LazyColumn (verticalArrangement = Arrangement.spacedBy(12.dp),){
            itemsIndexed(posts) {
                index, post -> Blog(post, navController, currentUserProfile)
                if (index < posts.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        thickness = 0.5.dp,
                        color = Color.Black
                    )
                }
            }

        }

    }else {
        Text("Upload something")
    }
}
