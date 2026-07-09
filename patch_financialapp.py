import re

file_path = 'app/src/main/java/com/example/MainActivity.kt'
with open(file_path, 'r') as f:
    content = f.read()

# Make sure we add necessary imports at the top
imports = """
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shadow
"""
if "import androidx.compose.foundation.clickable" not in content:
    content = content.replace("package com.example", "package com.example\n" + imports.strip())

replacement = """
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialApp(onOpenDrawer: () -> Unit, onGoHome: () -> Unit) {
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                title = { 
                    Text(
                        "Financial Calc", 
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = com.example.calchub.ui.theme.NeonPink,
                            shadow = Shadow(
                                color = com.example.calchub.ui.theme.NeonPink,
                                blurRadius = 15f
                            )
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = com.example.calchub.ui.theme.NeonGreen)
                    }
                },
                actions = {
                    IconButton(onClick = onGoHome) {
                        Icon(androidx.compose.material.icons.Icons.Default.Home, contentDescription = "Back to Home", tint = com.example.calchub.ui.theme.NeonGreen)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            com.example.calchub.ui.navigation.AppNavigation()
        }
    }
}
"""

content = re.sub(r'@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*fun FinancialApp\(onOpenDrawer: \(\) -> Unit, onGoHome: \(\) -> Unit\) \{.*?\n\}\n(?=\@Composable|class)', replacement.strip() + "\n\n", content, flags=re.DOTALL)

with open(file_path, 'w') as f:
    f.write(content)
