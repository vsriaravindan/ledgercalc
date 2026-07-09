import re

file_path = 'app/src/main/java/com/example/MainActivity.kt'
with open(file_path, 'r') as f:
    content = f.read()

replacement = """
    Scaffold(containerColor = androidx.compose.ui.graphics.Color.Transparent,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (isMainTab) {
                Box(
                    modifier = Modifier
                        .padding(androidx.compose.foundation.layout.WindowInsets.navigationBars.asPaddingValues())
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    NavigationBar(
                        containerColor = com.example.calchub.ui.theme.NeonSurface.copy(alpha = 0.9f),
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0))
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
                            .border(1.dp, com.example.calchub.ui.theme.NeonText.copy(alpha = 0.1f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
                            label = { Text("Calculator", color = if (currentDestination?.hierarchy?.any { it.route == "calculator" } == true) com.example.calchub.ui.theme.NeonGreen else com.example.calchub.ui.theme.NeonText) },
                            selected = currentDestination?.hierarchy?.any { it.route == "calculator" } == true,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = com.example.calchub.ui.theme.NeonSurfaceVariant,
                                selectedIconColor = com.example.calchub.ui.theme.NeonGreen,
                                unselectedIconColor = com.example.calchub.ui.theme.NeonText
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
                            label = { Text("Ledgers", color = if (currentDestination?.hierarchy?.any { it.route == "groups" } == true) com.example.calchub.ui.theme.NeonPink else com.example.calchub.ui.theme.NeonText) },
                            selected = currentDestination?.hierarchy?.any { it.route == "groups" } == true,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = com.example.calchub.ui.theme.NeonSurfaceVariant,
                                selectedIconColor = com.example.calchub.ui.theme.NeonPink,
                                unselectedIconColor = com.example.calchub.ui.theme.NeonText
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
                            label = { Text("History", color = if (currentDestination?.hierarchy?.any { it.route == "history" } == true) com.example.calchub.ui.theme.NeonGreen else com.example.calchub.ui.theme.NeonText) },
                            selected = currentDestination?.hierarchy?.any { it.route == "history" } == true,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = com.example.calchub.ui.theme.NeonSurfaceVariant,
                                selectedIconColor = com.example.calchub.ui.theme.NeonGreen,
                                unselectedIconColor = com.example.calchub.ui.theme.NeonText
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
    )
"""

content = re.sub(r'Scaffold\(containerColor = androidx\.compose\.ui\.graphics\.Color\.Transparent,.*?bottomBar = \{.*?\}(?=\s*\) \{ innerPadding ->)', replacement.strip(), content, flags=re.DOTALL)

# Also add imports if needed
imports_to_add = """
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
"""
if "import androidx.compose.ui.draw.clip" not in content:
    content = content.replace("package com.example", "package com.example\n" + imports_to_add.strip())

with open(file_path, 'w') as f:
    f.write(content)
