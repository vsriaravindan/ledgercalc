import re

file_path = 'app/src/main/java/com/example/ui/screens/GroupsScreen.kt'
with open(file_path, 'r') as f:
    lines = f.readlines()

new_lines = []
skip = False
for i, line in enumerate(lines):
    if i == 247: # Line 248 is index 247
        replacement = """
    if (showSettingsDialog) {
        val symbols = listOf("$", "€", "£", "₹", "¥", "₩", "₽", "฿", "₫", "₱")
        val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
        
        androidx.compose.ui.window.Dialog(onDismissRequest = { showSettingsDialog = false }) {
            com.example.ui.components.GlassSurface(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Settings", style = MaterialTheme.typography.titleLarge, color = com.example.calchub.ui.theme.NeonPink)
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                        item {
                            Text("Theme", style = MaterialTheme.typography.titleMedium, color = com.example.calchub.ui.theme.NeonGreen)
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                FilterChip(
                                    selected = themeMode == 0,
                                    onClick = { viewModel.setThemeMode(0) },
                                    label = { Text("System") }
                                )
                                FilterChip(
                                    selected = themeMode == 1,
                                    onClick = { viewModel.setThemeMode(1) },
                                    label = { Text("Light") }
                                )
                                FilterChip(
                                    selected = themeMode == 2,
                                    onClick = { viewModel.setThemeMode(2) },
                                    label = { Text("Dark") }
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = androidx.compose.ui.graphics.Color(0x33FFFFFF))
                            Spacer(Modifier.height(16.dp))
                            Text("Currency", style = MaterialTheme.typography.titleMedium, color = com.example.calchub.ui.theme.NeonGreen)
                            Spacer(Modifier.height(8.dp))
                        }
                        items(symbols.chunked(5)) { rowSymbols ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                rowSymbols.forEach { symbol ->
                                    TextButton(
                                        onClick = { viewModel.setCurrency(symbol) },
                                    ) {
                                        Text(symbol, fontSize = 24.sp, color = if (currency == symbol) com.example.calchub.ui.theme.NeonPink else com.example.calchub.ui.theme.NeonText, fontWeight = if (currency == symbol) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = androidx.compose.ui.graphics.Color(0x33FFFFFF))
                            Spacer(Modifier.height(16.dp))
                            
                            val includeWatermark by viewModel.includeWatermark.collectAsStateWithLifecycle()
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.setIncludeWatermark(!includeWatermark) }.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Include PDF Watermark", style = MaterialTheme.typography.titleMedium, color = com.example.calchub.ui.theme.NeonGreen)
                                Switch(
                                    checked = includeWatermark,
                                    onCheckedChange = { viewModel.setIncludeWatermark(it) }
                                )
                            }
                        }
                        item {
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = androidx.compose.ui.graphics.Color(0x33FFFFFF))
                            Spacer(Modifier.height(16.dp))
                            Text("Data Management", style = MaterialTheme.typography.titleMedium, color = com.example.calchub.ui.theme.NeonGreen)
                            Spacer(Modifier.height(8.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(onClick = { createDoc.launch("calculator_backup.json") }) {
                                    Text("Backup")
                                }
                                Button(onClick = { openDoc.launch(arrayOf("application/json")) }) {
                                    Text("Restore")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showSettingsDialog = false }) {
                            Text("Close", color = com.example.calchub.ui.theme.NeonPink)
                        }
                    }
                }
            }
        }
    }
}
"""
        new_lines.append(replacement)
        skip = True
    
    if i == 403: # end of file essentially
        skip = False
        continue
        
    if not skip:
        new_lines.append(line)

with open(file_path, 'w') as f:
    f.writelines(new_lines)
