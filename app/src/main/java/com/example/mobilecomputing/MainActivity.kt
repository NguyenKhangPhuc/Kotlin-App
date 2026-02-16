package com.example.mobilecomputing

import GyroSensor
import NotificationHelper
import SampleData
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.compose.foundation.border
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.mobilecomputing.ui.theme.MobileComputingTheme
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.mobilecomputing.ViewModel.UserProfileViewModel
import com.example.mobilecomputing.ViewModelFactory.UserProfileViewModelFactory
import androidx.compose.runtime.DisposableEffect
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            App()
        }
    }
}

data class Message(val author: String, val body: String)
sealed class Screen(val route: String, val label: String, val icon: ImageVector){
    object Chat : Screen("conservation", "Chat", Icons.Default.Email)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Setting: Screen("setting", "Setting", Icons.Default.Settings)
}

@Composable
fun App(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val notificationHelper = NotificationHelper(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context , Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            println("grant permission")
            notificationHelper.showNotification("Accessability", "Automatic", context as Activity)
        }
    }else {
        println("grant permission 2")
        notificationHelper.showNotification("Accessability", "Automatic", context as Activity)
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
        innerPadding ->
        NavHost(
            navController=navController,
            startDestination = Screen.Chat.route,
            modifier = Modifier.padding(innerPadding)
        ){
           composable(Screen.Chat.route){
               Conversation(SampleData.conversationSample)
           }
            composable(Screen.Home.route){
                Home()
            }
            composable(Screen.Setting.route){
                Text("Setting page")
            }
        }
    }
}


@Composable
fun MessageCard(msg: Message, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val factory = UserProfileViewModelFactory(database.userProfileDAO())
    val viewModel: UserProfileViewModel = viewModel(factory = factory)
    val userProfile by viewModel.userProfile.collectAsState()
    val imagePath = userProfile?.imagePath
    val username = userProfile?.username
    Row (modifier = Modifier.padding(all = 8.dp)) {
        imagePath.let {

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary)
                    )
                }
        }
        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }
        Column (modifier = Modifier.clickable { isExpanded = !isExpanded }){
            Text(
                text = username ?: "Unknown name",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            val surfaceTextBodyColor by animateColorAsState(
                if (isExpanded) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.5.dp,
                color=surfaceTextBodyColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1
                    )
            }
        }
    }
}


@Composable
fun Conversation(messages: List<Message>) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val gyroManager = remember { GyroSensor(sensorManager,context) }


    DisposableEffect(Unit) {
        gyroManager.start()
        onDispose { gyroManager.stop() }
    }
    LazyColumn {
        items(messages) { message -> MessageCard(message) }
    }
}

@Preview(showBackground = true)
@Composable
fun MessagePreview() {
    MobileComputingTheme {
        Conversation(SampleData.conversationSample)
    }
}