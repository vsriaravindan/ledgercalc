import re

file_path = 'app/src/main/java/com/example/MainActivity.kt'
with open(file_path, 'r') as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if i == 111: # index 111 is line 112
        new_lines.append('            }\n')
        new_lines.append('        }\n')
    else:
        new_lines.append(line)

with open(file_path, 'w') as f:
    f.writelines(new_lines)
