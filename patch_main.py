import re

file_path = 'app/src/main/java/com/example/MainActivity.kt'
with open(file_path, 'r') as f:
    content = f.read()

# Replace ModalDrawerSheet and its items with Neon/Glassmorphic styling
replacement = """
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = com.example.calchub.ui.theme.NeonBackground,
                drawerContentColor = com.example.calchub.ui.theme.NeonText
            ) {
                Spacer(Modifier.height(32.dp))
                Text(
                    "CalcHub Menu", 
                    modifier = Modifier.padding(16.dp), 
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = com.example.calchub.ui.theme.NeonPink,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(16.dp))
                com.example.ui.components.GlassSurface(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth().clickable { currentSection = "home"; scope.launch { drawerState.close() } }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = if (currentSection == "home") com.example.calchub.ui.theme.NeonGreen else com.example.calchub.ui.theme.NeonText)
                        Spacer(Modifier.width(16.dp))
                        Text("Home (Calculator)", color = if (currentSection == "home") com.example.calchub.ui.theme.NeonGreen else com.example.calchub.ui.theme.NeonText)
                    }
                }
                
                com.example.ui.components.GlassSurface(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth().clickable { currentSection = "financial"; scope.launch { drawerState.close() } }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = if (currentSection == "financial") com.example.calchub.ui.theme.NeonGreen else com.example.calchub.ui.theme.NeonText)
                        Spacer(Modifier.width(16.dp))
                        Text("Financial Calculator", color = if (currentSection == "financial") com.example.calchub.ui.theme.NeonGreen else com.example.calchub.ui.theme.NeonText)
                    }
                }
            }
"""

content = re.sub(r'ModalDrawerSheet\(modifier = Modifier\.width\(300\.dp\)\) \{.*?\}(?=\s*\})', replacement.strip(), content, flags=re.DOTALL)

with open(file_path, 'w') as f:
    f.write(content)
