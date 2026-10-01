package com.example.data.repository

import com.example.data.local.dao.TrackieDao
import com.example.data.local.model.AccountEntity
import com.example.data.local.model.BillEntity
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.DebtEntity
import com.example.data.local.model.GoalEntity
import com.example.data.local.model.TransactionEntity
import com.example.data.local.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class TrackieRepository(private val dao: TrackieDao) {

    val accounts: Flow<List<AccountEntity>> = dao.getAllAccounts()
    val transactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val categories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    val goals: Flow<List<GoalEntity>> = dao.getAllGoals()
    val bills: Flow<List<BillEntity>> = dao.getAllBills()
    val debts: Flow<List<DebtEntity>> = dao.getAllDebts()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()

    // Accounts
    suspend fun addAccount(account: AccountEntity) = dao.insertAccount(account)
    suspend fun updateAccount(account: AccountEntity) = dao.updateAccount(account)
    suspend fun deleteAccount(id: Long) = dao.deleteAccountById(id)

    // Transactions
    suspend fun addTransaction(tx: TransactionEntity) {
        dao.insertTransaction(tx)
        // Automatically adjust account balance
        when (tx.type) {
            "EXPENSE" -> {
                dao.adjustAccountBalance(tx.accountId, -tx.amount)
            }
            "INCOME" -> {
                dao.adjustAccountBalance(tx.accountId, tx.amount)
            }
            "TRANSFER", "SAVINGS" -> {
                dao.adjustAccountBalance(tx.accountId, -tx.amount)
                if (tx.toAccountId != null) {
                    dao.adjustAccountBalance(tx.toAccountId, tx.amount)
                }
            }
            "DEBT_GIVEN" -> {
                dao.adjustAccountBalance(tx.accountId, -tx.amount)
            }
            "DEBT_RECEIVED" -> {
                dao.adjustAccountBalance(tx.accountId, tx.amount)
            }
        }
    }

    suspend fun updateTransaction(tx: TransactionEntity) = dao.updateTransaction(tx)
    suspend fun deleteTransaction(tx: TransactionEntity) {
        dao.deleteTransactionById(tx.id)
        // Revert balance adjustment
        when (tx.type) {
            "EXPENSE" -> dao.adjustAccountBalance(tx.accountId, tx.amount)
            "INCOME" -> dao.adjustAccountBalance(tx.accountId, -tx.amount)
            "TRANSFER", "SAVINGS" -> {
                dao.adjustAccountBalance(tx.accountId, tx.amount)
                if (tx.toAccountId != null) {
                    dao.adjustAccountBalance(tx.toAccountId, -tx.amount)
                }
            }
            else -> {}
        }
    }

    // Categories
    suspend fun addCategory(category: CategoryEntity) = dao.insertCategory(category)
    suspend fun updateCategory(category: CategoryEntity) = dao.updateCategory(category)
    suspend fun deleteCategory(id: Long) = dao.deleteCategoryById(id)

    // Goals
    suspend fun addGoal(goal: GoalEntity) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = dao.updateGoal(goal)
    suspend fun contributeToGoal(id: Long, amount: Double) = dao.contributeToGoal(id, amount)
    suspend fun deleteGoal(id: Long) = dao.deleteGoalById(id)

    // Bills
    suspend fun addBill(bill: BillEntity) = dao.insertBill(bill)
    suspend fun updateBill(bill: BillEntity) = dao.updateBill(bill)
    suspend fun toggleBillPaid(id: Long, isPaid: Boolean) = dao.setBillPaidStatus(id, isPaid)
    suspend fun deleteBill(id: Long) = dao.deleteBillById(id)

    // Debts
    suspend fun addDebt(debt: DebtEntity) = dao.insertDebt(debt)
    suspend fun updateDebt(debt: DebtEntity) = dao.updateDebt(debt)
    suspend fun deleteDebt(id: Long) = dao.deleteDebtById(id)

    // Profile
    suspend fun updateProfile(profile: UserProfileEntity) = dao.insertOrUpdateProfile(profile)

    // Clear user data (no dummy data)
    suspend fun clearAllData() {
        dao.clearTransactions()
        dao.clearAccounts()
        dao.clearGoals()
        dao.clearBills()
        dao.clearDebts()
    }
}
