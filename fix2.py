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
    
    # Fix the remaining syntax errors
    # I'll just remove the Box containing the Canvas completely.
    content = re.sub(r'// Background with gradient.*?Box\(\s*modifier = Modifier\.fillMaxSize\(\)\s*\) \{.*?Canvas\(modifier = Modifier\.fillMaxSize\(\)\) \{.*?\}(?=\s*//)', '', content, flags=re.DOTALL)
    
    with open(f, 'w') as file:
        file.write(content)
