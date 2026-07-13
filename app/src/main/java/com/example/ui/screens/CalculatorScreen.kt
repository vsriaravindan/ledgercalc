package com.example.ui.screens
import androidx.compose.material.icons.filled.List
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CalculatorViewModel
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.CalculatorPad
import com.example.ui.components.ScientificPad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onOpenDrawer: () -> Unit = {},
    onSaveComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val expression by viewModel.expression.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val calcError by viewModel.calcError.collectAsStateWithLifecycle()
    
    val lifecycleOwner = LocalLifecycleOwner.current
    val activeGroupId by viewModel.activeGroupId.collectAsStateWithLifecycle()
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (expression.isNotEmpty() && result.isNotEmpty()) {
                    viewModel.saveDraftToHistory()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    var showSaveDialog by remember { mutableStateOf(false) }
    var showScientific by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Calculator Card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(20.dp),
                )
                .padding(16.dp)
        ) {
            val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
            val context = androidx.compose.ui.platform.LocalContext.current
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
                Row {
                    IconButton(onClick = { showScientific = !showScientific }) {
                        Icon(Icons.Default.List, contentDescription = "Scientific Mode", tint = if (showScientific) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { viewModel.undo() }) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = Color.White)
                    }
                    IconButton(onClick = { viewModel.redo() }) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = Color.White)
                    }
                    IconButton(onClick = { 
                        val text = if (result.isNotEmpty()) "$expression = $result" else expression
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(text))
                        android.widget.Toast.makeText(context, "Copied", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White)
                    }
                }
            }
            
            CalculatorDisplay(
                expression = expression,
                result = result,
                isError = calcError,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (showScientific) 0.7f else 1.2f)
            )
            
            if (showScientific) {
                ScientificPad(
                    onInput = { viewModel.onInput(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            CalculatorPad(
                onInput = { viewModel.onInput(it) },
                onClear = { viewModel.onClear() },
                onDelete = { viewModel.onDelete() },
                onEvaluate = { viewModel.onEvaluate() },
                onEquals = { 
                    if (result.isNotEmpty()) {
                        showSaveDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth().weight(2.5f)
            )
        }
    }

    if (showSaveDialog) {
        var note by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(if (activeGroupId != null) "Add to Ledger" else "Save Calculation") },
            text = {
                Column {
                    Text(if (activeGroupId != null) "Add result to active ledger group:" else "Save result to global history:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note / Label (Optional)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.onEquals(note)
                    showSaveDialog = false
                    onSaveComplete()
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
