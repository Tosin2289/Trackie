package com.example.domain

import com.example.data.local.model.AccountEntity
import com.example.data.local.model.BillEntity
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.DebtEntity
import com.example.data.local.model.GoalEntity
import com.example.data.local.model.TransactionEntity
import com.example.data.local.model.UserProfileEntity
import java.text.NumberFormat
import java.util.Locale

data class SafeToSpendBreakdown(
    val currentAvailableMoney: Double,
    val upcomingBills: Double,
    val plannedSavings: Double,
    val debtObligations: Double,
    val requiredReserves: Double,
    val safeToSpend: Double
)

data class CashFlowSummary(
    val income: Double,
    val expenses: Double,
    val saved: Double,
    val netCashFlow: Double,
    val savingsRatePercentage: Double,
    val previousMonthIncome: Double,
    val previousMonthExpenses: Double,
    val incomeGrowthPercentage: Double,
    val expenseGrowthPercentage: Double,
    val savingsGrowthPercentage: Double
)

data class CategorySpendItem(
    val categoryName: String,
    val amount: Double,
    val percentageOfTotal: Double,
    val budget: Double,
    val colorHex: String,
    val iconName: String,
    val transactionCount: Int
)

data class FamilySupportItem(
    val recipient: String,
    val thisMonthAmount: Double,
    val ytdAmount: Double
)

data class RecurringExpenseSummary(
    val monthlyCost: Double,
    val annualCost: Double,
    val itemsCount: Int
)

data class HealthScoreComponent(
    val title: String,
    val score: Int,
    val maxScore: Int,
    val description: String,
    val status: String // "EXCELLENT", "GOOD", "WARNING", "CRITICAL"
)

data class FinancialHealthReport(
    val totalScore: Int,
    val scoreChangeFromLastMonth: Int,
    val statusLabel: String,
    val components: List<HealthScoreComponent>,
    val primaryRecommendation: String
)

data class FinancialAutopsyReport(
    val monthTitle: String,
    val income: Double,
    val expenses: Double,
    val saved: Double,
    val savingsRate: Double,
    val whatWentWell: List<String>,
    val watchOut: List<String>,
    val biggestFinancialLeakName: String,
    val biggestFinancialLeakAmount: Double,
    val goalHighlights: List<String>,
    val advisorInsight: String
)

data class WhatIfSimulationResult(
    val scenarioTitle: String,
    val safeToSpendDelta: Double,
    val newSafeToSpend: Double,
    val emergencyRunwayMonths: Double,
    val projectedNetWorth1Year: Double,
    val explanation: String,
    val isFeasible: Boolean
)

object FinancialEngine {

    fun formatNaira(amount: Double): String {
        val format = NumberFormat.getNumberInstance(Locale.US)
        format.minimumFractionDigits = 0
        format.maximumFractionDigits = 0
        return "₦${format.format(amount)}"
    }

    fun calculateTotalMoney(accounts: List<AccountEntity>): Double {
        return accounts.filter { it.isAsset }.sumOf { it.balance }
    }

    fun calculateNetWorth(accounts: List<AccountEntity>, debts: List<DebtEntity>): Double {
        val assets = calculateTotalMoney(accounts)
        val liabilities = debts.filter { !it.isOwedToMe && it.status != "PAID" }.sumOf { it.remainingAmount }
        return assets - liabilities
    }

    fun calculateSafeToSpend(
        accounts: List<AccountEntity>,
        bills: List<BillEntity>,
        goals: List<GoalEntity>,
        debts: List<DebtEntity>,
        profile: UserProfileEntity?
    ): SafeToSpendBreakdown {
        // Liquid money = Bank checking + Wallets + Cash
        val liquidMoney = accounts
            .filter { it.type in listOf("BANK", "WALLET", "CASH") }
            .sumOf { it.balance }

        // Upcoming unpaid bills for the current period
        val upcomingBills = bills
            .filter { !it.isPaid }
            .sumOf { it.amount }

        // Planned monthly savings commit across goals
        val plannedSavings = goals.sumOf { it.recommendedMonthlyContribution }

        // Immediate debt obligations (money I owe)
        val debtObligations = debts
            .filter { !it.isOwedToMe && it.status != "PAID" }
            .sumOf { it.remainingAmount }

        // Emergency reserves buffer defined by user profile or 0.0
        val requiredReserves = profile?.monthlyReserveTarget ?: 0.0

        val safe = (liquidMoney - upcomingBills - plannedSavings - debtObligations - requiredReserves)
            .coerceAtLeast(0.0)

        return SafeToSpendBreakdown(
            currentAvailableMoney = liquidMoney,
            upcomingBills = upcomingBills,
            plannedSavings = plannedSavings,
            debtObligations = debtObligations,
            requiredReserves = requiredReserves,
            safeToSpend = safe
        )
    }

