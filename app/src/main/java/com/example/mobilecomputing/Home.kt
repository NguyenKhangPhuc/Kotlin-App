package com.example.mobilecomputing

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
import androidx.compose.material3.TextField
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.io.File
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilecomputing.DAO.UserProfileDao
import com.example.mobilecomputing.ViewModel.UserProfileViewModel
import com.example.mobilecomputing.ViewModelFactory.UserProfileViewModelFactory
import com.example.mobilecomputing.entity.UserProfileEntity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build

import androidx.core.content.ContextCompat


fun copyImageToInternalStorage(
    context: Context,
    uri: Uri
): String {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("Cannot open input stream")

    val fileName = "avatar.jpg"
    val outputFile = File(context.filesDir, fileName)

    inputStream.use { input ->
        outputFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return outputFile.absolutePath
}

@Composable
fun Home(){
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val factory = UserProfileViewModelFactory(database.userProfileDAO())
    val viewModel: UserProfileViewModel = viewModel(factory = factory)
    val userProfile by viewModel.userProfile.collectAsState()
    if (userProfile == null) {
        MainScreen(UserProfileEntity(0, "Unknown name", ""), viewModel)
    } else {
       MainScreen(userProfile!!, viewModel)
    }
}


@Composable
fun LoadingScreen(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Loading...")
        CircularProgressIndicator()
    }
}

@Composable
fun MainScreen(userProfile: UserProfileEntity, viewModel: UserProfileViewModel){
    val context = LocalContext.current
    var username by remember { mutableStateOf<String>(userProfile.username ?: "") }
    var savedImagePath by remember { mutableStateOf<String?>(userProfile.imagePath ?: "") }
    println(" $userProfile")
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val notificationHelper = NotificationHelper(context)
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }

    }
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            imagePicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text("Pick Image")
        }

        val file = savedImagePath?.let { File(it) }

        val imageModel = when {
            selectedImageUri != null -> selectedImageUri
            file != null && file.exists() -> file
            else -> null
        }

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            imageModel?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }


        Text("Send a message")

        TextField(
            value = username,
            onValueChange = { newValue -> username = newValue },
            placeholder = { Text("Type your username...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (selectedImageUri != null){
                val path = copyImageToInternalStorage(context, selectedImageUri!!)
                viewModel.saveProfile(UserProfileEntity(id = 0, username, imagePath = path))
                println("Saving")
            }else if (savedImagePath != null) {
                viewModel.saveProfile(UserProfileEntity(id = 0, username, imagePath = savedImagePath))
                println("Saving")
            }else {
                println("Choose an imageee")
            }
        }) {
            Text("Save")
        }

        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                notificationHelper.showNotification("Accessability", "Permission granted",null)
            } else {

            }
        }

        Button(onClick = {
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
        }) {
            Text("Enable button")
        }
    }
}