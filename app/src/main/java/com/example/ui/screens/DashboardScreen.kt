package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.model.TransactionEntity
import com.example.domain.FinancialEngine
import com.example.ui.components.CashFlowBarChart
import com.example.ui.components.CommitmentTimelineItem
import com.example.ui.components.FinoraAiInsightCard
import com.example.ui.components.FinoraHealthScoreCard
import com.example.ui.components.FinoraHeroCard
import com.example.ui.components.MonthlySnapshotRow
import com.example.ui.components.SafeToSpendBreakdownSheet
import com.example.ui.components.SpendingDonutChart
import com.example.ui.navigation.Screen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraCardBorder
import com.example.ui.theme.FinoraCyan
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraPurple
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.theme.FinoraTextTertiary
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.TrackieViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: TrackieViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val safeToSpend by viewModel.safeToSpendBreakdown.collectAsStateWithLifecycle()
    val totalMoney by viewModel.totalMoney.collectAsStateWithLifecycle()
    val netWorth by viewModel.netWorth.collectAsStateWithLifecycle()
    val heroMode by viewModel.heroDisplayMode.collectAsStateWithLifecycle()
    val cashFlow by viewModel.cashFlowSummary.collectAsStateWithLifecycle()
    val healthReport by viewModel.financialHealth.collectAsStateWithLifecycle()
    val bills by viewModel.bills.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val categorySpends by viewModel.categorySpends.collectAsStateWithLifecycle()
    val showSafeSpendSheet by viewModel.showSafeSpendSheet.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header (Avatar, "Good morning, Tosin", "Mon, 30 Sep 2026", Notification Bell)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(FinoraLime.copy(alpha = 0.15f))
                            .border(1.5.dp, FinoraLime, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = FinoraLime,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Good morning,",
                            style = MaterialTheme.typography.bodySmall,
                            color = FinoraTextSecondary
                        )
                        Text(
                            text = userProfile?.userName?.ifBlank { "Trackie User" } ?: "Trackie User",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FinoraTextPrimary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Mon, 30 Sep 2026",
                        style = MaterialTheme.typography.labelSmall,
                        color = FinoraTextSecondary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Settings) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FinoraCardBg)
                            .testTag("btn_dashboard_notifications")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = FinoraTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2. Main Hero Card (TOTAL MONEY ₦482,500 + SAFE TO SPEND ₦73,500 + Pill Selector)
        item {
            FinoraHeroCard(
                displayMode = heroMode,
                safeToSpend = safeToSpend,
                totalMoney = totalMoney,
                netWorth = netWorth,
                accountCount = accounts.size,
                onModeChange = { viewModel.heroDisplayMode.value = it },
                onInspectSafeSpend = { viewModel.showSafeSpendSheet.value = true }
            )
        }

        // 3. Quick Fintech Hub Scroll (Budgets, Goals, Can I Afford?, Autopsy, Net Worth, Debts, Family Support, What-If)
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { QuickHubPill("Budgets", Icons.Default.PieChart) { viewModel.navigateTo(Screen.Budgets) } }
                item { QuickHubPill("Goals", Icons.Default.Flag) { viewModel.navigateTo(Screen.Goals) } }
                item { QuickHubPill("Can I Afford?", Icons.Default.QuestionMark) { viewModel.navigateTo(Screen.CanIAfford) } }
                item { QuickHubPill("Autopsy", Icons.Default.Assessment) { viewModel.navigateTo(Screen.FinancialAutopsy) } }
                item { QuickHubPill("Net Worth", Icons.Default.AccountBalance) { viewModel.navigateTo(Screen.NetWorth) } }
                item { QuickHubPill("Debts", Icons.Default.Handshake) { viewModel.navigateTo(Screen.DebtManagement) } }
                item { QuickHubPill("Family Support", Icons.Default.FamilyRestroom) { viewModel.navigateTo(Screen.FamilySupport) } }
                item { QuickHubPill("Timeline", Icons.Default.CalendarMonth) { viewModel.navigateTo(Screen.FinancialTimeline) } }
                item { QuickHubPill("What-If?", Icons.Default.Timeline) { viewModel.navigateTo(Screen.WhatIfSimulator) } }
            }
        }

        // 4. Monthly Snapshot 3-Column Row (Income ₦320k +12%, Spent ₦247.6k +7%, Saved ₦72.4k +21%)
        item {
            MonthlySnapshotRow(
                income = cashFlow.income,
                expenses = cashFlow.expenses,
                saved = cashFlow.saved
            )
        }

        // 5. Financial Health Card (72 / 100, Good, ↑4 pts from last month)
        item {
            FinoraHealthScoreCard(
                report = healthReport,
                onInspect = { viewModel.navigateTo(Screen.FinancialHealth) }
            )
        }

        // 6. Cash Flow Distribution Bar Chart
        item {
            CashFlowBarChart(
                income = cashFlow.income,
                expenses = cashFlow.expenses,
                saved = cashFlow.saved
            )
        }

        // 7. Spending Breakdown Donut Chart
        item {
            SpendingDonutChart(
                items = categorySpends,
                onCategoryClick = { viewModel.navigateTo(Screen.Budgets) }
            )
        }

        // 8. Highlighted AI Financial Insight Card
        item {
            FinoraAiInsightCard(
                onSeeWhy = {
                    viewModel.askAdvisor("Why did my food spending increase 27% this month?")
                    viewModel.navigateTo(Screen.Advisor)
                }
            )
        }

        // 9. Upcoming Commitments Timeline (Tomorrow Rent ₦50k, Oct 3 Internet ₦15k, Oct 5 Savings ₦30k, Oct 7 Subs ₦8.5k)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Commitments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
                TextButton(onClick = { viewModel.navigateTo(Screen.UpcomingBills) }) {
                    Text(
                        text = "See all →",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FinoraLime
                    )
                }
            }
        }

        if (bills.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg.copy(alpha = 0.6f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FinoraCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = FinoraCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "No upcoming bills scheduled yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FinoraTextSecondary
                        )
                    }
                }
            }
        } else {
            items(bills.take(4)) { bill ->
                CommitmentTimelineItem(
                    bill = bill,
                    onTogglePaid = { viewModel.toggleBillPaid(bill) }
                )
            }
        }

        // 10. Recent Transactions with "View all"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
                TextButton(onClick = { viewModel.navigateTo(Screen.Transactions) }) {
                    Text(
                        text = "View all →",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FinoraLime
                    )
                }
            }
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showAddTransactionSheet.value = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg.copy(alpha = 0.6f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FinoraCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FinoraLime.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = FinoraLime, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "No transactions logged yet",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = FinoraTextPrimary
                            )
                            Text(
                                text = "Tap here or the + button to record your first transaction",
                                style = MaterialTheme.typography.bodySmall,
                                color = FinoraTextSecondary
                            )
                        }
                    }
                }
            }
        } else {
            items(transactions.take(5)) { tx ->
                FinoraDashboardTxRow(
                    tx = tx,
                    onClick = { viewModel.navigateTo(Screen.Transactions) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }

    if (showSafeSpendSheet) {
        SafeToSpendBreakdownSheet(
            breakdown = safeToSpend,
            onDismiss = { viewModel.showSafeSpendSheet.value = false }
        )
    }
}

@Composable
private fun QuickHubPill(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = FinoraLime, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = FinoraTextPrimary
            )
        }
    }
}

@Composable
private fun FinoraDashboardTxRow(
    tx: TransactionEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("recent_tx_${tx.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.merchant.ifEmpty { tx.categoryName },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FinoraTextPrimary
                )
                Text(
                    text = "${tx.categoryName} • ${tx.accountName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FinoraTextSecondary
                )
            }

            val isIncome = tx.type == "INCOME"
            val sign = if (isIncome) "+" else "-"
            val color = if (isIncome) FinoraCyan else FinoraTextPrimary

            Text(
                text = "$sign${FinancialEngine.formatNaira(tx.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
