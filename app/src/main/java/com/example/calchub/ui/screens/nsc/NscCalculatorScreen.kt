package com.example.calchub.ui.screens.nsc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
 * Screen for calculating National Savings Certificate (NSC) returns.
 * Calculates returns for a fixed 5-year tenure scheme.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NscCalculatorScreen(onBackClick: () -> Unit) {
    var investment by remember { mutableDoubleStateOf(100000.0) }
    var interestRate by remember { mutableDoubleStateOf(7.7) }

    val results = CalculatorLogic.calculateNSC(investment, interestRate)

    CalculatorScaffold(
        title = "NSC Calculator",
        onBackClick = onBackClick,
        calculatorId = "nsc"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Investment Amount",
                value = investment,
                onValueChange = { investment = it },
                range = 1000.0..10000000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Interest Rate (p.a)",
                value = interestRate,
                onValueChange = { interestRate = it },
                range = 1.0..15.0,
                symbol = "%"
            )

            Text(
                text = "Fixed 5-year tenure",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
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