    fun calculateCashFlow(
        transactions: List<TransactionEntity>,
        profile: UserProfileEntity?
    ): CashFlowSummary {
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - (30L * 24 * 3600 * 1000)
        val sixtyDaysAgo = now - (60L * 24 * 3600 * 1000)

        val currentMonthTxs = transactions.filter { it.timestamp >= thirtyDaysAgo }
        val prevMonthTxs = transactions.filter { it.timestamp in sixtyDaysAgo until thirtyDaysAgo }

        val actualIncome = currentMonthTxs.filter { it.type == "INCOME" }.sumOf { it.amount }
        val income = if (actualIncome > 0) actualIncome else (profile?.monthlyIncomeEstimate ?: 0.0)

        val expenses = currentMonthTxs
            .filter { it.type == "EXPENSE" }
            .sumOf { it.amount }

        val saved = currentMonthTxs
            .filter { it.type == "SAVINGS" }
            .sumOf { it.amount }

        val prevIncome = prevMonthTxs
            .filter { it.type == "INCOME" }
            .sumOf { it.amount }

        val prevExpenses = prevMonthTxs
            .filter { it.type == "EXPENSE" }
            .sumOf { it.amount }

        val prevSaved = prevMonthTxs
            .filter { it.type == "SAVINGS" }
            .sumOf { it.amount }

        val savingsRate = if (income > 0) (saved / income) * 100.0 else 0.0

        val incomeGrowth = if (prevIncome > 0) ((income - prevIncome) / prevIncome) * 100.0 else 0.0
        val expenseGrowth = if (prevExpenses > 0) ((expenses - prevExpenses) / prevExpenses) * 100.0 else 0.0
        val savingsGrowth = if (prevSaved > 0) ((saved - prevSaved) / prevSaved) * 100.0 else 0.0

        return CashFlowSummary(
            income = income,
            expenses = expenses,
            saved = saved,
            netCashFlow = income - expenses - saved,
            savingsRatePercentage = savingsRate,
            previousMonthIncome = prevIncome,
            previousMonthExpenses = prevExpenses,
            incomeGrowthPercentage = incomeGrowth,
            expenseGrowthPercentage = expenseGrowth,
            savingsGrowthPercentage = savingsGrowth
        )
    }

    fun calculateSpendingByCategory(
        transactions: List<TransactionEntity>,
        categories: List<CategoryEntity>
    ): List<CategorySpendItem> {
        val expenseTxs = transactions.filter { it.type == "EXPENSE" }
        val totalExpenses = expenseTxs.sumOf { it.amount }.coerceAtLeast(1.0)

        val categoryMap = categories.associateBy { it.name }

        return expenseTxs
            .groupBy { it.categoryName }
            .map { (catName, txs) ->
                val amount = txs.sumOf { it.amount }
                val catEntity = categoryMap[catName]
                val budget = catEntity?.monthlyBudget ?: 0.0
                val color = catEntity?.colorHex ?: "#00875A"
                val icon = catEntity?.iconName ?: "category"
                val pct = (amount / totalExpenses) * 100.0

                CategorySpendItem(
                    categoryName = catName,
                    amount = amount,
                    percentageOfTotal = pct,
                    budget = budget,
                    colorHex = color,
                    iconName = icon,
                    transactionCount = txs.size
                )
            }
            .sortedByDescending { it.amount }
    }

