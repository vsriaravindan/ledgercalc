package com.example.calchub.ui.screens

import com.example.calchub.ui.theme.NeonGreen
import com.example.calchub.ui.theme.NeonPink
import com.example.calchub.ui.theme.NeonText
import com.example.calchub.ui.theme.NeonBackground

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calchub.data.repository.FavoritesRepository
import com.example.calchub.domain.model.allCalculators
import com.example.calchub.ui.components.NeonCard
import com.example.calchub.ui.components.NeonSearch

/**
 * The screen displaying the user's favorite calculators.
 * Allows filtering favorites by search query.
 *
 * @param onCalculatorClick Callback triggered when a calculator is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onCalculatorClick: (String) -> Unit) {
    val context = LocalContext.current
    val favoritesRepository = remember { FavoritesRepository.getInstance(context) }
    val favorites by favoritesRepository.favorites.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val favoriteCalculators = allCalculators.filter { favorites.contains(it.route) }
    
    // Filter favorite calculators based on search
    val filteredCalculators = if (searchQuery.isEmpty()) {
        favoriteCalculators
    } else {
        favoriteCalculators.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }
    Box(modifier = Modifier.fillMaxSize()) {

    

        // Main Content (List)
        if (favoriteCalculators.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 230.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite calculators yet.",
                    color = NeonText.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = 230.dp,
                    bottom = 120.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (filteredCalculators.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No calculators match your search.",
                                color = NeonText.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    items(filteredCalculators) { calculator ->
                        NeonCard(
                            onClick = { onCalculatorClick(calculator.route) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                calculator.icon.let { icon ->
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = NeonPink,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                                Text(
                                    text = calculator.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                )
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
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 24.dp)
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
                    text = "Favorites",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White,
                        shadow = Shadow(
                            color = androidx.compose.ui.graphics.Color.White,
                            blurRadius = 20f
                        )
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = NeonPink,
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
