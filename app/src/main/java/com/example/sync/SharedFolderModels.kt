package com.example.sync

// ── Shared Folder ──────────────────────────────────────
data class SharedFolder(
    val id: Long = 0,
    val localFolderId: Long,
    val folderName: String,
    val secretCode: String,
    val ownerDeviceId: String,
    val ownerName: String,
    val permission: String = "full",
    val createdAt: String = "",
    val usedCount: Int = 0,
)

// ── Shared Member ──────────────────────────────────────
data class SharedMember(
    val id: Long = 0,
    val sharedFolderId: Long,
    val deviceId: String,
    val displayName: String,
    val isOwner: Boolean = false,
    val isActive: Boolean = true,
    val joinedAt: String = "",
)

// ── Shared Entry (synced from TransactionEntry) ───────
data class SharedEntry(
    val id: Long = 0,
    val sharedFolderId: Long,
    val localEntryId: Long? = null,
    val amount: Double,
    val label: String = "",
    val expression: String = "",
    val createdBy: String,
    val createdAt: String = "",
    val updatedAt: String? = null,
    val updatedBy: String? = null,
    val deletedAt: String? = null,
    val deletedBy: String? = null,
)

// ── Sync Event (audit log) ─────────────────────────────
data class SyncEvent(
    val id: Long = 0,
    val sharedFolderId: Long,
    val eventType: String,
    val entryId: Long? = null,
    val actorName: String,
    val amount: Double? = null,
    val label: String? = null,
    val expression: String? = null,
    val oldAmount: Double? = null,
    val oldLabel: String? = null,
    val oldExpression: String? = null,
    val createdAt: String = "",
)

// ── Response Wrappers ──────────────────────────────────
data class SharedFolderJoinResponse(
    val folder: SharedFolder,
    val member: SharedMember,
    val entries: List<SharedEntry>,
)

data class PendingShares(
    val owned: List<SharedFolder> = emptyList(),
    val joined: List<SharedFolder> = emptyList(),
)
