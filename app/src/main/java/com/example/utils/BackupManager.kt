package com.example.utils

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.example.data.AppDatabase
import com.example.data.GlobalHistory
import com.example.data.LedgerGroup
import com.example.data.TransactionEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object BackupManager {
    suspend fun createBackup(context: Context, database: AppDatabase, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val dao = database.calculatorDao()
            val groups = dao.getAllGroups().first()
            val globalHistory = dao.getAllGlobalHistory().first()
            
            val json = JSONObject()
            
            val groupsArray = JSONArray()
            groups.forEach { group ->
                val groupObj = JSONObject()
                groupObj.put("id", group.id)
                groupObj.put("name", group.name)
                groupObj.put("color", group.color)
                groupObj.put("timestamp", group.timestamp)
                
                val txs = dao.getTransactionsByGroupId(group.id).first()
                val txsArray = JSONArray()
                txs.forEach { tx ->
                    val txObj = JSONObject()
                    txObj.put("amount", tx.amount)
                    txObj.put("label", tx.label)
                    txObj.put("timestamp", tx.timestamp)
                    txsArray.put(txObj)
                }
                groupObj.put("transactions", txsArray)
                groupsArray.put(groupObj)
            }
            json.put("groups", groupsArray)
            
            val globalArray = JSONArray()
            globalHistory.forEach { history ->
                val historyObj = JSONObject()
                historyObj.put("expression", history.expression)
                historyObj.put("result", history.result)
                historyObj.put("note", history.note)
                historyObj.put("timestamp", history.timestamp)
                globalArray.put(historyObj)
            }
            json.put("globalHistory", globalArray)
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toString(2).toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun restoreBackup(context: Context, database: AppDatabase, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val stringBuilder = java.lang.StringBuilder()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }
            }
            
            val json = JSONObject(stringBuilder.toString())
            val dao = database.calculatorDao()

            // Wrap in transaction so a failure rolls back everything
            database.withTransaction {
                // We clear existing data for a full restore.
                database.clearAllTables()

                val groupsArray = json.optJSONArray("groups")
                if (groupsArray != null) {
                    for (i in 0 until groupsArray.length()) {
                        val groupObj = groupsArray.getJSONObject(i)
                        val name = groupObj.getString("name")
                        val color = groupObj.optLong("color", 0xFF1976D2)
                        val timestamp = groupObj.optLong("timestamp", System.currentTimeMillis())

                        val newGroup = LedgerGroup(name = name, color = color, timestamp = timestamp)
                        val newGroupId = dao.insertGroup(newGroup).toInt()

                        val txsArray = groupObj.optJSONArray("transactions")
                        if (txsArray != null) {
                            for (j in 0 until txsArray.length()) {
                                val txObj = txsArray.getJSONObject(j)
                                dao.insertTransaction(TransactionEntry(
                                    groupId = newGroupId,
                                    amount = txObj.getDouble("amount"),
                                    label = txObj.getString("label"),
                                    timestamp = txObj.optLong("timestamp", System.currentTimeMillis())
                                ))
                            }
                        }
                    }
                }

                val globalArray = json.optJSONArray("globalHistory")
                if (globalArray != null) {
                    for (i in 0 until globalArray.length()) {
                        val historyObj = globalArray.getJSONObject(i)
                        dao.insertGlobalHistory(GlobalHistory(
                            expression = historyObj.getString("expression"),
                            result = historyObj.getDouble("result"),
                            note = historyObj.getString("note"),
                            timestamp = historyObj.optLong("timestamp", System.currentTimeMillis())
                        ))
                    }
                }
            }
            
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
