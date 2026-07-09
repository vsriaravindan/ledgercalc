import os
import re

files = [
    'app/src/main/java/com/example/calchub/ui/screens/ToolsScreen.kt',
    'app/src/main/java/com/example/calchub/ui/screens/FavoritesScreen.kt'
]

for f in files:
    with open(f, 'r') as file:
        content = file.read()
    
    # insert Box(...) { right after the filteredCalculators assignment
    # find where "// Main Content" or "// Floating" is.
    content = re.sub(r'(\s*// Main Content)', r'\n    Box(modifier = Modifier.fillMaxSize()) {\1', content)
    
    with open(f, 'w') as file:
        file.write(content)
