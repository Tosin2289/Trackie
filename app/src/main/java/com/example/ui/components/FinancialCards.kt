package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.BillEntity
import com.example.data.local.model.GoalEntity
import com.example.domain.FinancialEngine
import com.example.domain.FinancialHealthReport
import com.example.domain.SafeToSpendBreakdown
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraCardBgElevated
import com.example.ui.theme.FinoraCardBorder
import com.example.ui.theme.FinoraCyan
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraOrange
import com.example.ui.theme.FinoraPurple
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.theme.FinoraTextTertiary
import com.example.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinoraHeroCard(
    displayMode: String, // "SAFE_TO_SPEND", "TOTAL_MONEY", "NET_WORTH"
    safeToSpend: SafeToSpendBreakdown,
    totalMoney: Double,
    netWorth: Double,
    accountCount: Int,
    onModeChange: (String) -> Unit,
    onInspectSafeSpend: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBalanceVisible by remember { mutableStateOf(true) }

    val glowGradient = Brush.radialGradient(
        colors = listOf(
            FinoraLime.copy(alpha = 0.12f),
            FinoraCyan.copy(alpha = 0.06f),
            Color.Transparent
        ),
        radius = 500f,
        center = Offset(700f, 150f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(24.dp))
            .testTag("hero_financial_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(glowGradient)
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Row: Total Money & Visibility Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL MONEY",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.4.sp,
                            fontWeight = FontWeight.Bold,
                            color = FinoraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBalanceVisible) FinancialEngine.formatNaira(totalMoney) else "₦ ••••••••",
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FinoraTextPrimary
                        )
                        Text(
                            text = "Across $accountCount accounts",
                            style = MaterialTheme.typography.bodySmall,
                            color = FinoraTextTertiary
                        )
                    }

                    IconButton(
                        onClick = { isBalanceVisible = !isBalanceVisible },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FinoraCardBgElevated)
                            .testTag("btn_toggle_balance_visibility")
                    ) {
                        Icon(
                            imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle balance visibility",
                            tint = FinoraTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Prominent Safe to Spend Card Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, FinoraCardBorder.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .clickable { onInspectSafeSpend() }
                        .testTag("hero_safe_to_spend_box"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBgElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(FinoraLime)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SAFE TO SPEND",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.2.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FinoraLime
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBalanceVisible) FinancialEngine.formatNaira(safeToSpend.safeToSpend) else "₦ ••••••",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = FinoraTextPrimary
                            )
                            Text(
                                text = "After upcoming bills, savings & reserves",
                                style = MaterialTheme.typography.bodySmall,
                                color = FinoraTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(FinoraLime.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Inspect calculation",
                                tint = FinoraLime,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Segmented Toggle Pill Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(FinoraDarkBg)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FinoraHeroPill(
                        title = "Total Money",
                        isSelected = displayMode == "TOTAL_MONEY",
                        onClick = { onModeChange("TOTAL_MONEY") },
                        testTag = "pill_total_money"
                    )
                    FinoraHeroPill(
                        title = "Net Worth",
                        isSelected = displayMode == "NET_WORTH",
                        onClick = { onModeChange("NET_WORTH") },
                        testTag = "pill_net_worth"
                    )
                    FinoraHeroPill(
                        title = "Safe to Spend",
                        isSelected = displayMode == "SAFE_TO_SPEND",
                        onClick = { onModeChange("SAFE_TO_SPEND") },
                        testTag = "pill_safe_to_spend"
                    )
                }
            }
        }
    }
}

@Composable
private fun FinoraHeroPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (isSelected) FinoraLime else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF090E17) else FinoraTextSecondary
        )
    }
}

@Composable
fun MonthlySnapshotRow(
    income: Double,
    expenses: Double,
    saved: Double,
    incomeGrowth: Double = 12.0,
    expensesGrowth: Double = 7.0,
    savedGrowth: Double = 21.0,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "This Month",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = FinoraTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SnapshotCard(
                title = "Income",
                amount = income,
                trend = "+${incomeGrowth.toInt()}%",
                trendColor = IncomeGreen,
                modifier = Modifier.weight(1f)
            )
            SnapshotCard(
                title = "Spent",
                amount = expenses,
                trend = "+${expensesGrowth.toInt()}%",
                trendColor = FinoraOrange,
                modifier = Modifier.weight(1f)
            )
            SnapshotCard(
                title = "Saved",
                amount = saved,
                trend = "+${savedGrowth.toInt()}%",
                trendColor = FinoraLime,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SnapshotCard(
    title: String,
    amount: Double,
    trend: String,
    trendColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = FinoraTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = FinancialEngine.formatNaira(amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = FinoraTextPrimary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = trend,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = trendColor
                )
            }
        }
    }
}

