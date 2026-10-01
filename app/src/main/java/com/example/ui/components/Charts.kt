package com.example.ui.components

import com.example.ui.theme.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CategorySpendItem
import com.example.domain.FinancialEngine
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
import java.util.Locale

@Composable
fun CashFlowBarChart(
    income: Double,
    expenses: Double,
    saved: Double,
    modifier: Modifier = Modifier
) {
    var selectedBar by remember { mutableStateOf<String?>(null) }
    var selectedTimePeriod by remember { mutableStateOf("Month") }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(650))
    }

    val maxVal = maxOf(income, expenses, saved).coerceAtLeast(1.0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(20.dp))
            .background(FinoraCardBg)
            .padding(18.dp)
            .testTag("cash_flow_chart")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Cash Flow Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
                Text(
                    text = "Income vs. Spent vs. Saved",
                    style = MaterialTheme.typography.bodySmall,
                    color = FinoraTextSecondary
                )
            }

            // Period selector
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(FinoraDarkBg)
                    .padding(2.dp)
            ) {
                listOf("Week", "Month", "3M").forEach { p ->
                    val isSel = selectedTimePeriod == p
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) FinoraLime else Color.Transparent)
                            .clickable { selectedTimePeriod = p }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = p,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSel) Color(0xFF090E17) else FinoraTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual bars
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            val width = size.width
            val height = size.height
            val barWidth = 32.dp.toPx()
            val spacing = (width - (barWidth * 3)) / 4f

            // Baseline
            drawLine(
                color = FinoraCardBorder,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 1.dp.toPx()
            )

            // 1. Income Bar (Cyan)
            val incomeH = (income / maxVal * (height - 16.dp.toPx()) * animProgress.value).toFloat()
            val incomeX = spacing
            drawRoundRect(
                color = FinoraCyan,
                topLeft = Offset(incomeX, height - incomeH),
                size = Size(barWidth, incomeH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // 2. Expenses Bar (Red/Coral)
            val expenseH = (expenses / maxVal * (height - 16.dp.toPx()) * animProgress.value).toFloat()
            val expenseX = spacing * 2 + barWidth
            drawRoundRect(
                color = ExpenseRed,
                topLeft = Offset(expenseX, height - expenseH),
                size = Size(barWidth, expenseH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // 3. Saved Bar (Lime)
            val savedH = (saved / maxVal * (height - 16.dp.toPx()) * animProgress.value).toFloat()
            val savedX = spacing * 3 + (barWidth * 2)
            drawRoundRect(
                color = FinoraLime,
                topLeft = Offset(savedX, height - savedH),
                size = Size(barWidth, savedH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Legend row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ChartLegendItem(
                label = "Income",
                amount = income,
                color = FinoraCyan,
                onClick = { selectedBar = "Income: ${FinancialEngine.formatNaira(income)}" }
            )
            ChartLegendItem(
                label = "Spent",
                amount = expenses,
                color = ExpenseRed,
                onClick = { selectedBar = "Spent: ${FinancialEngine.formatNaira(expenses)}" }
            )
            ChartLegendItem(
                label = "Saved",
                amount = saved,
                color = FinoraLime,
                onClick = { selectedBar = "Saved: ${FinancialEngine.formatNaira(saved)}" }
            )
        }
    }
}

@Composable
private fun ChartLegendItem(
    label: String,
    amount: Double,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = FinoraTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = FinancialEngine.formatNaira(amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = FinoraTextPrimary
        )
    }
}

@Composable
fun SpendingDonutChart(
    items: List<CategorySpendItem>,
    modifier: Modifier = Modifier,
    onCategoryClick: (CategorySpendItem) -> Unit = {}
) {
    val totalSpend = items.sumOf { it.amount }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(items) {
        animProgress.animateTo(1f, animationSpec = tween(750))
    }

    val palette = listOf(
        FinoraRed,        // Food & Dining
        FinoraOrange,     // Transport & Fuel
        FinoraPurple,     // Bills & Utilities
        FinoraCyan,       // Data & Airtime
        Color(0xFFEC4899), // Shopping
        Color(0xFFA855F7), // Entertainment
        FinoraTextTertiary // Others
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(20.dp))
            .background(FinoraCardBg)
            .padding(18.dp)
            .testTag("spending_donut_chart")
    ) {
        Text(
            text = "Where your money went",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FinoraTextPrimary
        )
        Text(
            text = "Categorized spending breakdown",
            style = MaterialTheme.typography.bodySmall,
            color = FinoraTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(150.dp)) {
                val strokeWidth = 20.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val centerOffset = Offset(size.width / 2, size.height / 2)

                if (items.isEmpty() || totalSpend <= 0) {
                    drawCircle(
                        color = FinoraCardBorder,
                        radius = radius,
                        center = centerOffset,
                        style = Stroke(width = strokeWidth)
                    )
                } else {
                    var startAngle = -90f
                    items.forEachIndexed { index, item ->
                        val sweep = ((item.amount / totalSpend) * 360f * animProgress.value).toFloat()
                        val color = palette.getOrElse(index) { Color.Gray }
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += sweep
                    }
                }
            }

            // Center Content: Total Spent
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Spent",
                    style = MaterialTheme.typography.labelSmall,
                    color = FinoraTextTertiary
                )
                Text(
                    text = FinancialEngine.formatNaira(totalSpend),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category breakdown compact rows
        items.take(5).forEachIndexed { idx, item ->
            val color = palette.getOrElse(idx) { Color.Gray }
            FinoraCategoryRow(
                name = item.categoryName,
                amount = item.amount,
                percentage = item.percentageOfTotal,
                color = color,
                onClick = { onCategoryClick(item) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun FinoraCategoryRow(
    name: String,
    amount: Double,
    percentage: Double,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = FinoraTextPrimary
                )
            }
            Text(
                text = "${FinancialEngine.formatNaira(amount)} (${String.format(Locale.US, "%.0f", percentage)}%)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = FinoraTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(FinoraDarkBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((percentage / 100f).toFloat().coerceIn(0f, 1f))
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}
