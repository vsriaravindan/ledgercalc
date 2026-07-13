package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
) {
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val includeWatermark by viewModel.includeWatermark.collectAsStateWithLifecycle()
    val fontFamily by viewModel.fontFamily.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Theme
            SettingsGroup("Theme") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("System" to 0, "Light" to 1, "Dark" to 2).forEach { (label, mode) ->
                        FilterChip(
                            selected = themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Currency
            SettingsGroup("Currency") {
                val symbols = listOf("$", "€", "£", "₹", "¥", "₩", "₽", "฿", "₫", "₱")
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    symbols.chunked(5).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            row.forEach { symbol ->
                                TextButton(
                                    onClick = { viewModel.setCurrency(symbol) },
                                    contentPadding = PaddingValues(8.dp),
                                ) {
                                    Text(
                                        text = symbol,
                                        fontSize = 22.sp,
                                        fontWeight = if (currency == symbol) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currency == symbol) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Font Style
            SettingsGroup("Font Style") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    com.example.ui.theme.AppFont.entries.forEach { font ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setFontFamily(font.displayName) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (fontFamily == font.displayName) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                        ) {
                            Text(
                                text = font.displayName,
                                fontFamily = font.fontFamily,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                fontWeight = if (fontFamily == font.displayName) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            // PDF Watermark
            SettingsGroup("PDF Export") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setIncludeWatermark(!includeWatermark) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Include Watermark", color = MaterialTheme.colorScheme.onSurface)
                    Switch(
                        checked = includeWatermark,
                        onCheckedChange = { viewModel.setIncludeWatermark(it) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}
