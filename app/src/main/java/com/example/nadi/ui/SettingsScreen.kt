package com.example.nadi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NadiViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (uiState.isDarkMode) Color(0xFF07111F) else Color.White)
            .padding(16.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "SETTINGS",
                    color = if (uiState.isDarkMode) Color(0xFFE8B85C) else Color(0xFF07111F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = if (uiState.isDarkMode) Color.White else Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingsItem(
            title = "Dark Mode",
            control = {
                Switch(
                    checked = uiState.isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFE8B85C),
                        checkedTrackColor = Color(0xFF101D2D)
                    )
                )
            },
            isDarkMode = uiState.isDarkMode
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray.copy(alpha = 0.3f))

        Text(
            text = "Master Volume",
            color = if (uiState.isDarkMode) Color.White else Color.Black,
            fontSize = 18.sp
        )
        
        Slider(
            value = uiState.volume,
            onValueChange = { viewModel.setVolume(it) },
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFE8B85C),
                activeTrackColor = Color(0xFFE8B85C),
                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray.copy(alpha = 0.3f))

        Text(
            text = "Shruti Range (Coming Soon)",
            color = if (uiState.isDarkMode) Color.LightGray else Color.DarkGray,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Default Raga (Coming Soon)",
            color = if (uiState.isDarkMode) Color.LightGray else Color.DarkGray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    control: @Composable () -> Unit,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = if (isDarkMode) Color.White else Color.Black,
            fontSize = 18.sp
        )
        control()
    }
}
