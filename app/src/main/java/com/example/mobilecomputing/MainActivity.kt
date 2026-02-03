package com.example.mobilecomputing

import SampleData
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextField
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

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
fun BottomNavigationBar(navController: NavController){
    val items = listOf(Screen.Home, Screen.Chat, Screen.Setting)
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach {item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {Text(item.label)}
            )
        }
    }
}

@Composable
fun MessageCard(msg: Message, modifier: Modifier = Modifier) {
    Row (modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(id = R.drawable.profile_picture),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }
        Column (modifier = Modifier.clickable { isExpanded = !isExpanded }){
            Text(
                text = msg.author,
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
                modifier = Modifier.animateContentSize().padding(1.dp)
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
fun Home(){
    var message by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Send a message")

        TextField(
            value = message,
            onValueChange = { newValue -> message = newValue },
            placeholder = { Text("Type your message...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            // Xử lý gửi message
            println("Message sent: $message")
        }) {
            Text("Send")
        }
    }
}


@Composable
fun Conversation(messages: List<Message>) {
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