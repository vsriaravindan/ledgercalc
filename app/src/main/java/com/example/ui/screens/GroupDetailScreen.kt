package com.example.ui.screens
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CalculatorViewModel
import com.example.data.TransactionEntry
import com.example.sync.SharedFolderRepository
import com.example.sync.SyncEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GroupDetailScreen(
    groupId: Int,
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedFolderId = remember { viewModel.getSharedFolderIdForGroup(groupId) }
    val isShared = sharedFolderId != null
    val isReadOnly = viewModel.sharedFolderPermission.collectAsStateWithLifecycle().value == "read_only"

    LaunchedEffect(groupId) {
        viewModel.setActiveGroup(groupId)
        if (sharedFolderId != null) {
            viewModel.setSharedFolderInfo(sharedFolderId)
            // Fetch permission from Supabase
            viewModel.refreshSharedData(sharedFolderId)
            viewModel.pullRemoteEntries(sharedFolderId, groupId)
            viewModel.loadSyncEvents(sharedFolderId)
        }
    }

    // Auto-poll every 10 seconds when shared folder is open
    LaunchedEffect(sharedFolderId) {
        if (sharedFolderId != null) {
            while (true) {
                kotlinx.coroutines.delay(10_000)
                // First process deletes, then pull new entries
                viewModel.processRemoteDeletes(sharedFolderId, groupId)
                viewModel.pullRemoteEntries(sharedFolderId, groupId)
                viewModel.loadSyncEvents(sharedFolderId)
            }
        }
    }

    val group by viewModel.activeGroup.collectAsStateWithLifecycle()
    val transactions by viewModel.activeGroupTransactions.collectAsStateWithLifecycle()
    val balance by viewModel.activeGroupBalance.collectAsStateWithLifecycle()
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val syncEvents by viewModel.syncEvents.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val sortPrefs = remember { context.getSharedPreferences("group_sort", android.content.Context.MODE_PRIVATE) }

    var sortOption by remember { mutableStateOf(sortPrefs.getString("sort_$groupId", "Newest") ?: "Newest") }
    var showSortMenu by remember { mutableStateOf(false) }

    fun saveSortOption(option: String) {
        sortOption = option
        sortPrefs.edit().putString("sort_$groupId", option).apply()
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showManualAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<TransactionEntry?>(null) }
    var transactionToDelete by remember { mutableStateOf<Int?>(null) }

    val sortedTransactions = remember(transactions, sortOption) {
        when (sortOption) {
            "Oldest" -> transactions.sortedBy { it.timestamp }
            "Amount (High to Low)" -> transactions.sortedByDescending { it.amount }
            "Amount (Low to High)" -> transactions.sortedBy { it.amount }
            else -> transactions.sortedByDescending { it.timestamp }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var isExporting by remember { mutableStateOf(false) }
    val includeWatermark by viewModel.includeWatermark.collectAsStateWithLifecycle()

    // ── Build merged list: local transactions + filtered audit events ──
    val filteredEvents = remember(syncEvents, sharedFolderId) {
        syncEvents.filter { it.sharedFolderId == (sharedFolderId ?: 0L) }
    }
    val mergedItems = remember(sortedTransactions, filteredEvents) {
        val items = mutableListOf<Any>()
        items.addAll(sortedTransactions)
        items.addAll(filteredEvents.filter { it.eventType != "added" })
        items
    }

    Scaffold(containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isShared) Text("👥 ", style = MaterialTheme.typography.titleMedium)
                        Text(group?.name ?: "Group Ledger")
                    }
                },
                navigationIcon = {
                    Row {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (isShared) {
                        IconButton(onClick = {
                            sharedFolderId?.let { viewModel.refreshSyncEvents(it) }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                    IconButton(onClick = {
                        if (group != null && transactions.isNotEmpty()) {
                            isExporting = true
                            coroutineScope.launch {
                                val filePath = com.example.utils.PdfExporter.exportGroupHistoryToPdf(
                                    context, group!!, transactions, currency, includeWatermark
                                )
                                isExporting = false
                                if (filePath != null) {
                                    val uri = if (filePath.startsWith("content://")) {
                                        android.net.Uri.parse(filePath)
                                    } else {
                                        androidx.core.content.FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            java.io.File(filePath)
                                        )
                                    }
                                    android.widget.Toast.makeText(context, "PDF saved ✓", android.widget.Toast.LENGTH_SHORT).show()
                                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share PDF"))
                                } else {
                                    android.widget.Toast.makeText(context, "Failed to generate PDF", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            android.widget.Toast.makeText(context, "No entries to export", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export PDF")
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            listOf("Newest", "Oldest", "Amount (High to Low)", "Amount (Low to High)").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = { saveSortOption(option); showSortMenu = false }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isReadOnly) {
                val canEdit = !isShared || viewModel.isOnline(context)
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SmallFloatingActionButton(
                        onClick = {
                            if (canEdit) showAddDialog = true
                            else android.widget.Toast.makeText(context, "Connect to internet to edit shared ledger", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    ) { Icon(Icons.Default.Dialpad, contentDescription = "Use Calculator") }
                    FloatingActionButton(
                        onClick = {
                            if (canEdit) showManualAddDialog = true
                            else android.widget.Toast.makeText(context, "Connect to internet to edit shared ledger", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    ) { Icon(Icons.Default.Add, contentDescription = "Add Entry") }
                }
            }
        },
        modifier = modifier
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isReadOnly) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("📖 Read-only — contact folder owner to edit",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary)
                }
            }

            if (isShared && isSyncing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Balance Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.Start) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(percent = 50)) {
                            Text(
                                if (isShared) "SHARED GROUP" else "ACTIVE GROUP",
                                color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        }
                        group?.color?.let { groupColor ->
                            Surface(color = androidx.compose.ui.graphics.Color(groupColor.toInt()).copy(alpha = 0.2f),
                                shape = androidx.compose.foundation.shape.CircleShape, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = androidx.compose.ui.graphics.Color(groupColor.toInt()), modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Running Balance", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    val isInteger = balance % 1.0 == 0.0
                    val balStr = if (isInteger) balance.toLong().toString() else String.format("%.2f", balance)
                    Text(text = (if (balance >= 0) currency else "-$currency") + if (balance >= 0) balStr else balStr.drop(1),
                        style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // History List
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(mergedItems) { item ->
                    when (item) {
                        is TransactionEntry -> {
                            val isInt = item.amount % 1.0 == 0.0
                            val amtStr = if (isInt) item.amount.toLong().toString() else String.format("%.2f", item.amount)
                            val color = if (item.amount >= 0) com.example.ui.theme.GlowTeal else MaterialTheme.colorScheme.error

                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                                    .combinedClickable(onClick = {},
                                        onLongClick = {
                                            if (!isReadOnly) {
                                                val canEdit = !isShared || viewModel.isOnline(context)
                                                if (canEdit) showEditDialog = item
                                                else android.widget.Toast.makeText(context, "Connect to internet to edit", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }),
                                shape = RoundedCornerShape(24.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                ListItem(
                                    colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                                    headlineContent = { Text(item.label.ifEmpty { "Entry" }, fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text(dateFormat.format(Date(item.timestamp))) },
                                    trailingContent = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = (if (item.amount > 0) "+" else "") + amtStr, color = color,
                                                fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                            Spacer(Modifier.width(8.dp))
                                            IconButton(onClick = {
                                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(amtStr))
                                                android.widget.Toast.makeText(context, "Copied amount", android.widget.Toast.LENGTH_SHORT).show()
                                            }, modifier = Modifier.size(24.dp)) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                            }
                                            if (!isReadOnly) {
                                                Spacer(Modifier.width(4.dp))
                                                IconButton(onClick = { transactionToDelete = item.id }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        is SyncEvent -> {
                            val icon = when (item.eventType) { "edited" -> "📝"; "deleted" -> "🗑️"; else -> "📌" }
                            val detail = when (item.eventType) {
                                "edited" -> {
                                    val oldAmt = item.oldAmount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else String.format("%.2f", it) } ?: ""
                                    val newAmt = item.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else String.format("%.2f", it) } ?: ""
                                    "${item.actorName} edited \"${item.label ?: item.oldLabel ?: ""}\": ${oldAmt} → ${newAmt}"
                                }
                                "deleted" -> {
                                    val amt = item.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else String.format("%.2f", it) } ?: ""
                                    "${item.actorName} deleted \"${item.label ?: ""}\": ${amt}"
                                }
                                else -> ""
                            }
                            if (detail.isNotBlank()) {
                                Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)) {
                                    Text(text = "$icon $detail", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete confirm
        if (transactionToDelete != null) {
            AlertDialog(
                onDismissRequest = { transactionToDelete = null },
                title = { Text("Delete Transaction") },
                text = { Text("Are you sure you want to delete this transaction?") },
                confirmButton = {
                    TextButton(onClick = {
                        val txId = transactionToDelete!!
                        val tx = transactions.find { it.id == txId }
                        coroutineScope.launch {
                            if (isShared && sharedFolderId != null && tx != null) {
                                SharedFolderRepository.syncDeleteEntry(
                                    context, sharedFolderId, tx.id.toLong(),
                                    tx.amount, tx.label, tx.expression
                                )
                            }
                            viewModel.deleteTransaction(txId)
                            if (sharedFolderId != null) viewModel.refreshSyncEvents(sharedFolderId)
                        }
                        transactionToDelete = null
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = { TextButton(onClick = { transactionToDelete = null }) { Text("Cancel") } }
            )
        }

        // Calculator bottom sheet
        if (showAddDialog) {
            ModalBottomSheet(
                onDismissRequest = { showAddDialog = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                modifier = Modifier.fillMaxHeight(0.9f)
            ) {
                CalculatorScreen(viewModel = viewModel, onSaveComplete = {
                    showAddDialog = false
                    if (sharedFolderId != null) viewModel.refreshSyncEvents(sharedFolderId)
                })
            }
        }

        // Manual add / Edit dialog
        if (showManualAddDialog || showEditDialog != null) {
            val isEdit = showEditDialog != null
            var amountStr by remember { mutableStateOf(if (isEdit) showEditDialog?.amount?.toString() ?: "" else "") }
            var note by remember { mutableStateOf(if (isEdit) showEditDialog?.label ?: "" else "") }
            var amountError by remember { mutableStateOf(false) }
            val txExpression = showEditDialog?.expression ?: ""

            fun validateAndSave() {
                val amt = amountStr.toDoubleOrNull()
                if (amt == null) { amountError = true; return }
                amountError = false
                coroutineScope.launch {
                    if (isEdit) {
                        val oldTx = showEditDialog!!
                        viewModel.updateTransaction(oldTx.copy(amount = amt, label = note))
                        if (isShared && sharedFolderId != null) {
                            SharedFolderRepository.syncEditEntry(
                                context, sharedFolderId, oldTx.id.toLong(),
                                amt, note, oldTx.amount, oldTx.label, oldTx.expression
                            )
                            viewModel.refreshSyncEvents(sharedFolderId)
                        }
                    } else {
                        viewModel.addDirectEntry(groupId, amt, note)
                        if (sharedFolderId != null) viewModel.refreshSyncEvents(sharedFolderId)
                    }
                }
                showManualAddDialog = false
                showEditDialog = null
            }

            AlertDialog(
                onDismissRequest = { showManualAddDialog = false; showEditDialog = null },
                title = { Text(if (isEdit) "Edit Entry" else "Manual Entry") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isEdit && txExpression.isNotEmpty()) {
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp)) {
                                Text(text = txExpression, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                    color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp))
                            }
                        }
                        OutlinedTextField(value = amountStr, onValueChange = { amountStr = it; amountError = false },
                            label = { Text("Amount (e.g. 50 or -20)") }, isError = amountError,
                            supportingText = if (amountError) {{ Text("Enter a valid number") }} else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Label") }, singleLine = true)
                    }
                },
                confirmButton = { Button(onClick = { validateAndSave() }) { Text("Save") } },
                dismissButton = { TextButton(onClick = { showManualAddDialog = false; showEditDialog = null }) { Text("Cancel") } }
            )
        }
    }
}
