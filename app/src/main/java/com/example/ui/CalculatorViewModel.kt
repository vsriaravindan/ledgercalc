package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.CalculatorRepository
import com.example.data.MIGRATION_2_3
import com.example.data.GlobalHistory
import com.example.data.LedgerGroup
import com.example.data.MIGRATION_1_2
import com.example.data.TransactionEntry
import com.example.utils.CalculatorUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "calculator-db"
    )
    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
    .build()

    private val repository = CalculatorRepository(db.calculatorDao())

    val allGroups = repository.allGroups.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val globalHistory = repository.globalHistory.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Calculator State
    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _calcError = MutableStateFlow(false)
    val calcError: StateFlow<Boolean> = _calcError.asStateFlow()

    private val prefs = application.getSharedPreferences("calc_prefs", android.content.Context.MODE_PRIVATE)
    private val _currencySymbol = MutableStateFlow(prefs.getString("currency", "$") ?: "$")
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    private val _themeMode = MutableStateFlow(prefs.getInt("theme_mode", 0)) // 0=System, 1=Light, 2=Dark
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    private val _includeWatermark = MutableStateFlow(prefs.getBoolean("include_watermark", true))
    val includeWatermark: StateFlow<Boolean> = _includeWatermark.asStateFlow()

    private val _fontFamily = MutableStateFlow(prefs.getString("font_family", "Default") ?: "Default")
    val fontFamily: StateFlow<String> = _fontFamily.asStateFlow()

    fun setCurrency(symbol: String) {
        _currencySymbol.value = symbol
        prefs.edit().putString("currency", symbol).apply()
    }

    fun setThemeMode(mode: Int) {
        _themeMode.value = mode
        prefs.edit().putInt("theme_mode", mode).apply()
    }

    fun setIncludeWatermark(include: Boolean) {
        _includeWatermark.value = include
        prefs.edit().putBoolean("include_watermark", include).apply()
    }

    fun setFontFamily(font: String) {
        _fontFamily.value = font
        prefs.edit().putString("font_family", font).apply()
    }

    val selectedFontFamily: androidx.compose.ui.text.font.FontFamily
        get() {
            val fontName = _fontFamily.value
            return com.example.ui.theme.AppFont.entries.firstOrNull { it.displayName == fontName }?.fontFamily
                ?: androidx.compose.ui.text.font.FontFamily.Default
        }

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _undoStack = MutableStateFlow<List<String>>(emptyList())
    private val _redoStack = MutableStateFlow<List<String>>(emptyList())

    // Group specific state
    private val _activeGroupId = MutableStateFlow<Int?>(null)
    val activeGroupId: StateFlow<Int?> = _activeGroupId.asStateFlow()

    val activeGroupTransactions = MutableStateFlow<List<TransactionEntry>>(emptyList())
    val activeGroupBalance = MutableStateFlow<Double>(0.0)
    val activeGroup = MutableStateFlow<LedgerGroup?>(null)
    
    private var groupJob: kotlinx.coroutines.Job? = null

    fun setActiveGroup(groupId: Int?) {
        _activeGroupId.value = groupId
        groupJob?.cancel()
        if (groupId != null) {
            groupJob = viewModelScope.launch {
                launch {
                    repository.getTransactionsForGroup(groupId).collect { txs ->
                        activeGroupTransactions.value = txs
                    }
                }
                launch {
                    repository.getGroupBalance(groupId).collect { bal ->
                        activeGroupBalance.value = bal ?: 0.0
                    }
                }
                launch {
                    repository.getGroupById(groupId).collect { grp ->
                        activeGroup.value = grp
                    }
                }
            }
        } else {
            activeGroupTransactions.value = emptyList()
            activeGroupBalance.value = 0.0
            activeGroup.value = null
        }
        clearCalculator()
    }

    // Input handlers
    fun onInput(value: String) {
        val current = _expression.value
        saveStateForUndo()
        _expression.value = current + value
        updateResult()
        scheduleAutoSave()
    }

    fun onClear() {
        if (_expression.value.isNotEmpty()) {
            saveStateForUndo()
            clearCalculator()
        }
    }

    fun onDelete() {
        val current = _expression.value
        if (current.isNotEmpty()) {
            saveStateForUndo()
            _expression.value = current.dropLast(1)
            updateResult()
            scheduleAutoSave()
        }
    }

    fun onEvaluate() {
        if (_expression.value.isNotEmpty() && _result.value.isNotEmpty()) {
            saveStateForUndo()
            _expression.value = _result.value
            _result.value = ""
        }
    }

    fun saveDraftToHistory() {
        val expr = _expression.value
        val resStr = _result.value
        if (expr.isNotEmpty() && resStr.isNotEmpty()) {
            val resVal = resStr.toDoubleOrNull() ?: 0.0
            viewModelScope.launch {
                repository.insertGlobalHistory(
                    GlobalHistory(
                        expression = expr,
                        result = resVal,
                        note = "Not completed"
                    )
                )
            }
            _expression.value = ""
            _result.value = ""
        }
    }

    fun onEquals(note: String = "") {
        val expr = _expression.value
        val resStr = _result.value
        if (expr.isNotEmpty() && resStr.isNotEmpty()) {
            val resVal = resStr.toDoubleOrNull() ?: 0.0
            val groupId = _activeGroupId.value

            viewModelScope.launch {
                if (groupId != null) {
                    repository.insertTransaction(
                        TransactionEntry(
                            groupId = groupId,
                            amount = resVal,
                            label = note.ifEmpty { expr },
                            expression = expr
                        )
                    )
                } else {
                    repository.insertGlobalHistory(
                        GlobalHistory(
                            expression = expr,
                            result = resVal,
                            note = note
                        )
                    )
                }
            }
            
            // clear after save
            saveStateForUndo()
            _expression.value = ""
            _result.value = ""
        }
    }

    // Direct entry (without calculator)
    fun addDirectEntry(groupId: Int, amount: Double, label: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntry(
                    groupId = groupId,
                    amount = amount,
                    label = label,
                    expression = ""
                )
            )
        }
    }

    // Undo/Redo logic
    private val MAX_UNDO = 50
    private var autoSaveJob: Job? = null

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(2000) // 2 seconds after last input
            val expr = _expression.value
            val res = _result.value
            if (expr.isNotEmpty() && res.isNotEmpty()) {
                val resVal = res.toDoubleOrNull() ?: return@launch
                repository.insertGlobalHistory(
                    GlobalHistory(
                        expression = expr,
                        result = resVal,
                        note = "Not saved"
                    )
                )
            }
        }
    }

    private fun saveStateForUndo() {
        val current = _expression.value
        val stack = _undoStack.value
        _undoStack.value = (stack + current).takeLast(MAX_UNDO)
        _redoStack.value = emptyList()
    }

    fun undo() {
        val stack = _undoStack.value
        if (stack.isNotEmpty()) {
            val prev = stack.last()
            _redoStack.update { it + _expression.value }
            _undoStack.value = stack.dropLast(1)
            _expression.value = prev
            updateResult()
        }
    }

    fun redo() {
        val stack = _redoStack.value
        if (stack.isNotEmpty()) {
            val next = stack.last()
            _undoStack.update { it + _expression.value }
            _redoStack.value = stack.dropLast(1)
            _expression.value = next
            updateResult()
        }
    }

    private fun updateResult() {
        val expr = _expression.value
        if (expr.isEmpty()) {
            _result.value = ""
            _calcError.value = false
            return
        }

        // If expression ends with operator, show the last number as preview
        val trailingOps = setOf('+', '-', '*', '/', '^', '×', '÷')
        if (expr.last() in trailingOps) {
            // Extract the last number (token) after the last operator
            val parts = expr.split(Regex("[+\\-*/^×÷]"))
            val lastPart = parts.lastOrNull()?.trim() ?: ""
            _result.value = if (lastPart.isNotEmpty() && lastPart.toDoubleOrNull() != null) {
                val num = lastPart.toDouble()
                if (num % 1.0 == 0.0) num.toLong().toString() else num.toString()
            } else {
                ""
            }
            _calcError.value = false
            return
        }

        // If expression ends with a decimal point, show it as-is
        if (expr.last() == '.') {
            _result.value = expr
            _calcError.value = false
            return
        }

        try {
            val res = CalculatorUtils.evaluateExpression(expr)
            _result.value = if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
            _calcError.value = false
        } catch (e: Exception) {
            _result.value = ""
            _calcError.value = true
        }
    }

    private fun clearCalculator() {
        _expression.value = ""
        _result.value = ""
        _calcError.value = false
    }

    // Group Management
    fun createGroup(name: String, color: Long) {
        viewModelScope.launch {
            repository.insertGroup(LedgerGroup(name = name, color = color))
        }
    }

    fun updateGroup(group: LedgerGroup) {
        viewModelScope.launch {
            repository.updateGroup(group)
        }
    }

    fun deleteGroup(id: Int) {
        viewModelScope.launch {
            if (_activeGroupId.value == id) {
                setActiveGroup(null)
            }
            repository.deleteGroupById(id)
        }
    }

    // History Management
    fun addHistoryToGroup(history: GlobalHistory, groupId: Int, isIncome: Boolean) {
        val amount = if (isIncome) Math.abs(history.result) else -Math.abs(history.result)
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntry(
                    groupId = groupId,
                    amount = amount,
                    label = history.note.ifEmpty { history.expression },
                    expression = history.expression
                )
            )
        }
    }

    fun deleteGlobalHistory(id: Int) {
        viewModelScope.launch {
            repository.deleteGlobalHistoryById(id)
        }
    }
    
    fun clearAllGlobalHistory() {
        viewModelScope.launch {
            repository.clearGlobalHistory()
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }
    
    fun updateTransaction(tx: TransactionEntry) {
        viewModelScope.launch {
            repository.updateTransaction(tx)
        }
    }

    // Backup & Restore
    fun backupDatabase(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = com.example.utils.BackupManager.createBackup(context, db, uri)
            onResult(result)
        }
    }

    fun restoreDatabase(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = com.example.utils.BackupManager.restoreBackup(context, db, uri)
            onResult(result)
        }
    }
}
