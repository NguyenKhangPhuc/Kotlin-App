package com.example.mobilecomputing.Home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.mobilecomputing.ViewModel.PostViewModel
import com.example.mobilecomputing.entity.PostEntity
import com.example.mobilecomputing.entity.UserProfileEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@Composable
fun PostCreationContent(
    onPost: () -> Unit,
    userProfile: UserProfileEntity,
    postViewModel: PostViewModel
) {
    val context = LocalContext.current
    var textContent by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }
    val tmpUri = remember {
        val file = File(context.cacheDir, "tmp_image_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tmpUri // Cập nhật URI để hiển thị ảnh vừa chụp lên Box
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(tmpUri)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        Text("Create Post", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập nội dung bài viết
        OutlinedTextField(
            value = textContent,
            onValueChange = { textContent = it },
            placeholder = { Text("What are you thinking") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        )

        Row(modifier = Modifier.padding(vertical = 16.dp)) {
            IconButton(onClick = {
                imagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Icon(Icons.Default.Add, contentDescription = "Gallery")
            }

            IconButton(onClick = {when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {

                    cameraLauncher.launch(tmpUri)
                }
                else -> {

                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }}) {
                Icon(Icons.Default.Create, contentDescription = "Camera")
            }
        }
        if (selectedImageUri != null) {
            println("Taken image -- ${selectedImageUri}")

            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxWidth().height(500.dp),
                contentScale = ContentScale.Crop
            )
        }

        Button(
            onClick = {
                var path = ""
                if (selectedImageUri != null){
                    path = copyImageToInternalStorage(context, selectedImageUri!!)
                }
                postViewModel.createPost(PostEntity(userId = userProfile.id, content = textContent, imagePath = path, audioPath = "", createdAt = getCurrentFormattedDateTime()))
                onPost() },
            modifier = Modifier.fillMaxWidth(),
            enabled = textContent.isNotBlank()
        ) {
            Text("Create post")
        }
    }
}

@Composable
fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

fun getCurrentFormattedDateTime(): String {

    val sdf = SimpleDateFormat("MMMM d, yyyy • h:mm a", Locale.ENGLISH)
    return sdf.format(Date())
}