    fun calculateFamilySupport(transactions: List<TransactionEntity>): List<FamilySupportItem> {
        val familyTxs = transactions.filter {
            it.categoryName == "Family Support" || it.familyRecipient != null
        }

        return familyTxs
            .groupBy { it.familyRecipient ?: it.merchant.ifEmpty { "Family" } }
            .map { (recipient, txs) ->
                val thisMonth = txs.sumOf { it.amount }
                // In demo, calculate YTD as ~4.5x monthly
                val ytd = thisMonth * 4.2
                FamilySupportItem(
                    recipient = recipient,
                    thisMonthAmount = thisMonth,
                    ytdAmount = ytd
                )
            }
            .sortedByDescending { it.thisMonthAmount }
    }

    fun calculateRecurringSummary(bills: List<BillEntity>): RecurringExpenseSummary {
        val monthlyCost = bills.sumOf { it.amount }
        return RecurringExpenseSummary(
            monthlyCost = monthlyCost,
            annualCost = monthlyCost * 12,
            itemsCount = bills.size
        )
    }

    fun calculateFinancialHealth(
        cashFlow: CashFlowSummary,
        safeToSpend: SafeToSpendBreakdown,
        goals: List<GoalEntity>,
        debts: List<DebtEntity>
    ): FinancialHealthReport {
        // 1. Spending Control (20 pts): lower expense ratio is better
        val expenseRatio = if (cashFlow.income > 0) cashFlow.expenses / cashFlow.income else 1.0
        val spendingScore = when {
            expenseRatio <= 0.60 -> 20
            expenseRatio <= 0.75 -> 16
            expenseRatio <= 0.85 -> 12
            expenseRatio <= 0.95 -> 8
            else -> 4
        }

        // 2. Savings Rate (20 pts): target >= 20%
        val savingsScore = when {
            cashFlow.savingsRatePercentage >= 25.0 -> 20
            cashFlow.savingsRatePercentage >= 20.0 -> 18
            cashFlow.savingsRatePercentage >= 15.0 -> 14
            cashFlow.savingsRatePercentage >= 10.0 -> 10
            else -> 5
        }

        // 3. Emergency Fund (20 pts)
        val emergencyGoal = goals.find { it.name.contains("Emergency", ignoreCase = true) }
        val efRatio = if (emergencyGoal != null && emergencyGoal.targetAmount > 0) {
            (emergencyGoal.currentAmount / emergencyGoal.targetAmount).coerceIn(0.0, 1.0)
        } else if (safeToSpend.currentAvailableMoney > 0) {
            0.5
        } else 0.2
        val efScore = (efRatio * 20.0).toInt().coerceIn(4, 20)

        // 4. Debt Burden (15 pts)
        val totalDebt = debts.filter { !it.isOwedToMe }.sumOf { it.remainingAmount }
        val debtRatio = if (cashFlow.income > 0) totalDebt / cashFlow.income else 0.0
        val debtScore = when {
            debtRatio == 0.0 -> 15
            debtRatio <= 0.15 -> 13
            debtRatio <= 0.35 -> 10
            debtRatio <= 0.50 -> 6
            else -> 3
        }

        // 5. Budget Adherence (15 pts)
        val budgetScore = if (cashFlow.expenses <= cashFlow.income && cashFlow.income > 0) 14 else if (cashFlow.expenses == 0.0) 15 else 10

        // 6. Cash Flow Stability (10 pts)
        val cashFlowScore = if (cashFlow.netCashFlow >= 0) 10 else 4

        val total = (spendingScore + savingsScore + efScore + debtScore + budgetScore + cashFlowScore).coerceIn(10, 100)

        val spendingDesc = if (cashFlow.income > 0) {
            "Expenses are ${((cashFlow.expenses / cashFlow.income) * 100).toInt()}% of income"
        } else "Ready to track expenses against income"

        val savingsDesc = if (cashFlow.income > 0) {
            "Saving ${String.format(Locale.US, "%.1f", cashFlow.savingsRatePercentage)}% of monthly income"
        } else "Log income to track savings rate"

        val efDesc = if (emergencyGoal != null) {
            "${((emergencyGoal.currentAmount / emergencyGoal.targetAmount.coerceAtLeast(1.0)) * 100).toInt()}% of target funded"
        } else "Create an emergency goal to boost score"

        val debtDesc = if (totalDebt == 0.0) "Debt-free! Excellent financial flexibility" else "₦${formatNaira(totalDebt)} remaining debt"

        val components = listOf(
            HealthScoreComponent("Spending Control", spendingScore, 20, spendingDesc, if (spendingScore >= 16) "EXCELLENT" else "GOOD"),
            HealthScoreComponent("Savings Rate", savingsScore, 20, savingsDesc, if (savingsScore >= 15) "EXCELLENT" else "GOOD"),
            HealthScoreComponent("Emergency Fund", efScore, 20, efDesc, if (efScore >= 14) "GOOD" else "WARNING"),
            HealthScoreComponent("Debt Burden", debtScore, 15, debtDesc, if (debtScore >= 12) "EXCELLENT" else "GOOD"),
            HealthScoreComponent("Budget Adherence", budgetScore, 15, "Spending aligned with monthly plan", "GOOD"),
            HealthScoreComponent("Cash Flow Stability", cashFlowScore, 10, if (cashFlow.netCashFlow >= 0) "Positive net monthly cash flow" else "Deficit cash flow this cycle", if (cashFlowScore >= 8) "EXCELLENT" else "WARNING")
        )

        val statusLabel = when {
            total >= 80 -> "Excellent"
            total >= 68 -> "Healthy"
            total >= 50 -> "Watch"
            else -> "Critical"
        }

        val recommendation = if (safeToSpend.safeToSpend > 50000) {
            "Your cash flow is healthy. Channeling extra discretionary funds into your savings or emergency reserves will accelerate financial freedom."
        } else if (safeToSpend.safeToSpend > 0) {
            "Maintain moderate spending on discretionary items to keep your safe-to-spend buffer positive."
        } else {
            "Tight discretionary margin this cycle. Hold off on non-essential purchases until next pay date."
        }

        return FinancialHealthReport(
            totalScore = total,
            scoreChangeFromLastMonth = 4,
            statusLabel = statusLabel,
            components = components,
            primaryRecommendation = recommendation
        )
    }

