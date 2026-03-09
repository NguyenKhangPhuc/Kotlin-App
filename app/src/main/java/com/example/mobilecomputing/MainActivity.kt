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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.mobilecomputing.Home.Home
import com.example.mobilecomputing.Home.Posts
import com.example.mobilecomputing.Home.UserProfileHome
import com.example.mobilecomputing.Home.UserProfileMainScreen
import com.example.mobilecomputing.ViewModel.PostViewModel
import com.example.mobilecomputing.ViewModelFactory.PostFactory
import com.example.mobilecomputing.entity.UserProfileEntity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        var keepSplashScreen by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { false }
        lifecycleScope.launch {
            delay(2000)
            keepSplashScreen = false
        }
        enableEdgeToEdge()

        setContent {
            if (keepSplashScreen) {
                SplashScreenUI()
            } else {
                App()
            }
        }
    }
}

@Composable
fun SplashScreenUI() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "K P A P P",
                style = TextStyle(
                    color = Color(0xFFFF69B4),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 10.sp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = Color(0xFFFF69B4),
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

data class Message(val author: String, val body: String)
sealed class Screen(val route: String, val label: String, val icon: ImageVector){
    object Chat : Screen("conservation", "Blogs", Icons.Default.Email)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Setting: Screen("setting", "Setting", Icons.Default.Settings)
    object Login : Screen("login", "Login", Icons.Default.Info)
    object SignUp : Screen("signup", "Signup", Icons.Default.Info)
    object UserProfile : Screen("user/{userId}", "Profile", Icons.Default.Person) {
        fun createRoute(userId: Int) = "user/$userId"
    }
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
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val gyroManager = remember { GyroSensor(sensorManager,context) }


    DisposableEffect(Unit) {
        gyroManager.start()
        onDispose { gyroManager.stop() }
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
               AllBlogMainScreen(navController)
           }
            composable(Screen.Home.route){
                Home(navController)
            }
            composable(Screen.Setting.route){
                SettingPage(navController)
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
            composable(
                route = Screen.UserProfile.route,
                arguments = listOf(navArgument("userId") { type = NavType.IntType }) // Khai báo kiểu Int
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                UserProfileHome(userId = userId, navController)
            }
        }
    }
}


@Composable
fun AllBlogMainScreen( navController: NavController,modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val factory = PostFactory(database.postDAO())
    val postViewModel: PostViewModel = viewModel(factory = factory)
    val allPosts by postViewModel.allPosts.collectAsState()

    val userFactory = UserProfileViewModelFactory(database.userProfileDAO())
    val viewModel: UserProfileViewModel = viewModel(factory = userFactory)
    val sessionManager = SessionManager(context)
    val idFromPrefs = sessionManager.getUserId()
    if (idFromPrefs != -1) {
        viewModel.setUserId(idFromPrefs)
    }
    val userProfile by viewModel.userProfile.collectAsState()

    if (allPosts.isNullOrEmpty()){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No posts from other users")
        }
    } else if (userProfile == null){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading...")
            CircularProgressIndicator()
        }
    }else {
        Column (modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)){
            Posts(allPosts, navController, currentUserProfile = userProfile!!)
        }
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
                viewModel.login(
                    emailInput = email,
                    passwordInput = password,
                    sessionManager = sessionManager,
                    onResult = { success ->
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
                navController.navigate(Screen.SignUp.route) {
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }
            }) {
                Text(
                    text = "Sign up",
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