@Composable
fun FinoraHealthScoreCard(
    report: FinancialHealthReport,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(20.dp))
            .clickable { onInspect() }
            .testTag("financial_health_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Financial Health",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${report.totalScore}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = FinoraTextPrimary
                    )
                    Text(
                        text = " / 100",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FinoraTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FinoraLime.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = report.statusLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = FinoraLime
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "↑ ${report.scoreChangeFromLastMonth} pts from last month",
                        style = MaterialTheme.typography.labelSmall,
                        color = FinoraTextSecondary
                    )
                }
            }

            // Semi-circular / Gauge visual on the right
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(60.dp)) {
                    val stroke = 6.dp.toPx()
                    drawArc(
                        color = FinoraCardBorder,
                        startAngle = 140f,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = FinoraLime,
                        startAngle = 140f,
                        sweepAngle = (260f * (report.totalScore / 100f)),
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = FinoraLime,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FinoraAiInsightCard(
    onSeeWhy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, FinoraPurple.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .testTag("ai_insight_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(FinoraPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = FinoraPurple,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FINANCIAL INSIGHT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                        color = FinoraPurple
                    )
                }

                TextButton(onClick = onSeeWhy) {
                    Text(
                        text = "See why →",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FinoraLime
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your food spending increased 27% this month.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = FinoraTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "You spent ₦48,200 on food, which is ₦10,200 above your 3-month average. Food delivery apps were the main driver.",
                style = MaterialTheme.typography.bodySmall,
                color = FinoraTextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeToSpendBreakdownSheet(
    breakdown: SafeToSpendBreakdown,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FinoraCardBg,
        modifier = Modifier.testTag("safe_to_spend_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Safe to Spend Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Close breakdown",
                        tint = FinoraTextPrimary
                    )
                }
            }

            Text(
                text = "Finora deterministically calculates what you can spend right now without risking your commitments or emergency buffer.",
                style = MaterialTheme.typography.bodySmall,
                color = FinoraTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            BreakdownRow(
                label = "Current Available Cash",
                subtitle = "Liquid checking, wallets & physical cash",
                amount = breakdown.currentAvailableMoney,
                prefix = "+",
                color = IncomeGreen
            )
            BreakdownRow(
                label = "Upcoming Commitments",
                subtitle = "Rent, Spectranet Fiber, subscriptions",
                amount = breakdown.upcomingBills,
                prefix = "-",
                color = ExpenseRed
            )
            BreakdownRow(
                label = "Planned Savings Target",
                subtitle = "Monthly auto-commitments to Emergency & Mac goals",
                amount = breakdown.plannedSavings,
                prefix = "-",
                color = ExpenseRed
            )
            BreakdownRow(
                label = "Debt Obligations",
                subtitle = "Active installment loans & store credit",
                amount = breakdown.debtObligations,
                prefix = "-",
                color = ExpenseRed
            )
            BreakdownRow(
                label = "Emergency Safety Reserve",
                subtitle = "Minimum 1-month baseline protection",
                amount = breakdown.requiredReserves,
                prefix = "-",
                color = ExpenseRed
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, FinoraLime.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = FinoraCardBgElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EQUALS SAFE TO SPEND",
                            style = MaterialTheme.typography.labelSmall,
                            color = FinoraLime,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Available for discretionary spending",
                            style = MaterialTheme.typography.bodySmall,
                            color = FinoraTextSecondary
                        )
                    }
                    Text(
                        text = FinancialEngine.formatNaira(breakdown.safeToSpend),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FinoraLime
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    subtitle: String,
    amount: Double,
    prefix: String,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FinoraTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = FinoraTextTertiary
                )
            }
            Text(
                text = "$prefix ${FinancialEngine.formatNaira(amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(FinoraCardBorder.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun CommitmentTimelineItem(
    bill: BillEntity,
    onTogglePaid: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    val dueDateStr = formatter.format(Date(bill.nextDueDateMillis))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(14.dp))
            .testTag("bill_item_${bill.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (bill.isPaid) FinoraCardBg.copy(alpha = 0.5f) else FinoraCardBg
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (bill.isPaid) IncomeGreen.copy(alpha = 0.15f)
                            else FinoraCardBgElevated
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dueDateStr,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (bill.isPaid) IncomeGreen else FinoraLime
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = bill.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = FinoraTextPrimary
                    )
                    Text(
                        text = if (bill.isPaid) "Paid for this cycle" else "Due $dueDateStr • ${bill.recurrence.lowercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (bill.isPaid) IncomeGreen else FinoraTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = FinancialEngine.formatNaira(bill.amount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (bill.isPaid) FinoraTextTertiary else FinoraTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onTogglePaid,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bill.isPaid) FinoraCardBgElevated else FinoraLime
                    ),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("btn_toggle_bill_${bill.id}")
                ) {
                    Text(
                        text = if (bill.isPaid) "Undo" else "Mark Paid",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (bill.isPaid) FinoraTextSecondary else Color(0xFF090E17)
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsGoalCard(
    goal: GoalEntity,
    onContribute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (goal.currentAmount / goal.targetAmount.coerceAtLeast(1.0)).toFloat().coerceIn(0f, 1f)
    val pct = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(16.dp))
            .testTag("goal_item_${goal.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
                Text(
                    text = "$pct%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = FinoraLime
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${FinancialEngine.formatNaira(goal.currentAmount)} of ${FinancialEngine.formatNaira(goal.targetAmount)}",
                style = MaterialTheme.typography.bodySmall,
                color = FinoraTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = FinoraLime,
                trackColor = FinoraDarkBg
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Target: ${FinancialEngine.formatNaira(goal.recommendedMonthlyContribution)}/mo",
                    style = MaterialTheme.typography.labelSmall,
                    color = FinoraTextTertiary
                )
                Button(
                    onClick = onContribute,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FinoraCardBgElevated),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("btn_contribute_goal_${goal.id}")
                ) {
                    Text(
                        text = "+ Contribute",
                        style = MaterialTheme.typography.labelSmall,
                        color = FinoraLime,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
