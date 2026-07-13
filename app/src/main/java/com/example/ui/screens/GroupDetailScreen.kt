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
    LaunchedEffect(groupId) {
        viewModel.setActiveGroup(groupId)
    }

    val group by viewModel.activeGroup.collectAsStateWithLifecycle()
    val transactions by viewModel.activeGroupTransactions.collectAsStateWithLifecycle()
    val balance by viewModel.activeGroupBalance.collectAsStateWithLifecycle()
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val context = androidx.compose.ui.platform.LocalContext.current
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

    Scaffold(containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                title = { Text(group?.name ?: "Group Ledger") },
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
                                    // Share via Android share sheet
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
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SmallFloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Dialpad, contentDescription = "Use Calculator")
                }
                FloatingActionButton(onClick = { showManualAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Entry")
                }
            }
        },
        modifier = modifier
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Balance Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Text(
                                "ACTIVE GROUP",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        group?.color?.let { groupColor ->
                            Surface(
                                color = androidx.compose.ui.graphics.Color(groupColor.toInt()).copy(alpha = 0.2f),
                                shape = androidx.compose.foundation.shape.CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = androidx.compose.ui.graphics.Color(groupColor.toInt()),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Running Balance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    val isInteger = balance % 1.0 == 0.0
                    val balStr = if (isInteger) balance.toLong().toString() else String.format("%.2f", balance)
                    Text(
                        text = (if (balance >= 0) currency else "-$currency") + if (balance >= 0) balStr else balStr.drop(1),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // History List
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(sortedTransactions, key = { it.id }) { tx ->
                    val isInteger = tx.amount % 1.0 == 0.0
                    val amountStr = if (isInteger) tx.amount.toLong().toString() else String.format("%.2f", tx.amount)
                    val color = if (tx.amount >= 0) com.example.ui.theme.GlowTeal else MaterialTheme.colorScheme.error
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = { showEditDialog = tx }
                            ),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                            headlineContent = { Text(tx.label.ifEmpty { "Entry" }, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(dateFormat.format(Date(tx.timestamp))) },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = (if (tx.amount > 0) "+" else "") + amountStr,
                                        color = color,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    IconButton(onClick = {
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(amountStr))
                                        android.widget.Toast.makeText(context, "Copied amount", android.widget.Toast.LENGTH_SHORT).show()
                                    }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    IconButton(onClick = { transactionToDelete = tx.id }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
        
        if (transactionToDelete != null) {
            AlertDialog(
                onDismissRequest = { transactionToDelete = null },
                title = { Text("Delete Transaction") },
                text = { Text("Are you sure you want to delete this transaction?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteTransaction(transactionToDelete!!)
                        transactionToDelete = null
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { transactionToDelete = null }) { Text("Cancel") }
                }
            )
        }

        if (showAddDialog) {
            ModalBottomSheet(
                onDismissRequest = { showAddDialog = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                modifier = Modifier.fillMaxHeight(0.9f)
            ) {
                CalculatorScreen(viewModel = viewModel, onSaveComplete = { showAddDialog = false })
            }
        }

        if (showManualAddDialog || showEditDialog != null) {
            val isEdit = showEditDialog != null
            var amountStr by remember { mutableStateOf(if (isEdit) showEditDialog?.amount?.toString() ?: "" else "") }
            var note by remember { mutableStateOf(if (isEdit) showEditDialog?.label ?: "" else "") }
            var amountError by remember { mutableStateOf(false) }
            val txExpression = showEditDialog?.expression ?: ""
            
            fun validateAndSave() {
                val amt = amountStr.toDoubleOrNull()
                if (amt == null) {
                    amountError = true
                    return
                }
                amountError = false
                if (isEdit) {
                    viewModel.updateTransaction(showEditDialog!!.copy(amount = amt, label = note))
                } else {
                    viewModel.addDirectEntry(groupId, amt, note)
                }
                showManualAddDialog = false
                showEditDialog = null
            }
            
            AlertDialog(
                onDismissRequest = { 
                    showManualAddDialog = false
                    showEditDialog = null 
                },
                title = { Text(if (isEdit) "Edit Entry" else "Manual Entry") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isEdit && txExpression.isNotEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = txExpression,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(12.dp),
                                )
                            }
                        }
                        OutlinedTextField(
                            value = amountStr,
                            onValueChange = { amountStr = it; amountError = false },
                            label = { Text("Amount (e.g. 50 or -20)") },
                            isError = amountError,
                            supportingText = if (amountError) {{ Text("Enter a valid number") }} else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Label") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { validateAndSave() }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showManualAddDialog = false
                        showEditDialog = null 
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
