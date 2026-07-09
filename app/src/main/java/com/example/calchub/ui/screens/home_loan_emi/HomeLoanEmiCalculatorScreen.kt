package com.example.calchub.ui.screens.home_loan_emi

import androidx.compose.runtime.Composable
import com.example.calchub.ui.screens.emi.EmiCalculatorScreen

/**
 * Screen for calculating Home Loan EMI.
 * Reuses the [EmiCalculatorScreen] composable.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@Composable
fun HomeLoanEmiCalculatorScreen(onBackClick: () -> Unit) {
    EmiCalculatorScreen(onBackClick)
}
