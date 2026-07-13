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
 * Includes a [NeonHeader] with back navigation, favorite toggle, and share.
 *
 * @param title The title of the calculator screen.
 * @param onBackClick Callback for the back button.
 * @param calculatorId The unique ID of the calculator (for favorites).
 * @param onShare Callback when share is clicked. Null hides the share button.
 * @param shareContent The text to share. Only used when onShare is set.
 * @param content The content of the screen.
 */
@Composable
fun CalculatorScaffold(
    title: String,
    onBackClick: () -> Unit,
    calculatorId: String,
    onShare: (() -> Unit)? = null,
    shareContent: String = "",
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
                onFavoriteClick = { favoritesRepository.toggleFavorite(calculatorId) },
                onShare = if (onShare != null && shareContent.isNotEmpty()) {
                    { onShare() }
                } else null,
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        content = content
    )
}
