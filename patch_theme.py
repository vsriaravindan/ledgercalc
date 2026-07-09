import re

file_path = 'app/src/main/java/com/example/ui/theme/Theme.kt'
with open(file_path, 'r') as f:
    content = f.read()

replacement = """
private val BentoDarkColorScheme = darkColorScheme(
    primary = com.example.calchub.ui.theme.NeonGreen,
    onPrimary = com.example.calchub.ui.theme.NeonBackground,
    primaryContainer = com.example.calchub.ui.theme.NeonSurface,
    onPrimaryContainer = com.example.calchub.ui.theme.NeonGreen,
    background = com.example.calchub.ui.theme.NeonBackground,
    onBackground = com.example.calchub.ui.theme.NeonText,
    surface = com.example.calchub.ui.theme.NeonSurface,
    onSurface = com.example.calchub.ui.theme.NeonText,
    surfaceVariant = com.example.calchub.ui.theme.NeonSurface.copy(alpha=0.7f),
    onSurfaceVariant = com.example.calchub.ui.theme.NeonText,
    outline = com.example.calchub.ui.theme.NeonGreen.copy(alpha=0.5f),
    outlineVariant = com.example.calchub.ui.theme.NeonPink.copy(alpha=0.3f),
    error = BentoDarkError,
    onError = BentoDarkOnError,
    errorContainer = BentoDarkErrorContainer,
    onErrorContainer = BentoDarkOnErrorContainer
)

private val BentoLightColorScheme = BentoDarkColorScheme
"""

content = re.sub(r'private val BentoLightColorScheme = lightColorScheme\(.*?\)\s*private val BentoDarkColorScheme = darkColorScheme\(.*?\)', replacement.strip(), content, flags=re.DOTALL)

with open(file_path, 'w') as f:
    f.write(content)
