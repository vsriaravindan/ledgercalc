package com.example.calchub.ui.screens.ppf

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calchub.domain.logic.CalculatorLogic
import com.example.calchub.ui.components.CalculatorInput
import com.example.calchub.ui.components.CalculatorScaffold
import com.example.calchub.ui.components.DonutChart
import com.example.calchub.ui.components.DonutChartData
import com.example.calchub.ui.components.ResultCard

/**
 * Screen for Public Provident Fund (PPF) calculation.
 * Calculates maturity amount and interest earned over time.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PpfCalculatorScreen(onBackClick: () -> Unit) {
    var yearlyInvestment by remember { mutableDoubleStateOf(100000.0) }
    var interestRate by remember { mutableDoubleStateOf(7.1) } // Current PPF rate
    var timePeriodYears by remember { mutableDoubleStateOf(15.0) }

    val results = CalculatorLogic.calculatePPF(yearlyInvestment, interestRate, timePeriodYears)

    CalculatorScaffold(
        title = "PPF Calculator",
        onBackClick = onBackClick,
        calculatorId = "ppf"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Yearly Investment",
                value = yearlyInvestment,
                onValueChange = { yearlyInvestment = it },
                range = 500.0..150000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Interest Rate (p.a)",
                value = interestRate,
                onValueChange = { interestRate = it },
                range = 1.0..15.0,
                symbol = "%"
            )

            CalculatorInput(
                label = "Time Period",
                value = timePeriodYears,
                onValueChange = { timePeriodYears = it },
                range = 15.0..50.0,
                symbol = "Yr"
            )

            DonutChart(
                data = listOf(
                    DonutChartData(results.first, MaterialTheme.colorScheme.primary, "Invested"),
                    DonutChartData(results.second, MaterialTheme.colorScheme.tertiary, "Returns")
                )
            )

            ResultCard(
                investedAmount = results.first,
                estimatedReturns = results.second,
                totalValue = results.third
            )
        }
    }
}
