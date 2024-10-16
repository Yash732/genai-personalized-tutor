package com.example.chatbot2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chatbot2.ui.theme.Chatbot2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create the view model instance
        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        setContent {
            Chatbot2Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "start_page") {

                    // Start Page Composable
                    composable("start_page") {
                        StartPage { selectedCard, subject, age ->
                            // Navigate to ChatPage with arguments
                            navController.navigate("chat_page/$selectedCard/$subject/$age")
                        }
                    }
                    composable(
                        "chat_page/{selectedCard}/{subject}/{age}",
                        arguments = listOf(
                            navArgument("selectedCard") { type = NavType.StringType },
                            navArgument("subject") { type = NavType.StringType },
                            navArgument("age") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val selectedCard = backStackEntry.arguments?.getString("selectedCard") ?: ""
                        val subject = backStackEntry.arguments?.getString("subject") ?: ""
                        val age = backStackEntry.arguments?.getInt("age") ?: 0

                        ChatPage(
                            modifier = Modifier,
                            viewModel = chatViewModel,
                            selectedCard = selectedCard,
                            subject = subject,
                            age = age
                        )
                    }
                }
            }
        }
    }
}
