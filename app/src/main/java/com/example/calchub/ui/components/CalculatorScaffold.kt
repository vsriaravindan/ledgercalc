package com.example.calchub.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.calchub.data.repository.FavoritesRepository

@OptIn(ExperimentalMaterial3Api::class)
/**
 * A standardized Scaffold for Calculator screens.
 * Includes a [NeonHeader] with back navigation and favorite toggle.
 *
 * @param title The title of the calculator screen.
 * @param onBackClick Callback for the back button.
 * @param calculatorId The unique ID of the calculator (for favorites).
 * @param content The content of the screen.
 */
@Composable
fun CalculatorScaffold(
    title: String,
    onBackClick: () -> Unit,
    calculatorId: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val favoritesRepository = remember { FavoritesRepository.getInstance(context) }
    val favorites by favoritesRepository.favorites.collectAsState()
    val isFavorite = favorites.contains(calculatorId)

    Scaffold(
        topBar = {
            NeonHeader(
                title = "CalcHub",
                subtitle = title,
                isFavorite = isFavorite,
                onBackClick = onBackClick,
                onFavoriteClick = { favoritesRepository.toggleFavorite(calculatorId) }
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        content = content
    )
}
