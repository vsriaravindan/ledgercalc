package com.example.calchub.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calchub.domain.model.Calculator
import com.example.calchub.domain.model.allCalculators
import com.example.calchub.ui.components.NeonCard
import com.example.calchub.ui.components.NeonSearch

/**
 * The home screen of the application.
 * Displays a grid of all available calculators, searchable by name.
 *
 * @param onCalculatorClick Callback triggered when a calculator is selected.
 */
@Composable
fun HomeScreen(onCalculatorClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter calculators based on search
    val filteredCalculators = if (searchQuery.isEmpty()) {
        allCalculators
    } else {
        allCalculators.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content (Grid) - Now behind the floating elements
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(
                top = 230.dp, // Increased to avoid overlap with header + status bar
                bottom = 120.dp, // Space for floating nav bar
                start = 16.dp,
                end = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCalculators) { calculator ->
                CalculatorCard(calculator, onCalculatorClick)
            }
        }

        // Floating Header & Search
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 24.dp) // Add some bottom padding for the gradient fade
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Calc",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.White,
                            blurRadius = 20f
                        )
                    )
                )
                Text(
                    text = "Hub",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.White,
                            blurRadius = 20f
                        )
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Search Bar
            NeonSearch(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

/**
 * A card representing a single calculator in the grid.
 *
 * @param calculator The calculator data model.
 * @param onClick Callback triggered when the card is clicked.
 */
@Composable
fun CalculatorCard(calculator: Calculator, onClick: (String) -> Unit) {
    NeonCard(
        modifier = Modifier.aspectRatio(0.65f),
        onClick = { onClick(calculator.route) },
        
        useGradientBorder = true
    ) {
        // This Column's arrangement is now in full control.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 4.dp), // Adjust padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // This will now work as expected
        ) {
            // The Box now wraps its content instead of expanding.
            Box(
                contentAlignment = Alignment.Center,
                // *** CRITICAL CHANGE: Modifier.weight(1f) is REMOVED ***
                // The size of this box will now be determined by its children.
            ) {
                // The Canvas defines the glow area.
                Canvas(modifier = Modifier.size(56.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                }
                // The Icon is drawn on top of the Canvas.
                Icon(
                    imageVector = calculator.icon,
                    contentDescription = calculator.name,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = calculator.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = Color.White,
                maxLines = 2,
                minLines = 2
            )
        }
    }
}
