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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilecomputing.entity.UserProfileEntity

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
    object Login : Screen("login", "Login", Icons.Default.Info)
    object SignUp : Screen("signup", "Signup", Icons.Default.Info)
}

@Composable
fun App(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val notificationHelper = NotificationHelper(context)
    val sessionManager = remember { SessionManager(context) }

    // Kiểm tra ngay giá trị từ SharedPreferences khi khởi tạo State
    var isLoggedIn by remember {
        mutableStateOf(sessionManager.getUserId() != -1)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
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
            if (currentRoute != Screen.Login.route && currentRoute != Screen.SignUp.route) {
                BottomNavigationBar(navController)
            }
        }
    ) {
        innerPadding ->
        NavHost(
            navController=navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
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
            composable(Screen.Login.route) {
                LoginScreen(
                   navController,
                    sessionManager,
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(onNavigateToLogin = { navController.navigate(Screen.Login.route) })
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

@Composable
fun LoginScreen(navController: NavController, sessionManager: SessionManager) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val database = AppDatabase.getInstance(context)
    val factory = UserProfileViewModelFactory(database.userProfileDAO())
    val viewModel: UserProfileViewModel = viewModel(factory = factory)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "KPAPP",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Text(
            text = "Login and Enjoy the application",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 32.dp)
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.login(emailInput = email, passwordInput = password, sessionManager = sessionManager, onResult = { success ->
                    if (success) {
                        navController.navigate(Screen.Chat.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {

                        println("Đăng nhập thất bại!")
                    }
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account? ")
            TextButton(onClick = {
                navController.navigate(Screen.Chat.route) {
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }
            }) {
                Text(
                    text = "Sign In",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SignUpScreen(onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val database = AppDatabase.getInstance(context)
    val factory = UserProfileViewModelFactory(database.userProfileDAO())
    val viewModel: UserProfileViewModel = viewModel(factory = factory)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "KPAPP",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Text(
            text = "Create your account",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 32.dp)
        )


        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.signup(UserProfileEntity(username = username, email = email, password = password, imagePath = null))
                onNavigateToLogin()
                      },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account? ")
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Sign In",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}