package com.example.data

import kotlinx.coroutines.flow.Flow

class CalculatorRepository(private val dao: CalculatorDao) {

    val globalHistory: Flow<List<GlobalHistory>> = dao.getAllGlobalHistory()
    val allGroups: Flow<List<LedgerGroup>> = dao.getAllGroups()

    suspend fun insertGlobalHistory(history: GlobalHistory) = dao.insertGlobalHistory(history)
    suspend fun updateGlobalHistory(history: GlobalHistory) = dao.updateGlobalHistory(history)
    suspend fun deleteGlobalHistoryById(id: Int) = dao.deleteGlobalHistoryById(id)
    suspend fun clearGlobalHistory() = dao.clearGlobalHistory()

    fun getGroupById(id: Int) = dao.getGroupById(id)
    suspend fun insertGroup(group: LedgerGroup) = dao.insertGroup(group)
    suspend fun updateGroup(group: LedgerGroup) = dao.updateGroup(group)
    suspend fun deleteGroupById(id: Int) = dao.deleteGroupById(id)

    fun getTransactionsForGroup(groupId: Int) = dao.getTransactionsByGroupId(groupId)
    fun getGroupBalance(groupId: Int) = dao.getGroupBalance(groupId)

    suspend fun insertTransaction(transaction: TransactionEntry) = dao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: TransactionEntry) = dao.updateTransaction(transaction)
    suspend fun deleteTransactionById(id: Int) = dao.deleteTransactionById(id)
}
