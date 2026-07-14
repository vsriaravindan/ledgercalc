package com.example.sync

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Lightweight Supabase REST client using OkHttp.
 * Handles: CRUD on shared_folders, shared_members, shared_entries, sync_events.
 */
object SupabaseClient {

    private const val SUPABASE_URL = "https://iuhbtmfdvfuurtkszvar.supabase.co"
    private const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml1aGJ0bWZkdmZ1dXJ0a3N6dmFyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODM2NzgwNTEsImV4cCI6MjA5OTI1NDA1MX0.anHXCRBktYo4tWRCRAkkE0yQwG_GSujfo8UWvdLedeU"

    private val JSON = "application/json".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun headers() = Headers.Builder()
        .add("apikey", ANON_KEY)
        .add("Authorization", "Bearer $ANON_KEY")
        .add("Content-Type", "application/json")
        .add("Prefer", "return=representation")
        .build()

    // ── Device ID ───────────────────────────────────────
    private var _deviceId: String? = null

    fun getDeviceId(context: Context): String {
        if (_deviceId == null) {
            val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
            _deviceId = prefs.getString("device_id", null)
            if (_deviceId == null) {
                _deviceId = UUID.randomUUID().toString()
                prefs.edit().putString("device_id", _deviceId).apply()
            }
        }
        return _deviceId!!
    }

    fun getDisplayName(context: Context): String {
        val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
        return prefs.getString("display_name", "") ?: ""
    }

    fun setDisplayName(context: Context, name: String) {
        context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
            .edit().putString("display_name", name).apply()
    }

    // ── REST Helpers ────────────────────────────────────

