package com.example.sync

import android.content.Context
import com.example.data.TransactionEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Random

/**
 * Business logic for sharing/joining folders and syncing entries.
 */
object SharedFolderRepository {

    private val random = Random()

    /** Generate a 6-digit numeric code */
    fun generateCode(): String {
        val code = (100000 + random.nextInt(900000)).toString()
        return code
    }

    // ── Share a folder ──────────────────────────────────

    suspend fun shareFolder(
        context: Context,
        localFolderId: Long,
        folderName: String,
        permission: String,
    ): Result<SharedFolder> = withContext(Dispatchers.IO) {
        val deviceId = SupabaseClient.getDeviceId(context)
        val ownerName = SupabaseClient.getDisplayName(context)
        if (ownerName.isBlank()) {
            return@withContext Result.failure(Exception("Set your display name first in Settings"))
        }

        val code = generateCode()
        val folder = SharedFolder(
            localFolderId = localFolderId,
            folderName = folderName,
            secretCode = code,
            ownerDeviceId = deviceId,
            ownerName = ownerName,
            permission = permission,
        )

        val result = SupabaseClient.createSharedFolder(folder)
        if (result.isFailure) return@withContext result

        val created = result.getOrThrow()

        // Add owner as first member
        SupabaseClient.addMember(SharedMember(
            sharedFolderId = created.id,
            deviceId = deviceId,
            displayName = ownerName,
            isOwner = true,
        ))

        Result.success(created)
    }

    // ── Join a folder via code ──────────────────────────

    suspend fun joinFolder(
        context: Context,
        code: String,
    ): Result<SharedFolderJoinResponse> = withContext(Dispatchers.IO) {
        val deviceId = SupabaseClient.getDeviceId(context)
        val displayName = SupabaseClient.getDisplayName(context)
        if (displayName.isBlank()) {
            return@withContext Result.failure(Exception("Set your display name first in Settings"))
        }

        // Look up code
        val lookup = SupabaseClient.getSharedFolderByCode(code)
        if (lookup.isFailure) return@withContext Result.failure(Exception("Network error. Try again."))
        val folders = lookup.getOrThrow()
        if (folders.isEmpty()) return@withContext Result.failure(Exception("Invalid code. Check and try again."))

        val folder = folders.first()

        // Check if already a member
        val memberCheck = SupabaseClient.isMember(folder.id, deviceId)
        if (memberCheck.isSuccess && memberCheck.getOrThrow()) {
            return@withContext Result.failure(Exception("You already joined this folder."))
        }

        // Check if owner tries to join own folder
        if (folder.ownerDeviceId == deviceId) {
            return@withContext Result.failure(Exception("You own this folder. Open it from your groups."))
        }

        // Increment used_count
        SupabaseClient.incrementUsedCode(folder.id)

        // Add member
        val member = SupabaseClient.addMember(SharedMember(
            sharedFolderId = folder.id,
            deviceId = deviceId,
            displayName = displayName,
        ))
        if (member.isFailure) return@withContext Result.failure(Exception("Failed to join."))

        // Fetch existing entries
        val entries = SupabaseClient.getActiveEntries(folder.id)
        val entryList = entries.getOrElse { emptyList() }

        Result.success(SharedFolderJoinResponse(
            folder = folder,
            member = member.getOrThrow(),
            entries = entryList,
        ))
    }

    // ── Get user's shared folders (owned + joined) ──────

    suspend fun getMyShares(context: Context): Result<PendingShares> = withContext(Dispatchers.IO) {
        val deviceId = SupabaseClient.getDeviceId(context)

        val owned = SupabaseClient.getSharedFoldersByDevice(deviceId)

        Result.success(PendingShares(
            owned = owned.getOrElse { emptyList() },
        ))
    }

    // ── Sync operations ─────────────────────────────────

    suspend fun syncAddEntry(
        context: Context,
        sharedFolderId: Long,
        amount: Double,
        label: String,
        expression: String,
        localEntryId: Long? = null,
    ): Result<SharedEntry> {
        val actorName = SupabaseClient.getDisplayName(context)

        val entry = SharedEntry(
            sharedFolderId = sharedFolderId,
            localEntryId = localEntryId,
            amount = amount,
            label = label,
            expression = expression,
            createdBy = actorName,
        )
        val result = SupabaseClient.addEntry(entry)
        if (result.isFailure) return result

        val created = result.getOrThrow()

        // Audit event
        SupabaseClient.addSyncEvent(SyncEvent(
            sharedFolderId = sharedFolderId,
            eventType = "added",
            entryId = created.id,
            actorName = actorName,
            amount = amount,
            label = label,
            expression = expression,
        ))

        return Result.success(created)
    }

    suspend fun syncDeleteEntry(
        context: Context,
        sharedFolderId: Long,
        entryId: Long,
        amount: Double,
        label: String,
        expression: String,
    ) {
        val actorName = SupabaseClient.getDisplayName(context)

        SupabaseClient.softDeleteEntry(entryId, actorName)

        // Audit event
        SupabaseClient.addSyncEvent(SyncEvent(
            sharedFolderId = sharedFolderId,
            eventType = "deleted",
            entryId = entryId,
            actorName = actorName,
            amount = amount,
            label = label,
            expression = expression,
        ))
    }

    suspend fun syncEditEntry(
        context: Context,
        sharedFolderId: Long,
        entryId: Long,
        newAmount: Double,
        newLabel: String,
        oldAmount: Double,
        oldLabel: String,
        oldExpression: String,
    ) {
        val actorName = SupabaseClient.getDisplayName(context)

        SupabaseClient.updateEntry(entryId, newAmount, newLabel, actorName)

        // Audit event
        SupabaseClient.addSyncEvent(SyncEvent(
            sharedFolderId = sharedFolderId,
            eventType = "edited",
            entryId = entryId,
            actorName = actorName,
            amount = newAmount,
            label = newLabel,
            oldAmount = oldAmount,
            oldLabel = oldLabel,
            oldExpression = oldExpression,
        ))
    }

    // ── Fetch data ──────────────────────────────────────

    suspend fun getFolderEntries(sharedFolderId: Long): Result<List<SharedEntry>> {
        return SupabaseClient.getActiveEntries(sharedFolderId)
    }

    suspend fun getFolderEvents(sharedFolderId: Long): Result<List<SyncEvent>> {
        return SupabaseClient.getSyncEvents(sharedFolderId)
    }
}
