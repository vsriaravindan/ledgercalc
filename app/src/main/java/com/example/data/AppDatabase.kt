package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "global_history")
data class GlobalHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expression: String,
    val result: Double,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ledger_group")
data class LedgerGroup(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: Long = 0xFF1976D2, // Default to a blue shade
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "transaction_entry",
    foreignKeys = [
        ForeignKey(
            entity = LedgerGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupId: Int,
    val amount: Double,
    val label: String = "",
    val expression: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface CalculatorDao {
    @Query("SELECT * FROM global_history ORDER BY timestamp DESC")
    fun getAllGlobalHistory(): Flow<List<GlobalHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalHistory(history: GlobalHistory)

    @Update
    suspend fun updateGlobalHistory(history: GlobalHistory)

    @Query("DELETE FROM global_history WHERE id = :id")
    suspend fun deleteGlobalHistoryById(id: Int)

    @Query("DELETE FROM global_history")
    suspend fun clearGlobalHistory()

    @Query("SELECT * FROM ledger_group ORDER BY timestamp DESC")
    fun getAllGroups(): Flow<List<LedgerGroup>>

    @Query("SELECT * FROM ledger_group WHERE id = :id")
    fun getGroupById(id: Int): Flow<LedgerGroup?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: LedgerGroup): Long

    @Update
    suspend fun updateGroup(group: LedgerGroup)

    @Query("DELETE FROM ledger_group WHERE id = :id")
    suspend fun deleteGroupById(id: Int)

    @Query("SELECT * FROM transaction_entry WHERE groupId = :groupId ORDER BY timestamp DESC")
    fun getTransactionsByGroupId(groupId: Int): Flow<List<TransactionEntry>>

    @Query("SELECT * FROM transaction_entry WHERE groupId = :groupId")
    suspend fun getTransactionsOnce(groupId: Int): List<TransactionEntry>

    @Query("SELECT SUM(amount) FROM transaction_entry WHERE groupId = :groupId")
    fun getGroupBalance(groupId: Int): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntry): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntry)

    @Query("DELETE FROM transaction_entry WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Schema v1→v2: added color field to ledger_group, label to transaction_entry, note to global_history
        db.execSQL("ALTER TABLE ledger_group ADD COLUMN color INTEGER NOT NULL DEFAULT 4278236370")
        db.execSQL("ALTER TABLE transaction_entry ADD COLUMN label TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE global_history ADD COLUMN note TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE transaction_entry ADD COLUMN expression TEXT NOT NULL DEFAULT ''")
    }
}

@Database(
    entities = [GlobalHistory::class, LedgerGroup::class, TransactionEntry::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calculatorDao(): CalculatorDao
}