    private suspend fun getJson(
        table: String,
        query: String = "",
    ): Result<JSONArray> = withContext(Dispatchers.IO) {
        try {
            val url = "$SUPABASE_URL/rest/v1/$table?$query"
            val request = Request.Builder().url(url).headers(headers()).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("HTTP ${response.code}: $body"))
            }
            Result.success(JSONArray(body))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun postJson(
        table: String,
        jsonBody: String,
    ): Result<JSONArray> = withContext(Dispatchers.IO) {
        try {
            val url = "$SUPABASE_URL/rest/v1/$table"
            val request = Request.Builder()
                .url(url).headers(headers())
                .post(jsonBody.toRequestBody(JSON))
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("HTTP ${response.code}: $body"))
            }
            Result.success(JSONArray(body))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun patchRequest(
        table: String,
        query: String,
        jsonBody: String,
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = "$SUPABASE_URL/rest/v1/$table?$query"
            val request = Request.Builder()
                .url(url).headers(headers())
                .patch(jsonBody.toRequestBody(JSON))
                .build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val b = response.body?.string() ?: ""
                return@withContext Result.failure(IOException("HTTP ${response.code}: $b"))
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── JSON parsing helpers ────────────────────────────

    private fun JSONArray.toSharedFolderList(): List<SharedFolder> {
        val list = mutableListOf<SharedFolder>()
        for (i in 0 until length()) {
            val obj = getJSONObject(i)
            list.add(SharedFolder(
                id = obj.optLong("id"),
                localFolderId = obj.optLong("local_folder_id"),
                folderName = obj.optString("folder_name"),
                secretCode = obj.optString("secret_code"),
                ownerDeviceId = obj.optString("owner_device_id"),
                ownerName = obj.optString("owner_name"),
                permission = obj.optString("permission", "full"),
                currency = obj.optString("currency", "$"),
                createdAt = obj.optString("created_at"),
                usedCount = obj.optInt("used_count"),
            ))
        }
        return list
    }

    private fun JSONArray.toSharedMemberList(): List<SharedMember> {
        val list = mutableListOf<SharedMember>()
        for (i in 0 until length()) {
            val obj = getJSONObject(i)
            list.add(SharedMember(
                id = obj.optLong("id"),
                sharedFolderId = obj.optLong("shared_folder_id"),
                deviceId = obj.optString("device_id"),
                displayName = obj.optString("display_name"),
                isOwner = obj.optBoolean("is_owner"),
                isActive = obj.optBoolean("is_active", true),
                joinedAt = obj.optString("joined_at"),
            ))
        }
        return list
    }

    private fun JSONArray.toSharedEntryList(): List<SharedEntry> {
        val list = mutableListOf<SharedEntry>()
        for (i in 0 until length()) {
            val obj = getJSONObject(i)
            list.add(SharedEntry(
                id = obj.optLong("id"),
                sharedFolderId = obj.optLong("shared_folder_id"),
                localEntryId = if (obj.isNull("local_entry_id")) null else obj.optLong("local_entry_id"),
                amount = obj.optDouble("amount"),
                label = obj.optString("label"),
                expression = obj.optString("expression"),
                createdBy = obj.optString("created_by"),
                createdAt = obj.optString("created_at"),
                updatedAt = if (obj.isNull("updated_at")) null else obj.optString("updated_at"),
                updatedBy = if (obj.isNull("updated_by")) null else obj.optString("updated_by"),
                deletedAt = if (obj.isNull("deleted_at")) null else obj.optString("deleted_at"),
                deletedBy = if (obj.isNull("deleted_by")) null else obj.optString("deleted_by"),
            ))
        }
        return list
    }

    private fun JSONArray.toSyncEventList(): List<SyncEvent> {
        val list = mutableListOf<SyncEvent>()
        for (i in 0 until length()) {
            val obj = getJSONObject(i)
            list.add(SyncEvent(
                id = obj.optLong("id"),
                sharedFolderId = obj.optLong("shared_folder_id"),
                eventType = obj.optString("event_type"),
                entryId = if (obj.isNull("entry_id")) null else obj.optLong("entry_id"),
                actorName = obj.optString("actor_name"),
                amount = if (obj.isNull("amount")) null else obj.optDouble("amount"),
                label = if (obj.isNull("label")) null else obj.optString("label"),
                expression = if (obj.isNull("expression")) null else obj.optString("expression"),
                oldAmount = if (obj.isNull("old_amount")) null else obj.optDouble("old_amount"),
                oldLabel = if (obj.isNull("old_label")) null else obj.optString("old_label"),
                oldExpression = if (obj.isNull("old_expression")) null else obj.optString("old_expression"),
                createdAt = obj.optString("created_at"),
            ))
        }
        return list
    }

    // ── API: Shared Folders ─────────────────────────────

    suspend fun createSharedFolder(folder: SharedFolder): Result<SharedFolder> {
        val json = JSONObject().apply {
            put("local_folder_id", folder.localFolderId)
            put("folder_name", folder.folderName)
            put("secret_code", folder.secretCode)
            put("owner_device_id", folder.ownerDeviceId)
            put("owner_name", folder.ownerName)
            put("permission", folder.permission)
            put("currency", folder.currency)
        }.toString()
        val result = postJson("shared_folders", json)
        return result.map { it.toSharedFolderList().firstOrNull() ?: folder }
    }

    suspend fun getSharedFolderByCode(code: String): Result<List<SharedFolder>> {
        val result = getJson("shared_folders", "secret_code=eq.$code&limit=1")
        return result.map { it.toSharedFolderList() }
    }

    suspend fun getSharedFoldersByDevice(deviceId: String): Result<List<SharedFolder>> {
        return getJson("shared_folders", "owner_device_id=eq.$deviceId").map { it.toSharedFolderList() }
    }

    suspend fun incrementUsedCode(sharedFolderId: Long): Result<Boolean> {
        return patchRequest("shared_folders", "id=eq.$sharedFolderId",
            """{"used_count": 1}""")
    }

    suspend fun getSharedFolderById(sharedFolderId: Long): Result<List<SharedFolder>> {
        return getJson("shared_folders", "id=eq.$sharedFolderId&limit=1").map { it.toSharedFolderList() }
    }

    suspend fun updateCurrency(sharedFolderId: Long, currency: String): Result<Boolean> {
        return patchRequest("shared_folders", "id=eq.$sharedFolderId",
            """{"currency": "$currency"}""")
    }

    // ── API: Shared Members ─────────────────────────────

    suspend fun addMember(member: SharedMember): Result<SharedMember> {
        val json = JSONObject().apply {
            put("shared_folder_id", member.sharedFolderId)
            put("device_id", member.deviceId)
            put("display_name", member.displayName)
            put("is_owner", member.isOwner)
        }.toString()
        val result = postJson("shared_members", json)
        return result.map { it.toSharedMemberList().firstOrNull() ?: member }
    }

    suspend fun getMembers(sharedFolderId: Long): Result<List<SharedMember>> {
        return getJson("shared_members", "shared_folder_id=eq.$sharedFolderId")
            .map { it.toSharedMemberList() }
    }

    suspend fun isMember(sharedFolderId: Long, deviceId: String): Result<Boolean> {
        val result = getJson("shared_members",
            "shared_folder_id=eq.$sharedFolderId&device_id=eq.$deviceId&is_active=eq.true&limit=1")
        return result.map { it.toSharedMemberList().isNotEmpty() }
    }

    // ── API: Shared Entries ─────────────────────────────

    suspend fun getActiveEntries(sharedFolderId: Long): Result<List<SharedEntry>> {
        return getJson("shared_entries",
            "shared_folder_id=eq.$sharedFolderId&deleted_at=is.null&order=created_at.desc")
            .map { it.toSharedEntryList() }
    }

    /** Fetch entries including soft-deleted ones, for sync cleanup */
    suspend fun getAllEntries(sharedFolderId: Long): Result<List<SharedEntry>> {
        return getJson("shared_entries",
            "shared_folder_id=eq.$sharedFolderId&order=created_at.desc")
            .map { it.toSharedEntryList() }
    }

    suspend fun addEntry(entry: SharedEntry): Result<SharedEntry> {
        val json = JSONObject().apply {
            put("shared_folder_id", entry.sharedFolderId)
            put("local_entry_id", entry.localEntryId ?: JSONObject.NULL)
            put("amount", entry.amount)
            put("label", entry.label)
            put("expression", entry.expression)
            put("created_by", entry.createdBy)
        }.toString()
        val result = postJson("shared_entries", json)
        return result.map { it.toSharedEntryList().firstOrNull() ?: entry }
    }

    suspend fun softDeleteEntry(entryId: Long, deletedBy: String): Result<Boolean> {
        return patchRequest("shared_entries", "id=eq.$entryId",
            """{"deleted_at": "now()", "deleted_by": "$deletedBy"}""")
    }

    suspend fun updateEntry(entryId: Long, amount: Double, label: String, updatedBy: String): Result<Boolean> {
        return patchRequest("shared_entries", "id=eq.$entryId",
            """{"amount": $amount, "label": "$label", "updated_at": "now()", "updated_by": "$updatedBy"}""")
    }

    // ── API: Sync Events (Audit) ────────────────────────

    suspend fun addSyncEvent(event: SyncEvent): Result<SyncEvent> {
        val json = JSONObject().apply {
            put("shared_folder_id", event.sharedFolderId)
            put("event_type", event.eventType)
            put("entry_id", event.entryId ?: JSONObject.NULL)
            put("actor_name", event.actorName)
            put("amount", event.amount ?: JSONObject.NULL)
            put("label", event.label ?: JSONObject.NULL)
            put("expression", event.expression ?: JSONObject.NULL)
            put("old_amount", event.oldAmount ?: JSONObject.NULL)
            put("old_label", event.oldLabel ?: JSONObject.NULL)
            put("old_expression", event.oldExpression ?: JSONObject.NULL)
        }.toString()
        val result = postJson("sync_events", json)
        return result.map { it.toSyncEventList().firstOrNull() ?: event }
    }

    suspend fun getSyncEvents(sharedFolderId: Long): Result<List<SyncEvent>> {
        return getJson("sync_events",
            "shared_folder_id=eq.$sharedFolderId&order=created_at.desc&limit=100")
            .map { it.toSyncEventList() }
    }
}
