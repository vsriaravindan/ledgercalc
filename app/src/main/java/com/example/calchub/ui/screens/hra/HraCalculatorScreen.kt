package com.example.calchub.ui.screens.hra

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calchub.domain.logic.CalculatorLogic
import com.example.calchub.ui.components.CalculatorInput
import com.example.calchub.ui.components.CalculatorScaffold
import com.example.calchub.ui.components.NeonCard
import com.example.calchub.ui.components.ResultRow
import java.text.NumberFormat
import java.util.Locale

/**
 * Screen for calculating House Rent Allowance (HRA) exemption.
 * Considers salary, HRA received, rent paid, and city type.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HraCalculatorScreen(onBackClick: () -> Unit) {
    var basicSalary by remember { mutableDoubleStateOf(500000.0) }
    var hraReceived by remember { mutableDoubleStateOf(200000.0) }
    var rentPaid by remember { mutableDoubleStateOf(180000.0) }
    var isMetroCity by remember { mutableStateOf(true) }

    val results = CalculatorLogic.calculateHRA(basicSalary, hraReceived, rentPaid, isMetroCity)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "HRA Calculator",
        onBackClick = onBackClick,
        calculatorId = "hra"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Basic Salary (Annual)",
                value = basicSalary,
                onValueChange = { basicSalary = it },
                range = 100000.0..5000000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "HRA Received (Annual)",
                value = hraReceived,
                onValueChange = { hraReceived = it },
                range = 0.0..2000000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Rent Paid (Annual)",
                value = rentPaid,
                onValueChange = { rentPaid = it },
                range = 0.0..2000000.0,
                symbol = "₹"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isMetroCity,
                    onCheckedChange = { isMetroCity = it }
                )
                Text("Living in Metro City (Mumbai, Delhi, Kolkata, Chennai)")
            }

            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ResultRow("HRA Received", currencyFormat.format(results.first))
                    ResultRow("HRA Exemption", currencyFormat.format(results.second))
                    ResultRow("Taxable HRA", currencyFormat.format(results.third), isTotal = true)
                }
            }
        }
    }
}
