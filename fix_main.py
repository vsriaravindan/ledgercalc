import re

file_path = 'app/src/main/java/com/example/MainActivity.kt'
with open(file_path, 'r') as f:
    lines = f.readlines()

new_lines = []
skip = False
for i, line in enumerate(lines):
    if i == 107 - 1: # 0-indexed
        new_lines.append('        }\n')
        skip = True
    
    if i == 117 - 1:
        skip = False
        continue
        
    if not skip:
        new_lines.append(line)

with open(file_path, 'w') as f:
    f.writelines(new_lines)
