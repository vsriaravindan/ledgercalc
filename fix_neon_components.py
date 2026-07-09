import re

file_path = 'app/src/main/java/com/example/calchub/ui/components/NeonComponents.kt'
with open(file_path, 'r') as f:
    content = f.read()

content = content.replace('import com.example.ui.components.GlassSurface\n', '')
content = content.replace('GlassSurface(', 'androidx.compose.material3.Surface(')

# Change Color.Transparent to NeonBackground or similar if needed.
# For now, just change GlassSurface to Surface. Surface has color=Color.Transparent by default if we don't pass it? No.
content = content.replace('androidx.compose.material3.Surface(', 'androidx.compose.material3.Surface(color=androidx.compose.ui.graphics.Color(0xFF121212), ')

with open(file_path, 'w') as f:
    f.write(content)

