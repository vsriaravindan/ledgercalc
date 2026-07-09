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
    
    # Fix broken Column modifier chains
    content = re.sub(r'\.fillMaxWidth\(\)\s*Color\([^)]+\)\.copy\(alpha = [0-9.]+f\),\s*Color\.Transparent\s*\)\s*\)\s*\)', '.fillMaxWidth()', content, flags=re.DOTALL)
    
    with open(f, 'w') as file:
        file.write(content)
