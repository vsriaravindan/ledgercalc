package com.example.calchub.ui.screens.car_loan_emi

import androidx.compose.runtime.Composable
import com.example.calchub.ui.screens.emi.EmiCalculatorScreen

/**
 * Screen for calculating Car Loan EMI.
 * Reuses the [EmiCalculatorScreen] composable.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@Composable
fun CarLoanEmiCalculatorScreen(onBackClick: () -> Unit) {
    EmiCalculatorScreen(onBackClick)
}
