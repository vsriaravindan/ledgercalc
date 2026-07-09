import re

def process_file(path, replacements, imports):
    with open(path, 'r') as f:
        content = f.read()

    for old, new in replacements:
        content = content.replace(old, new)
        
    # Add imports after package
    if imports:
        match = re.search(r'package .*?\n\n?', content)
        if match:
            idx = match.end()
            content = content[:idx] + imports + "\n" + content[idx:]

    with open(path, 'w') as f:
        f.write(content)

fav_replacements = [
    ('color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f)', 'color = NeonText.copy(alpha = 0.5f)'),
    ('tint = androidx.compose.ui.graphics.Color.White', 'tint = NeonPink', 1), # wait, I don't know which was pink and which was text
]

# Better way: replace based on exact lines
