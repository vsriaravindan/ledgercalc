package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.CalculatorViewModel
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.GlobalHistoryScreen
import com.example.ui.screens.GroupDetailScreen
import com.example.ui.screens.GroupsScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CalculatorViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }
                
            MyApplicationTheme(darkTheme = isDark, fontFamily = viewModel.selectedFontFamily) {
                com.example.ui.components.AppBackground {
                    MainApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainApp(viewModel: CalculatorViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentSection by remember { mutableStateOf("home") } // "home" or "financial"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(Modifier.height(32.dp))
                        Text(
                            "LedgerCalc", 
                            modifier = Modifier.padding(16.dp), 
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.height(16.dp))
                        androidx.compose.material3.Surface(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth().clickable { currentSection = "home"; scope.launch { drawerState.close() } }
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, contentDescription = null, tint = if (currentSection == "home") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Spacer(Modifier.width(16.dp))
                                Text("Home (Calculator)", color = if (currentSection == "home") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        }
                        
                        androidx.compose.material3.Surface(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth().clickable { currentSection = "financial"; scope.launch { drawerState.close() } }
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = if (currentSection == "financial") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Spacer(Modifier.width(16.dp))
                                Text("Financial Calculator", color = if (currentSection == "financial") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        }
                        
                        // Settings
                        androidx.compose.material3.Surface(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth().clickable { currentSection = "settings"; scope.launch { drawerState.close() } }
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Spacer(Modifier.width(16.dp))
                                Text("Settings", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    ) {
        if (currentSection == "home") {
            HomeApp(viewModel, onOpenDrawer = { scope.launch { drawerState.open() } })
        } else if (currentSection == "settings") {
            com.example.ui.screens.SettingsScreen(
                viewModel = viewModel,
                onBack = { currentSection = "home" },
            )
        } else {
            FinancialApp(
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onGoHome = { currentSection = "home" },
                onToggleTheme = {
                    val current = viewModel.themeMode.value
                    viewModel.setThemeMode(if (current == 2) 1 else if (current == 1) 0 else 2)
                },
                isDarkTheme = viewModel.themeMode.value != 1,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialApp(onOpenDrawer: () -> Unit, onGoHome: () -> Unit, onToggleTheme: () -> Unit, isDarkTheme: Boolean) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { 
                    Text(
                        "Financial Calculator", 
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle theme",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(onClick = onGoHome) {
                        Icon(Icons.Default.Home, contentDescription = "Back to Home", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            com.example.calchub.ui.navigation.AppNavigation()
        }
    }
}

@Composable
fun HomeApp(viewModel: CalculatorViewModel, onOpenDrawer: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isMainTab = currentDestination?.route in listOf("calculator", "groups", "history")

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (isMainTab) {
                Box(
                    modifier = Modifier
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    androidx.compose.material3.Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
                                .clip(RoundedCornerShape(32.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                        ) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
                                label = { Text("Calculator", color = if (currentDestination?.hierarchy?.any { it.route == "calculator" } == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                                selected = currentDestination?.hierarchy?.any { it.route == "calculator" } == true,
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                onClick = {
                                    navController.navigate("calculator") {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Folder, contentDescription = "Ledgers") },
                                label = { Text("Ledgers", color = if (currentDestination?.hierarchy?.any { it.route == "groups" } == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                                selected = currentDestination?.hierarchy?.any { it.route == "groups" } == true,
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                onClick = {
                                    navController.navigate("groups") {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.History, contentDescription = "History") },
                                label = { Text("History", color = if (currentDestination?.hierarchy?.any { it.route == "history" } == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                                selected = currentDestination?.hierarchy?.any { it.route == "history" } == true,
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                onClick = {
                                    navController.navigate("history") {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "calculator",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("calculator") {
                LaunchedEffect(Unit) { viewModel.setActiveGroup(null) }
                CalculatorScreen(viewModel = viewModel, onOpenDrawer = onOpenDrawer)
            }
            composable("groups") {
                GroupsScreen(
                    viewModel = viewModel,
                    onGroupClick = { id ->
                        navController.navigate("group_detail/$id")
                    },
                    onOpenDrawer = onOpenDrawer
                )
            }
            composable("history") {
                GlobalHistoryScreen(viewModel = viewModel, onOpenDrawer = onOpenDrawer)
            }
            composable("group_detail/{groupId}") { backStackEntry ->
                val groupIdStr = backStackEntry.arguments?.getString("groupId") ?: return@composable
                val groupId = groupIdStr.toIntOrNull() ?: return@composable
                
                GroupDetailScreen(
                    groupId = groupId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenDrawer = onOpenDrawer
                )
            }
        }
    }
}
