package com.example.calchub.ui.screens.mf_returns

import androidx.compose.runtime.Composable
import com.example.calchub.ui.screens.lumpsum.LumpsumCalculatorScreen

/**
 * Screen for calculating Mutual Fund returns.
 * Reuses the [LumpsumCalculatorScreen] composable as the logic is identical.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@Composable
fun MfReturnsCalculatorScreen(onBackClick: () -> Unit) {
    LumpsumCalculatorScreen(onBackClick)
}
