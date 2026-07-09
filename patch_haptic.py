import re

file_path = 'app/src/main/java/com/example/ui/components/CalculatorPad.kt'
with open(file_path, 'r') as f:
    content = f.read()

# Add haptic feedback to CalcButton
if 'LocalHapticFeedback' not in content:
    content = content.replace('import androidx.compose.ui.Modifier', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.platform.LocalHapticFeedback\nimport androidx.compose.ui.hapticfeedback.HapticFeedbackType')
    
    # In CalcButton, we want to capture the haptic feedback and use it on click
    content = re.sub(
        r'fun CalcButton\((.*?)\) \{',
        r'fun CalcButton(\1) {\n    val haptic = LocalHapticFeedback.current',
        content,
        flags=re.DOTALL
    )
    
    content = re.sub(
        r'\.clickable\(onClick = onClick\)',
        r'.clickable { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onClick() }',
        content
    )

with open(file_path, 'w') as f:
    f.write(content)
