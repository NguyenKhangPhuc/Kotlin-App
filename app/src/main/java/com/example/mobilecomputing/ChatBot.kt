import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilecomputing.ChatRepository
import com.example.mobilecomputing.ChatResponse
import com.example.mobilecomputing.SessionManager
import com.example.mobilecomputing.ViewModel.ChatViewModel
import com.example.mobilecomputing.ViewModelFactory.ChatViewModelFactory
import com.example.mobilecomputing.entity.UserProfileEntity

@Composable
fun ChatBotScreen(
    navController: NavController,
    userProfile: UserProfileEntity
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId().toString()

    val repository = remember { ChatRepository() }
    val factory = ChatViewModelFactory(repository)
    val chatViewModel: ChatViewModel = viewModel(factory = factory)

    var chatMessages by remember { mutableStateOf(listOf<ChatResponse>()) }
    var inputText by remember { mutableStateOf("") }

    val username = userProfile.username ?: "User"

    val reply by chatViewModel.chatReply.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(reply) {
        reply?.let {
            chatMessages = chatMessages + ChatResponse(it, "AI")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { item ->
                ChatBubble(item, username)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Hỏi chatbot...") },
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val currentInput = inputText
                        // 1. Add tin nhắn của người dùng vào list ngay lập tức
                        chatMessages = chatMessages + ChatResponse(currentInput, "user")
                        // 2. Gọi API thông qua ViewModel
                        chatViewModel.sendChat(currentInput, userId)
                        // 3. Xóa ô input
                        inputText = ""
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier.background(Color(0xFFFF69B4), CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun ChatBubble(item: ChatResponse, username: String) {
    val isAI = item.type == "AI"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isAI) Alignment.Start else Alignment.End
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isAI) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Gray),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Chatbot", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            } else {
                Text(username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFF69B4)),
                    tint = Color.White
                )
            }
        }

        Surface(
            color = if (isAI) Color(0xFFF1F1F1) else Color(0xFFFF69B4),
            contentColor = if (isAI) Color.Black else Color.White,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(top = 4.dp, start = if (isAI) 48.dp else 0.dp, end = if (isAI) 0.dp else 48.dp)
        ) {
            Text(
                text = item.message,
                modifier = Modifier.padding(12.dp),
                fontSize = 16.sp
            )
        }
    }
}