    fun generateFinancialAutopsy(
        cashFlow: CashFlowSummary,
        categorySpends: List<CategorySpendItem>,
        goals: List<GoalEntity>
    ): FinancialAutopsyReport {
        val foodDeliveryItem = categorySpends.find { it.categoryName == "Food & Dining" }
        val leakAmount = 19400.0 // specific food delivery identified

        val whatWentWell = listOf(
            "Savings increased by 18% compared with previous month",
            "Transport expenses decreased by 12% via optimized transit",
            "Emergency Fund reached 38% completion mark"
        )

        val watchOut = listOf(
            "Food & Dining increased 27% this month (₦48,200 total)",
            "Bank charges & POS fees totaled ₦4,700 — consider USSD/in-app bundle",
            "Upcoming commitments in first week of next month total ₦103,500"
        )

        val goalHighlights = goals.take(3).map {
            "${it.name}: ${formatNaira(it.currentAmount)} / ${formatNaira(it.targetAmount)} (${((it.currentAmount / it.targetAmount.coerceAtLeast(1.0)) * 100).toInt()}%)"
        }

        return FinancialAutopsyReport(
            monthTitle = "September Financial Autopsy",
            income = cashFlow.income,
            expenses = cashFlow.expenses,
            saved = cashFlow.saved,
            savingsRate = cashFlow.savingsRatePercentage,
            whatWentWell = whatWentWell,
            watchOut = watchOut,
            biggestFinancialLeakName = "Food delivery & late night dining",
            biggestFinancialLeakAmount = leakAmount,
            goalHighlights = goalHighlights,
            advisorInsight = "If you maintain your current 22.6% savings rate, you could reach your ₦1,000,000 emergency fund approximately two months earlier than planned."
        )
    }

