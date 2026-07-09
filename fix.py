import os
import re

files = [
    'app/src/main/java/com/example/calchub/ui/screens/HomeScreen.kt',
    'app/src/main/java/com/example/calchub/ui/screens/ToolsScreen.kt',
    'app/src/main/java/com/example/calchub/ui/screens/FavoritesScreen.kt'
]

for f in files:
    with open(f, 'r') as file:
        content = file.read()
    
    # Remove the broken lines left by sed
    content = re.sub(r'modifier = Modifier\s*\.fillMaxSize\(\)\s*Color\([^)]+\),.*?\) \{', r'modifier = Modifier.fillMaxSize()\n    ) {', content, flags=re.DOTALL)
    
    with open(f, 'w') as file:
        file.write(content)
