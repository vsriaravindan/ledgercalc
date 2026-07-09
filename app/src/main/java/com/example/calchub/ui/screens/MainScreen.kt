package com.example.calchub.ui.screens

import com.example.calchub.ui.theme.NeonBackground

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calchub.ui.components.NeonNavBar

/**
 * The main container screen of the app.
 * Handles navigation between Home, Favorites, and Tools tabs using a bottom navigation bar.
 *
 * @param onCalculatorClick Callback triggered when a calculator is selected.
 */
@Composable
fun MainScreen(onCalculatorClick: (String) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = NeonBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Content Area
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (selectedTab) {
                    0 -> HomeScreen(onCalculatorClick = onCalculatorClick)
                    1 -> FavoritesScreen(onCalculatorClick = onCalculatorClick)
                    2 -> ToolsScreen(onCalculatorClick = onCalculatorClick)
                }
            }

            // Floating Navigation Bar Container
            // This container handles ALL padding (system + aesthetic)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    // Apply system insets FIRST to avoid the gesture bar.
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    // Apply aesthetic padding SECOND for the floating look.
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // The fixed NeonNavBar is placed here. It has NO padding of its own.
                NeonNavBar(
                    selectedItem = selectedTab,
                    onItemSelected = { selectedTab = it }
                )
            }
        }
    }
}