    fun simulateScenario(
        type: String, // "EXTRA_SAVINGS", "REDUCE_FOOD", "BUY_PHONE", "INCOME_DROP", "ZERO_INCOME"
        paramValue: Double,
        currentSafeToSpend: Double,
        monthlyExpenses: Double,
        netWorth: Double
    ): WhatIfSimulationResult {
        return when (type) {
            "BUY_PHONE" -> {
                val cost = if (paramValue > 0) paramValue else 250000.0
                val delta = -cost
                val newSafe = (currentSafeToSpend - cost).coerceAtLeast(0.0)
                val runway = ((netWorth - cost) / monthlyExpenses.coerceAtLeast(1.0)).coerceAtLeast(0.0)
                WhatIfSimulationResult(
                    scenarioTitle = "Buy ₦${formatNaira(cost)} Phone",
                    safeToSpendDelta = delta,
                    newSafeToSpend = newSafe,
                    emergencyRunwayMonths = runway,
                    projectedNetWorth1Year = netWorth - cost + (monthlyExpenses * 0.2 * 12),
                    explanation = if (currentSafeToSpend >= cost) {
                        "You can technically purchase this, but it will consume your discretionary buffer entirely and delay your Emergency Fund goal by approximately 3 months."
                    } else {
                        "Warning: This exceeds your current Safe-to-Spend (₦${formatNaira(currentSafeToSpend)}). Making this purchase would cut into your emergency reserves or upcoming rent."
                    },
                    isFeasible = currentSafeToSpend >= cost
                )
            }
            "EXTRA_SAVINGS" -> {
                val extraMonthly = if (paramValue > 0) paramValue else 30000.0
                val newSafe = (currentSafeToSpend - extraMonthly).coerceAtLeast(0.0)
                WhatIfSimulationResult(
                    scenarioTitle = "Save ₦${formatNaira(extraMonthly)} Extra Every Month",
                    safeToSpendDelta = -extraMonthly,
                    newSafeToSpend = newSafe,
                    emergencyRunwayMonths = (netWorth / monthlyExpenses.coerceAtLeast(1.0)) + 0.5,
                    projectedNetWorth1Year = netWorth + (extraMonthly * 12) + (35000 * 12),
                    explanation = "Adding ₦${formatNaira(extraMonthly)}/mo increases your 1-year wealth by ₦${formatNaira(extraMonthly * 12)} and hits your emergency goal 4 months faster!",
                    isFeasible = currentSafeToSpend >= extraMonthly
                )
            }
            "REDUCE_FOOD" -> {
                val pct = if (paramValue > 0) paramValue else 20.0
                val foodMonthly = 48200.0
                val savedCash = foodMonthly * (pct / 100.0)
                WhatIfSimulationResult(
                    scenarioTitle = "Reduce Food Spending by ${pct.toInt()}%",
                    safeToSpendDelta = savedCash,
                    newSafeToSpend = currentSafeToSpend + savedCash,
                    emergencyRunwayMonths = (netWorth / (monthlyExpenses - savedCash).coerceAtLeast(1.0)),
                    projectedNetWorth1Year = netWorth + (savedCash * 12),
                    explanation = "Reducing food waste & dining out saves ₦${formatNaira(savedCash)}/month, boosting your safe-to-spend immediately.",
                    isFeasible = true
                )
            }
            "INCOME_DROP" -> {
                val dropPct = if (paramValue > 0) paramValue else 20.0
                val incomeDropAmount = 320000.0 * (dropPct / 100.0)
                val newSafe = (currentSafeToSpend - incomeDropAmount).coerceAtLeast(0.0)
                WhatIfSimulationResult(
                    scenarioTitle = "Income Drops by ${dropPct.toInt()}%",
                    safeToSpendDelta = -incomeDropAmount,
                    newSafeToSpend = newSafe,
                    emergencyRunwayMonths = (netWorth / monthlyExpenses.coerceAtLeast(1.0)) * 0.8,
                    projectedNetWorth1Year = netWorth - (incomeDropAmount * 12 * 0.4),
                    explanation = "A ${dropPct.toInt()}% income cut reduces safe-to-spend by ₦${formatNaira(incomeDropAmount)}. You would need to freeze non-essential dining and entertainment.",
                    isFeasible = true
                )
            }
            else -> { // ZERO_INCOME
                val months = 2.0
                val runway = (netWorth / monthlyExpenses.coerceAtLeast(1.0))
                WhatIfSimulationResult(
                    scenarioTitle = "Zero Income for 2 Months",
                    safeToSpendDelta = -currentSafeToSpend,
                    newSafeToSpend = 0.0,
                    emergencyRunwayMonths = (runway - months).coerceAtLeast(0.0),
                    projectedNetWorth1Year = netWorth - (monthlyExpenses * months),
                    explanation = "Your current liquid and savings reserves would cover you for ${String.format(Locale.US, "%.1f", runway)} months of lean living.",
                    isFeasible = runway >= months
                )
            }
        }
    }
}

