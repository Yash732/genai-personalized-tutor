package com.example.chatbot2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatbot2.ui.theme.ColorModelMessage
import com.example.chatbot2.ui.theme.ColorUserMessage
import com.example.chatbot2.ui.theme.PurpleGrey80

@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
    selectedCard: String, // Passed to view model
    subject: String,
    age: Int
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AppHeader()
        MessageList(
            modifier = Modifier.weight(1f),
            messageList = viewModel.messageList
        )
        MessageInput(onMessageSend = { message, image ->
            viewModel.sendMessage(message, image, selectedCard, subject, age) // Pass selected card, subject, and age
        })
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList : List<MessageModel>){
    if(messageList.isEmpty()){
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.baseline_chat_24),
                contentDescription = "Icon",
                tint = PurpleGrey80
            )
            Text(text = "What would you like to learn today? ", fontSize = 22.sp )
        }
    }
    LazyColumn(
        modifier = modifier,
        //used as by default messages will be displayed from top
        reverseLayout = true
    ) {
        items(messageList.reversed()){
            MessageRow(messageModel = it)
        }
    }

}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            Box(
                modifier = Modifier.align(
                    if(isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if(isModel) 8.dp else 70.dp,
                        end = if(isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if(isModel) ColorModelMessage else ColorUserMessage)
                    .padding(16.dp)

            ){
                SelectionContainer {
                    Text(
                        text = messageModel.message,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )
                }

            }
        }
    }
}

@Composable
fun MessageInput(onMessageSend: (String, Bitmap?) -> Unit) {
    var message by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current

    // Launcher for picking images from the device's storage
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            selectedImage = BitmapFactory.decodeStream(inputStream) // Decode and set the selected image
        }
    }

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Input text field for the message
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = { message = it },
            label = { Text("Type a message") }, // Adding a label for clarity
            placeholder = { Text("Enter message...") }
        )

        // Button to launch the image picker
        IconButton(
            onClick = { launcher.launch("image/*") }, // Trigger image selection
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Column(){
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Upload Image",
                    tint = if(selectedImage != null) Color.Green else Color.Gray
                )
            }

        }

        // Button to send the message and selected image
        IconButton(
            onClick = {
                if (message.isNotEmpty() || selectedImage != null) {
                    onMessageSend(message, selectedImage)
                    message = "" // Clear the input field
                    selectedImage = null // Reset the selected image after sending
                }
            },
            enabled = message.isNotEmpty() || selectedImage != null // Enable only if message or image is present
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
        }
    }
}


@Composable
fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xDAF3B235))
    ){
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Personalized Tutor",
            fontSize = 22.sp,
            color = Color.White
        )
    }
}

