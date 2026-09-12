package com.example.nadi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NadiApp()
        }
    }
}

@Composable
fun NadiApp() {

    MaterialTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF07111F)
        ) {

            HomeScreen()
        }
    }
}



@Composable
fun HomeScreen() {

    var selectedRaga by remember {
        mutableStateOf("Hamsadhwani")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07111F))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        TopBar()

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ShrutiCard(
                modifier = Modifier.weight(1f)
            )

            TanpuraCard(
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        RagaCard(
            selectedRaga = selectedRaga,
            onRagaChange = { selectedRaga = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SwaraSection()
    }
}

@Composable
fun TopBar() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = {}
        ) {

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "NĀDI",
                color = Color(0xFFE8B85C),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "RAGA & SHRUTI",
                color = Color.LightGray,
                fontSize = 10.sp,
                letterSpacing = 3.sp
            )
        }

        Row {

            IconButton(
                onClick = {}
            ) {

                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Theme",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {}
            ) {

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ShrutiCard(
    modifier: Modifier = Modifier
) {

    var shrutiFrequency by remember {
        mutableStateOf(240.0)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF101D2D)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "SHRUTI (SA)",
                color = Color(0xFFE8B85C),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = String.format("%.1f Hz", shrutiFrequency),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                Button(
                    onClick = {
                        if (shrutiFrequency > 100.0) {
                            shrutiFrequency -= 1.0
                        }
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Text("-")
                }

                Button(
                    onClick = {
                        if (shrutiFrequency < 500.0) {
                            shrutiFrequency += 1.0
                        }
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "C#3",
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun TanpuraCard(
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF101D2D)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "TANPURA",
                color = Color(0xFFE8B85C),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sa - Pa - Sa",
                color = Color.LightGray,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "〰〰〰〰〰〰〰",
                color = Color(0xFFE8B85C),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {},
                shape = RoundedCornerShape(50)
            ) {

                Text(
                    text = "▶"
                )
            }
        }
    }
}

@Composable
fun RagaCard(
    selectedRaga: String,
    onRagaChange: (String) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF101D2D)
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "SELECT RAGA",
                color = Color(0xFFE8B85C),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Raga selector will be implemented later
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = selectedRaga,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "AROHANAM",
                        color = Color(0xFFE8B85C),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "S R₂ G₃ P N₃ S",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "AVAROHANAM",
                        color = Color(0xFFE8B85C),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "S N₃ P G₃ R₂ S",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SwaraSection() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF101D2D)
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "SWARAS",
                color = Color(0xFFE8B85C),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                SwaraButton("S", "Sa")
                SwaraButton("R₂", "Ri")
                SwaraButton("G₃", "Ga")
                SwaraButton("P", "Pa")
                SwaraButton("N₃", "Ni")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tap any swara to play as shruti",
                modifier = Modifier.fillMaxWidth(),
                color = Color.LightGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun SwaraButton(
    notation: String,
    name: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = {
                // Audio will be added here
            },
            modifier = Modifier.size(65.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF172638)
            ),
            contentPadding = PaddingValues(0.dp)
        ) {

            Text(
                text = notation,
                fontSize = 18.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = name,
            color = Color.LightGray,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NadiAppPreview() {
    NadiApp()
}