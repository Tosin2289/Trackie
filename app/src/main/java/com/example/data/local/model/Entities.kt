package com.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "BANK", "WALLET", "CASH", "SAVINGS", "INVESTMENT", "CREDIT"
    val balance: Double,
    val institution: String,
    val accountNumber: String = "",
    val colorHex: String = "#00875A",
    val isAsset: Boolean = true
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: String, // "EXPENSE", "INCOME", "TRANSFER", "SAVINGS", "DEBT_GIVEN", "DEBT_RECEIVED", "REFUND"
    val categoryId: Long = 0,
    val categoryName: String,
    val subCategory: String = "",
    val accountId: Long,
    val accountName: String,
    val toAccountId: Long? = null,
    val toAccountName: String? = null,
    val merchant: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val familyRecipient: String? = null, // e.g. "Mum", "Sibling", "Dad"
    val paymentMethod: String = "Bank Transfer", // "Bank Transfer", "POS", "Cash", "Debit Card", "USSD", "Digital Wallet"
    val isRecurring: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String = "EXPENSE", // "EXPENSE", "INCOME"
    val iconName: String = "category",
    val colorHex: String = "#00875A",
    val monthlyBudget: Double = 0.0,
    val isCustom: Boolean = false
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDateMillis: Long,
    val recommendedMonthlyContribution: Double,
    val category: String = "Savings",
    val iconName: String = "flag",
    val colorHex: String = "#00875A"
)

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val dueDayOfMonth: Int,
    val nextDueDateMillis: Long,
    val category: String = "Bills",
    val isPaid: Boolean = false,
    val recurrence: String = "MONTHLY", // "MONTHLY", "WEEKLY", "ANNUALLY", "ONE_TIME"
    val reminderDaysBefore: Int = 3
)

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personOrCreditor: String,
    val amount: Double,
    val remainingAmount: Double,
    val isOwedToMe: Boolean, // true = Money owed to me, false = Money I owe
    val dueDateMillis: Long,
    val status: String = "PENDING", // "PENDING", "PARTIALLY_PAID", "PAID"
    val notes: String = ""
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Ayoola",
    val email: String = "ayoolatosin00@gmail.com",
    val supabaseUserId: String = "utqvepqvblwhjnepxdvk-user",
    val currencySymbol: String = "₦",
    val currencyCode: String = "NGN",
    val monthlyIncomeEstimate: Double = 450000.0,
    val monthlyReserveTarget: Double = 66500.0,
    val priorities: String = "Emergency fund,Debt repayment,Savings,Investment",
    val isBiometricEnabled: Boolean = false
)
