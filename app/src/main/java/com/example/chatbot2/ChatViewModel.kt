package com.example.chatbot2

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = Constants.apiKey,
        generationConfig = generationConfig {
            temperature = 1f
            responseMimeType = "text/plain"
        }
    )

    fun sendMessage(question: String, bitmap: Bitmap?, selectedCard: String, subject: String, age: Int) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }.toList()
                )

                // Customize the prompt based on selected mode
                val modeDescription = when (selectedCard) {
                    "Creative Mode" -> "a creative and open-ended teaching approach"
                    "Facts Based" -> "a fact-based and knowledge-oriented approach"
                    "Formula Based" -> "a structured, formula-based learning approach"
                    else -> "a general teaching approach"
                }

                // Create the personalized content
                val personalizedPrompt =
                    "You are a tutor for a $age-year-old student focusing on $subject. Use $modeDescription to explain the following in brief: $question."

                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Generating Output...", "model"))

                val response = chat.sendMessage(
                    content {
                        bitmap?.let { image(it) } // Send image if not null
                        text(personalizedPrompt) // Send the personalized prompt
                    }
                )

                messageList.removeLast() // Remove "Generating Output..."
                messageList.add(MessageModel(response.text.toString(), "model"))
            } catch (e: Exception) {
                messageList.removeLast()
                messageList.add(MessageModel("Error: " + e.message.toString(), "model"))
            }
        }
    }
}
