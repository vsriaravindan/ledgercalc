package com.example.ui.screens

import androidx.compose.material.icons.filled.Menu
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CalculatorViewModel
import com.example.data.GlobalHistory
import com.example.data.LedgerGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GlobalHistoryScreen(
    viewModel: CalculatorViewModel,
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val history by viewModel.globalHistory.collectAsStateWithLifecycle()
    val groups by viewModel.allGroups.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var selectedHistoryItemForGroup by remember { mutableStateOf<com.example.data.GlobalHistory?>(null) }
    var itemToDelete by remember { mutableStateOf<Int?>(null) }

    Scaffold(containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                title = { Text("Global History") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    var showClearDialog by remember { mutableStateOf(false) }
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All")
                    }
                    if (showClearDialog) {
                        AlertDialog(
                            onDismissRequest = { showClearDialog = false },
                            title = { Text("Clear History") },
                            text = { Text("Are you sure you want to clear all global history?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.clearAllGlobalHistory()
                                    showClearDialog = false
                                }) {
                                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
                            }
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No history yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(history, key = { it.id }) { item ->
                    val isInteger = item.result % 1.0 == 0.0
                    val resultStr = if (isInteger) item.result.toLong().toString() else item.result.toString()
                    val fullExpr = "${item.expression} = $resultStr"
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = { selectedHistoryItemForGroup = item }
                            ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                            headlineContent = { Text(fullExpr, fontWeight = FontWeight.Bold) },
                            supportingContent = { 
                                Column {
                                    if (item.note.isNotBlank()) {
                                        Text("Note: ${item.note}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Text(dateFormat.format(Date(item.timestamp)), style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        clipboardManager.setText(AnnotatedString(fullExpr))
                                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(onClick = { itemToDelete = item.id }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        if (itemToDelete != null) {
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("Delete Entry") },
                text = { Text("Are you sure you want to delete this history entry?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteGlobalHistory(itemToDelete!!)
                        itemToDelete = null
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) { Text("Cancel") }
                }
            )
        }

        if (selectedHistoryItemForGroup != null) {
            var selectedGroup by remember { mutableStateOf<com.example.data.LedgerGroup?>(null) }
            var expanded by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { selectedHistoryItemForGroup = null },
                title = { Text("Add to Group") },
                text = {
                    Column {
                        Text("Select a group to add this calculation to:")
                        Spacer(modifier = Modifier.height(16.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedGroup?.name ?: "Select Group",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                groups.forEach { group ->
                                    DropdownMenuItem(
                                        text = { Text(group.name) },
                                        onClick = {
                                            selectedGroup = group
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Row {
                        TextButton(
                            onClick = {
                                selectedGroup?.let { group ->
                                    viewModel.addHistoryToGroup(
                                        history = selectedHistoryItemForGroup!!,
                                        groupId = group.id,
                                        isIncome = false
                                    )
                                    selectedHistoryItemForGroup = null
                                }
                            },
                            enabled = selectedGroup != null
                        ) {
                            Text("- Expense", color = MaterialTheme.colorScheme.error)
                        }
                        TextButton(
                            onClick = {
                                selectedGroup?.let { group ->
                                    viewModel.addHistoryToGroup(
                                        history = selectedHistoryItemForGroup!!,
                                        groupId = group.id,
                                        isIncome = true
                                    )
                                    selectedHistoryItemForGroup = null
                                }
                            },
                            enabled = selectedGroup != null
                        ) {
                            Text("+ Income", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedHistoryItemForGroup = null }) { Text("Cancel") }
                }
            )
        }
    }
}
