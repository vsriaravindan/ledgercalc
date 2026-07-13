package com.example.calchub.ui.screens

import com.example.calchub.ui.theme.NeonGreen
import com.example.calchub.ui.theme.NeonPink
import com.example.calchub.ui.theme.NeonText

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calchub.domain.model.CalculatorCategory
import com.example.calchub.domain.model.allCalculators
import com.example.calchub.ui.components.NeonCard
import com.example.calchub.ui.components.NeonSearch

/**
 * The tools screen displaying calculators grouped by category.
 *
 * @param onCalculatorClick Callback triggered when a calculator is selected.
 */
@Composable
fun ToolsScreen(onCalculatorClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    
    val categorizedCalculators = allCalculators.groupBy { it.category }
    val categories = CalculatorCategory.values()
    
    // Filter calculators based on search
    val filteredCategories = if (searchQuery.isEmpty()) {
        categories.associateWith { categorizedCalculators[it] ?: emptyList() }
    } else {
        categories.associateWith { category ->
            categorizedCalculators[category]?.filter { 
                it.name.contains(searchQuery, ignoreCase = true) 
            } ?: emptyList()
        }.filterValues { it.isNotEmpty() }
    }
    Box(modifier = Modifier.fillMaxSize()) {

    

        // Main Content (List)
        LazyColumn(
            contentPadding = PaddingValues(
                top = 230.dp,
                bottom = 120.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCategories.keys.toList()) { category ->
                val calculators = filteredCategories[category] ?: emptyList()
                if (calculators.isNotEmpty()) {
                    Column {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        calculators.forEach { calculator ->
                            NeonCard(
                                onClick = { onCalculatorClick(calculator.route) },
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    calculator.icon.let { icon ->
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                    }
                                    Text(
                                        text = calculator.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Header & Search
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0f),
                        )
                    )
                )
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 32.dp)
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
                    text = "Tools",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        shadow = Shadow(
                            color = MaterialTheme.colorScheme.onSurface,
                            blurRadius = 20f
                        )
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
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
