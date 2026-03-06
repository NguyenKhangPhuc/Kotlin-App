package com.example.mobilecomputing.Home

import NotificationHelper
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilecomputing.ViewModel.UserProfileViewModel
import com.example.mobilecomputing.ViewModelFactory.UserProfileViewModelFactory
import com.example.mobilecomputing.entity.UserProfileEntity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.ui.layout.ContentScale

import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.mobilecomputing.AppDatabase
import com.example.mobilecomputing.SessionManager
import com.example.mobilecomputing.ViewModel.PostViewModel
import com.example.mobilecomputing.ViewModel.RelationViewModel
import com.example.mobilecomputing.ViewModelFactory.PostFactory
import com.example.mobilecomputing.ViewModelFactory.RelationFactory
import com.example.mobilecomputing.entity.PostEntity
import com.example.mobilecomputing.entity.PostWithUser

fun copyImageToInternalStorage(
    context: Context,
    uri: Uri
): String {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("Cannot open input stream")

    val fileName = "img_${System.currentTimeMillis()}.jpg"
    val outputFile = File(context.filesDir, fileName)

    inputStream.use { input ->
        outputFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return outputFile.absolutePath
}


@Composable
fun Home(navController: NavController){
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val factory = UserProfileViewModelFactory(database.userProfileDAO())
    val viewModel: UserProfileViewModel = viewModel(factory = factory)
    val sessionManager = SessionManager(context)
    val idFromPrefs = sessionManager.getUserId()
    println("Id ---- ${idFromPrefs}")
    if (idFromPrefs != -1) {
        viewModel.setUserId(idFromPrefs)
    }
    val userProfile by viewModel.userProfile.collectAsState()
    if (userProfile == null) {
        LoadingScreen()
    } else {
       MainScreen(userProfile!!, viewModel, navController)
    }
}


@Composable
fun LoadingScreen(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Loading...")
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(userProfile: UserProfileEntity, viewModel: UserProfileViewModel, navController: NavController){
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val factory = PostFactory(database.postDAO())
    val postViewModel: PostViewModel = viewModel(factory = factory)
    postViewModel.setUserId(userProfile.id)
    val userPosts by postViewModel.userPosts.collectAsState()

    val relationFactory = RelationFactory(database.relationDAO())
    val relationViewModel: RelationViewModel = viewModel(factory = relationFactory)
    relationViewModel.setUserId(userProfile.id)
    val followers by relationViewModel.userFollowers.collectAsState()
    val followings by relationViewModel.userFollowings.collectAsState()
    var username by remember { mutableStateOf<String>(userProfile.username ?: "") }
    var savedImagePath by remember { mutableStateOf<String?>(userProfile.imagePath ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val notificationHelper = NotificationHelper(context)

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            val path = copyImageToInternalStorage(context,uri);
            viewModel.updateImagePath(userId = userProfile.id, path)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        ) {

        val file = savedImagePath?.let { File(it) }

        val imageModel = when {
            selectedImageUri != null -> selectedImageUri
            file != null && file.exists() -> file
            else -> null
        }

        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                notificationHelper.showNotification("Accessability", "Permission granted",null)
            } else {

            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = username, // Username
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(context , Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            println("ASK permission")
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            println("grant permission")
                            notificationHelper.showNotification("Accessability", "Permission granted",null)
                        }
                    }else {
                        println("grant permission 2")
                        notificationHelper.showNotification("Accessability", "Permission granted",null)
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable {
                        imagePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },

                contentAlignment = Alignment.Center
            ) {
                if (imageModel != null) {
                    println("Taken image -- ${imageModel}")

                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            }

            StatItem(label = "Posts", count = userPosts?.size ?: 0)
            StatItem(label = "Followers", count = followers?.size ?: 0)
            StatItem(label = "Following", count = followings?.size ?: 0)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("All the posts", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showBottomSheet = true } // Click vào cả hàng để mở BottomSheet
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable {
                        imagePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },

                contentAlignment = Alignment.Center
            ) {
                if (imageModel != null) {
                    println("Taken image -- ${imageModel}")

                    AsyncImage(
                        model = imageModel,
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


            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .height(40.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(start = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("What's on your mind?", color = Color.Gray)
            }

            Icon(Icons.Default.Add, contentDescription = "Create", tint = MaterialTheme.colorScheme.primary)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)


        Spacer(modifier = Modifier.height(8.dp))

        println("This is username above ${userProfile}")
        Posts(userPosts, navController, currentUserProfile = userProfile)

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                // Gọi Composable nội dung ở đây
                PostCreationContent(
                    onPost = {  ->
                        showBottomSheet = false
                    },
                    userProfile,
                    postViewModel,
                )
            }
        }
    }
}




