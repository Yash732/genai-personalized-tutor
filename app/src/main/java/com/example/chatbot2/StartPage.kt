package com.example.chatbot2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign

@Composable
fun OptionsCard(
    modifier: Modifier = Modifier,
    title: String,
    preferences: String,
    description: String,
    isSelected: Boolean,
    onClick: (String) -> Unit // Changed this to accept only the title
) {
    Button(
        onClick = { onClick(title) }, // Corrected to pass the title to onClick
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xDAF3B235) else Color(0xFF04013D)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with your image resource
                    contentDescription = "$title Icon",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text(
                        text = preferences,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = description,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsSelected(
    selectedSubject: String,
    onSubjectChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    selectedCard: String,
    onCardChange: (String) -> Unit,
    onStart: (String, Int) -> Unit
) {
    val subjects = listOf("Coding", "Math", "Environmental Science", "Physics")

    Column(
        modifier = Modifier
            .background(Color(0xFF220055))
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        OptionsCard(
            modifier = Modifier,
            title = "Creative Mode",
            preferences = "Open-ended Learning",
            description = "Provides more story-like and abstract approach to teach",
            isSelected = selectedCard == "Creative Mode",
            onClick = { onCardChange(it) } // Updated to call onCardChange correctly
        )
        OptionsCard(
            modifier = Modifier,
            title = "Facts Based",
            preferences = "Knowledge-Oriented",
            description = "Focuses on delivering factual information with clear explanations",
            isSelected = selectedCard == "Facts Based",
            onClick = { onCardChange(it) }
        )
        OptionsCard(
            modifier = Modifier,
            title = "Formula Based",
            preferences = "Step-by-Step Learning",
            description = "Emphasizes structured formulas and rules to solve problems",
            isSelected = selectedCard == "Formula Based",
            onClick = { onCardChange(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown for selecting a subject
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedSubject,
                onValueChange = { onSubjectChange(it) },

                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Gray,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF220055),
                    unfocusedContainerColor = Color(0xFF220055)
                ),
                label = {Text("Select Subject")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                subjects.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSubjectChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Input for age
        OutlinedTextField(
            value = age,
            onValueChange = onAgeChange,
            label = { Text("Enter Your Age") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF220055),
                unfocusedContainerColor = Color(0xFF220055)
            )
        )

        // Start button
        Button(
            onClick = {
                onStart(selectedSubject, age.toIntOrNull() ?: 0)
            },
            colors = ButtonDefaults.buttonColors(Color(0xDAF3B235)),
            modifier = Modifier.padding(top = 18.dp).align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Start", color = Color.White)
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun PreviewOptionsSelected() {
//    // Provide sample data and dummy callbacks for preview
//    OptionsSelected(
//        selectedSubject = "Math", // Default preview subject
//        onSubjectChange = {}, // Dummy callback
//        age = "25", // Default preview age
//        onAgeChange = {}, // Dummy callback
//        onStart = { _, _ -> } // Dummy callback for the start button
//    )
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartPage(onStart: (String, String, Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AppHeader()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            var selectedSubject by remember { mutableStateOf("") }
            var age by remember { mutableStateOf("") }
            var selectedCard by remember { mutableStateOf("") }

            // Update onStart to call with the appropriate arguments
            OptionsSelected(
                selectedSubject = selectedSubject,
                onSubjectChange = { selectedSubject = it },
                age = age,
                onAgeChange = { age = it },
                selectedCard = selectedCard,
                onCardChange = { selectedCard = it },
                onStart = { subject, ageInt -> // Match the expected signature
                    onStart(selectedCard, subject, ageInt) // Pass the selectedCard along with subject and age
                }
            )
        }
    }
}