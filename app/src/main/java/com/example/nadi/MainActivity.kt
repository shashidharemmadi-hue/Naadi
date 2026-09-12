package com.example.nadi

import android.os.Bundle
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import android.widget.Toast
import com.example.nadi.data.SettingsManager
import com.example.nadi.model.Raga
import com.example.nadi.model.Swara
import com.example.nadi.ui.AboutScreen
import com.example.nadi.ui.NadiViewModel
import com.example.nadi.ui.SettingsScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsManager = SettingsManager(applicationContext)
        val viewModel = ViewModelProvider(
            this,
            NadiViewModelFactory(settingsManager)
        ).get(NadiViewModel::class.java)

        setContent {
            NadiApp(viewModel)
        }
    }
}

class NadiViewModelFactory(private val settingsManager: SettingsManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NadiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NadiViewModel(settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class Screen { Home, Settings, About }

@Composable
fun NadiApp(viewModel: NadiViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    var openRagaSelectorTrigger by remember { mutableStateOf(false) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Error handling
    LaunchedEffect(viewModel.errorEvents) {
        viewModel.errorEvents.collect { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    // Lifecycle handling - stop audio on background if needed
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                // If we want to strictly follow "lifecycle safety", stop everything.
                // However, often Tanpura apps are meant to play in background.
                // The user asked to ensure audio is stopped.
                if (uiState.isTanpuraPlaying) {
                    viewModel.toggleTanpura()
                }
                viewModel.stopPlayback()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    MaterialTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    selectedScreen = currentScreen,
                    onNavigate = { screen ->
                        currentScreen = screen
                        if (screen == Screen.Home) {
                            openRagaSelectorTrigger = false
                        }
                    },
                    onRagasClick = {
                        currentScreen = Screen.Home
                        // Force trigger by toggling if already true, though it should be false usually
                        openRagaSelectorTrigger = true
                    },
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = if (uiState.isDarkMode) Color(0xFF07111F) else Color.White
            ) {
                when (currentScreen) {
                    Screen.Home -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onSettingsClick = { currentScreen = Screen.Settings },
                            showRagaSelectorInitially = openRagaSelectorTrigger
                        )
                        
                        LaunchedEffect(openRagaSelectorTrigger) {
                            if (openRagaSelectorTrigger) {
                                // Reset after it has been used by HomeScreen
                                openRagaSelectorTrigger = false
                            }
                        }
                    }
                    Screen.Settings -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.Home }
                        )
                    }
                    Screen.About -> {
                        AboutScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.Home }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    selectedScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onRagasClick: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF101D2D),
        drawerContentColor = Color.White
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            "NĀDI",
            modifier = Modifier.padding(16.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE8B85C)
        )
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

        NavigationDrawerItem(
            label = { Text("Home") },
            selected = selectedScreen == Screen.Home,
            onClick = {
                onNavigate(Screen.Home)
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFE8B85C).copy(alpha = 0.1f),
                selectedTextColor = Color(0xFFE8B85C),
                selectedIconColor = Color(0xFFE8B85C),
                unselectedTextColor = Color.White,
                unselectedIconColor = Color.White
            )
        )
        NavigationDrawerItem(
            label = { Text("Ragas") },
            selected = false,
            onClick = {
                onRagasClick()
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.MusicNote, contentDescription = null) },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedTextColor = Color.White,
                unselectedIconColor = Color.White
            )
        )
        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = selectedScreen == Screen.Settings,
            onClick = {
                onNavigate(Screen.Settings)
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFE8B85C).copy(alpha = 0.1f),
                selectedTextColor = Color(0xFFE8B85C),
                selectedIconColor = Color(0xFFE8B85C),
                unselectedTextColor = Color.White,
                unselectedIconColor = Color.White
            )
        )
        NavigationDrawerItem(
            label = { Text("About") },
            selected = selectedScreen == Screen.About,
            onClick = {
                onNavigate(Screen.About)
                onCloseDrawer()
            },
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFE8B85C).copy(alpha = 0.1f),
                selectedTextColor = Color(0xFFE8B85C),
                selectedIconColor = Color(0xFFE8B85C),
                unselectedTextColor = Color.White,
                unselectedIconColor = Color.White
            )
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NadiViewModel,
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit,
    showRagaSelectorInitially: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRagaSelector by remember { mutableStateOf(showRagaSelectorInitially) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(showRagaSelectorInitially) {
        if (showRagaSelectorInitially) {
            showRagaSelector = true
        }
    }

    if (showRagaSelector) {
        ModalBottomSheet(
            onDismissRequest = { showRagaSelector = false },
            sheetState = sheetState,
            containerColor = Color(0xFF101D2D),
            contentColor = Color.White
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                items(viewModel.allRagas) { raga ->
                    ListItem(
                        headlineContent = { 
                            Text(
                                text = raga.name,
                                color = if (uiState.selectedRaga == raga) Color(0xFFE8B85C) else Color.White
                            ) 
                        },
                        modifier = Modifier.clickable {
                            viewModel.selectRaga(raga)
                            showRagaSelector = false
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (uiState.isDarkMode) Color(0xFF07111F) else Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        TopBar(
            isDarkMode = uiState.isDarkMode,
            onThemeToggle = { viewModel.toggleDarkMode() },
            onMenuClick = onMenuClick,
            onSettingsClick = onSettingsClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ShrutiCard(
                frequency = uiState.shrutiFrequency,
                isDarkMode = uiState.isDarkMode,
                onIncrease = { viewModel.increaseFrequency() },
                onDecrease = { viewModel.decreaseFrequency() },
                modifier = Modifier.weight(1f)
            )

            TanpuraCard(
                isPlaying = uiState.isTanpuraPlaying,
                isDarkMode = uiState.isDarkMode,
                onToggle = { viewModel.toggleTanpura() },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        RagaCard(
            selectedRaga = uiState.selectedRaga,
            isDarkMode = uiState.isDarkMode,
            onOpenSelector = { showRagaSelector = true },
            onPlayArohanam = { viewModel.playArohanam() },
            onPlayAvarohanam = { viewModel.playAvarohanam() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SwaraSection(
            selectedRaga = uiState.selectedRaga,
            activeSwara = uiState.activeSwara,
            isDarkMode = uiState.isDarkMode,
            onSwaraClick = { viewModel.playSwara(it) }
        )
    }
}

@Composable
fun TopBar(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = onMenuClick
        ) {

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = if (isDarkMode) Color.White else Color.Black
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
                color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                fontSize = 10.sp,
                letterSpacing = 3.sp
            )
        }

        Row {

            IconButton(
                onClick = onThemeToggle
            ) {

                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                    tint = if (isDarkMode) Color.White else Color.Black
                )
            }

            IconButton(
                onClick = onSettingsClick
            ) {

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open Settings",
                    tint = if (isDarkMode) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun ShrutiCard(
    frequency: Double,
    isDarkMode: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF101D2D) else Color(0xFFF0F0F0)
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
                text = String.format(Locale.getDefault(), "%.1f Hz", frequency),
                color = if (isDarkMode) Color.White else Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                Button(
                    onClick = onDecrease,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkMode) Color(0xFF172638) else Color(0xFFE0E0E0),
                        contentColor = if (isDarkMode) Color.White else Color.Black
                    ),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .semantics { contentDescription = "Decrease frequency" }
                ) {
                    Text("-", fontSize = 20.sp)
                }

                Button(
                    onClick = onIncrease,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkMode) Color(0xFF172638) else Color(0xFFE0E0E0),
                        contentColor = if (isDarkMode) Color.White else Color.Black
                    ),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .semantics { contentDescription = "Increase frequency" }
                ) {
                    Text("+", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "C#3",
                color = if (isDarkMode) Color.LightGray else Color.DarkGray
            )
        }
    }
}

@Composable
fun TanpuraCard(
    isPlaying: Boolean,
    onToggle: () -> Unit,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF101D2D) else Color(0xFFF0F0F0)
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
                color = if (isDarkMode) Color.LightGray else Color.DarkGray,
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
                onClick = onToggle,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkMode) Color(0xFF172638) else Color(0xFFE0E0E0),
                    contentColor = if (isDarkMode) Color.White else Color.Black
                ),
                modifier = Modifier
                    .defaultMinSize(minWidth = 56.dp, minHeight = 48.dp)
                    .semantics { contentDescription = if (isPlaying) "Stop Tanpura" else "Play Tanpura" }
            ) {

                Text(
                    text = if (isPlaying) "■" else "▶",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun RagaCard(
    selectedRaga: Raga,
    isDarkMode: Boolean,
    onOpenSelector: () -> Unit,
    onPlayArohanam: () -> Unit,
    onPlayAvarohanam: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF101D2D) else Color(0xFFF0F0F0)
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
                onClick = onOpenSelector,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkMode) Color(0xFF172638) else Color(0xFFE0E0E0),
                    contentColor = if (isDarkMode) Color.White else Color.Black
                )
            ) {

                Text(
                    text = selectedRaga.name,
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AROHANAM",
                            color = Color(0xFFE8B85C),
                            fontSize = 12.sp
                        )
                        IconButton(
                            onClick = onPlayArohanam,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Arohanam",
                                tint = Color(0xFFE8B85C),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedRaga.arohanam.joinToString(" ") { it.notation },
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 16.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AVAROHANAM",
                            color = Color(0xFFE8B85C),
                            fontSize = 12.sp
                        )
                        IconButton(
                            onClick = onPlayAvarohanam,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Avarohanam",
                                tint = Color(0xFFE8B85C),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedRaga.avarohanam.joinToString(" ") { it.notation },
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SwaraSection(
    selectedRaga: Raga,
    activeSwara: Swara?,
    isDarkMode: Boolean,
    onSwaraClick: (Swara) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF101D2D) else Color(0xFFF0F0F0)
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

            val displaySwaras = selectedRaga.arohanam.filter { it.name != "S'" }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                displaySwaras.forEach { swara ->
                    SwaraButton(swara.notation, swara.fullName, swara, activeSwara, isDarkMode, onSwaraClick)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tap any swara to play as shruti",
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun SwaraButton(
    notation: String,
    name: String,
    swara: Swara,
    activeSwara: Swara?,
    isDarkMode: Boolean,
    onClick: (Swara) -> Unit
) {
    val isActive = activeSwara == swara

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { onClick(swara) },
            modifier = Modifier
                .size(65.dp)
                .semantics { contentDescription = "Play $name" },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isActive) Color(0xFFE8B85C) else (if (isDarkMode) Color(0xFF172638) else Color(0xFFE0E0E0))
            ),
            contentPadding = PaddingValues(0.dp)
        ) {

            Text(
                text = notation,
                fontSize = 18.sp,
                color = if (isActive) Color(0xFF07111F) else (if (isDarkMode) Color.White else Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = name,
            color = if (isDarkMode) Color.LightGray else Color.DarkGray,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NadiAppPreview() {
    // Preview placeholder
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Nādi App Preview")
    }
}
