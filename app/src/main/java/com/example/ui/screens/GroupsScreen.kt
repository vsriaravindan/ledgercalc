package com.example.ui.screens
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CalculatorViewModel
import com.example.data.LedgerGroup
import com.example.sync.SupabaseClient

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun GroupsScreen(
    viewModel: CalculatorViewModel,
    onGroupClick: (Int) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groups by viewModel.allGroups.collectAsStateWithLifecycle()
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<LedgerGroup?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf<LedgerGroup?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val createDoc = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.backupDatabase(context, uri) { success ->
                android.widget.Toast.makeText(context, if (success) "Backup Successful" else "Backup Failed", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
    val openDoc = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.restoreDatabase(context, uri) { success ->
                android.widget.Toast.makeText(context, if (success) "Restore Successful" else "Restore Failed", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                title = { Text("Ledgers") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Group")
            }
        },
        modifier = modifier
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Join Shared Folder button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { showJoinDialog = true },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text("Join Shared Ledger", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }

            if (groups.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No groups yet. Tap + to create one.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(groups, key = { it.id }) { group ->
                        var showOptionsDialog by remember { mutableStateOf(false) }
                        val isShared = viewModel.getSharedFolderIdForGroup(group.id) != null

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .combinedClickable(
                                    onClick = { onGroupClick(group.id) },
                                    onLongClick = { showOptionsDialog = true }
                                ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            color = MaterialTheme.colorScheme.surface,
                        ) {
                            ListItem(
                                colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                                headlineContent = { Text(group.name, fontWeight = FontWeight.Bold) },
                                leadingContent = {
                                    Surface(
                                        color = androidx.compose.ui.graphics.Color(group.color.toInt()).copy(alpha = 0.2f),
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = androidx.compose.ui.graphics.Color(group.color.toInt()),
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                },
                                trailingContent = {
                                    if (isShared) {
                                        Text("👥", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 8.dp))
                                    }
                                },
                            )
                        }

                        if (showOptionsDialog) {
                            AlertDialog(
                                onDismissRequest = { showOptionsDialog = false },
                                title = { Text("Options for '${group.name}'") },
                                text = {
                                    Column {
                                        TextButton(
                                            onClick = {
                                                showOptionsDialog = false
                                                showEditDialog = group
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Edit Ledger") }
                                        if (!isShared) {
                                            TextButton(
                                                onClick = {
                                                    showOptionsDialog = false
                                                    showShareDialog = group
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            ) { Text("Share to Friend", color = MaterialTheme.colorScheme.primary) }
                                        }
                                        TextButton(
                                            onClick = {
                                                showOptionsDialog = false
                                                viewModel.deleteGroup(group.id)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Delete Ledger", color = MaterialTheme.colorScheme.error) }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { showOptionsDialog = false }) { Text("Close") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Join dialog
    if (showJoinDialog) {
        JoinFolderDialog(
            viewModel = viewModel,
            onFolderJoined = { groupId, sharedFolderId ->
                viewModel.saveSharedFolderMapping(groupId, sharedFolderId)
            },
            onDismiss = { showJoinDialog = false },
        )
    }

    // Share dialog
    if (showShareDialog != null) {
        ShareFolderDialog(
            group = showShareDialog!!,
            viewModel = viewModel,
            onDismiss = { showShareDialog = null },
        )
    }

    if (showAddDialog || showEditDialog != null) {
        var name by remember { mutableStateOf(showEditDialog?.name ?: "") }
        var selectedColor by remember { mutableStateOf(showEditDialog?.color ?: 0xFF1976D2) }
        val colors = listOf(
            0xFF1976D2, 0xFF388E3C, 0xFFD32F2F, 0xFFFBC02D,
            0xFF8E24AA, 0xFFF57C00, 0xFF0288D1, 0xFF00796B
        )

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                showEditDialog = null
            },
            title = { Text(if (showEditDialog != null) "Edit Group" else "New Ledger Group", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Group Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Select Color", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colors.take(4).forEach { color ->
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { selectedColor = color },
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = androidx.compose.ui.graphics.Color(color.toInt()),
                                border = if (selectedColor == color) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null
                            ) {}
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colors.drop(4).forEach { color ->
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { selectedColor = color },
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = androidx.compose.ui.graphics.Color(color.toInt()),
                                border = if (selectedColor == color) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null
                            ) {}
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        if (showEditDialog != null) {
                            viewModel.updateGroup(showEditDialog!!.copy(name = name, color = selectedColor))
                        } else {
                            viewModel.createGroup(name, selectedColor)
                        }
                        showAddDialog = false
                        showEditDialog = null
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    showEditDialog = null
                }) { Text("Cancel") }
            }
        )
    }


    if (showSettingsDialog) {
        val symbols = listOf("$", "€", "£", "₹", "¥", "₩", "₽", "฿", "₫", "₱")
        val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

        androidx.compose.ui.window.Dialog(onDismissRequest = { showSettingsDialog = false }) {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Settings", style = MaterialTheme.typography.titleLarge, color = androidx.compose.ui.graphics.Color.White)
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                        item {
                            Text("Theme", style = MaterialTheme.typography.titleMedium, color = androidx.compose.ui.graphics.Color.White)
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                FilterChip(selected = themeMode == 0, onClick = { viewModel.setThemeMode(0) }, label = { Text("System") })
                                FilterChip(selected = themeMode == 1, onClick = { viewModel.setThemeMode(1) }, label = { Text("Light") })
                                FilterChip(selected = themeMode == 2, onClick = { viewModel.setThemeMode(2) }, label = { Text("Dark") })
                            }
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = androidx.compose.ui.graphics.Color(0x33FFFFFF))
                            Spacer(Modifier.height(16.dp))
                            Text("Currency", style = MaterialTheme.typography.titleMedium, color = androidx.compose.ui.graphics.Color.White)
                            Spacer(Modifier.height(8.dp))
                        }
                        items(symbols.chunked(5)) { rowSymbols ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                rowSymbols.forEach { symbol ->
                                    TextButton(onClick = { viewModel.setCurrency(symbol) }) {
                                        Text(symbol, fontSize = 24.sp, color = androidx.compose.ui.graphics.Color.White, fontWeight = if (currency == symbol) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = androidx.compose.ui.graphics.Color(0x33FFFFFF))
                            Spacer(Modifier.height(16.dp))
                            val includeWatermark by viewModel.includeWatermark.collectAsStateWithLifecycle()
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.setIncludeWatermark(!includeWatermark) }.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Include PDF Watermark", style = MaterialTheme.typography.titleMedium, color = androidx.compose.ui.graphics.Color.White)
                                Switch(checked = includeWatermark, onCheckedChange = { viewModel.setIncludeWatermark(it) })
                            }
                        }
                        item {
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = androidx.compose.ui.graphics.Color(0x33FFFFFF))
                            Spacer(Modifier.height(16.dp))
                            Text("Data Management", style = MaterialTheme.typography.titleMedium, color = androidx.compose.ui.graphics.Color.White)
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(onClick = { createDoc.launch("calculator_backup.json") }) { Text("Backup") }
                                Button(onClick = { openDoc.launch(arrayOf("application/json")) }) { Text("Restore") }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showSettingsDialog = false }) { Text("Close", color = androidx.compose.ui.graphics.Color.White) }
                    }
                }
            }
        }
    }
}
