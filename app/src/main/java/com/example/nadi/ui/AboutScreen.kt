package com.example.nadi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    viewModel: NadiViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode = uiState.isDarkMode

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Nādi", color = Color(0xFFE8B85C)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkMode) Color.White else Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkMode) Color(0xFF07111F) else Color.White
                )
            )
        },
        containerColor = if (isDarkMode) Color(0xFF07111F) else Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "NĀDI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE8B85C)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Raga & Shruti App",
                fontSize = 18.sp,
                color = if (isDarkMode) Color.LightGray else Color.DarkGray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "A simple tool for Indian Classical Music practitioners to explore Ragas and practice with a Tanpura.",
                textAlign = TextAlign.Center,
                color = if (isDarkMode) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Version 1.0.0",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
