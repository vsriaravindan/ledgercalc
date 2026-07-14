package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.LedgerGroup
import com.example.ui.CalculatorViewModel

// ── SHARE DIALOG ────────────────────────────────────────

@Composable
fun ShareFolderDialog(
    group: LedgerGroup,
    viewModel: CalculatorViewModel,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var permission by remember { mutableStateOf("full") }
    var sharedCode by remember { mutableStateOf<String?>(null) }
    var isSharing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Share Ledger", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Text(group.name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)

                if (sharedCode != null) {
                    // Show code
                    Text("Share this code with your friend:", style = MaterialTheme.typography.bodyMedium)
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = sharedCode!!,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 8.sp,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(sharedCode!!))
                                android.widget.Toast.makeText(context, "Code copied", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Copy")
                        }
                        Button(
                            onClick = {
                                val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(android.content.Intent.EXTRA_TEXT, "Join my shared ledger \"${group.name}\" on LedgerCalc!\n\nSecret Code: ${sharedCode!!}")
                                }
                                context.startActivity(android.content.Intent.createChooser(sendIntent, "Share via"))
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Share")
                        }
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Done") }

                } else {
                    // Permission selector
                    Text("Set permission for your friend:", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = permission == "full",
                            onClick = { permission = "full" },
                            label = { Text("Full access") },
                            modifier = Modifier.weight(1f),
                        )
                        FilterChip(
                            selected = permission == "read_only",
                            onClick = { permission = "read_only" },
                            label = { Text("Read only") },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    if (error != null) {
                        Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (com.example.sync.SupabaseClient.getDisplayName(context).isBlank()) {
                                    error = "Set your display name first in Settings"
                                    return@Button
                                }
                                isSharing = true
                                error = null
                                viewModel.shareFolder(context, group.id, group.name, permission) { result ->
                                    isSharing = false
                                    if (result.isSuccess) {
                                        sharedCode = result.getOrThrow().secretCode
                                    } else {
                                        error = result.exceptionOrNull()?.message ?: "Failed to share"
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isSharing,
                        ) {
                            if (isSharing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Generate Code")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── JOIN DIALOG ─────────────────────────────────────────

@Composable
fun JoinFolderDialog(
    viewModel: CalculatorViewModel,
    onFolderJoined: (groupId: Int, sharedFolderId: Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }
    var isJoining by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Join Shared Ledger", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Text("Enter the 6-digit code shared by your friend:", style = MaterialTheme.typography.bodyMedium)

                OutlinedTextField(
                    value = code,
                    onValueChange = { if (it.length <= 6) code = it.filter { c -> c.isDigit() } },
                    label = { Text("Secret Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 32.sp,
                        letterSpacing = 8.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                )

                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (code.length < 6) {
                                error = "Enter the full 6-digit code"
                                return@Button
                            }
                            if (com.example.sync.SupabaseClient.getDisplayName(context).isBlank()) {
                                error = "Set your display name first in Settings"
                                return@Button
                            }
                            isJoining = true
                            error = null
                            viewModel.joinFolder(context, code) { result ->
                                isJoining = false
                                if (result.isSuccess) {
                                    val resp = result.getOrThrow()
                                    // Create a local group with the shared folder's name
                                    viewModel.createGroup(resp.folder.folderName, 0xFF7C3AED)
                                    // We'll handle the mapping in the success callback
                                    onFolderJoined(resp.folder.localFolderId.toInt(), resp.folder.id)
                                    onDismiss()
                                    android.widget.Toast.makeText(context, "Joined! Shared ledger added.", android.widget.Toast.LENGTH_LONG).show()
                                } else {
                                    error = result.exceptionOrNull()?.message ?: "Failed to join"
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = code.length == 6 && !isJoining,
                    ) {
                        if (isJoining) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Join")
                        }
                    }
                }
            }
        }
    }
}
