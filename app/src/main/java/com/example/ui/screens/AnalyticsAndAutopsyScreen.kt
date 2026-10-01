package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.FinancialEngine
import com.example.ui.components.CashFlowBarChart
import com.example.ui.components.SpendingDonutChart
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandMint
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.SavingsBlue
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.TrackieViewModel
import java.util.Locale

@Composable
fun AnalyticsAndAutopsyScreen(
    viewModel: TrackieViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Financial Autopsy, 1: What-If Simulator, 2: Spending Breakdown, 3: Health Pillars

    val cashFlow by viewModel.cashFlowSummary.collectAsStateWithLifecycle()
    val categorySpends by viewModel.categorySpends.collectAsStateWithLifecycle()
    val autopsy by viewModel.financialAutopsy.collectAsStateWithLifecycle()
    val healthReport by viewModel.financialHealth.collectAsStateWithLifecycle()
    val simulationResult by viewModel.simulationResult.collectAsStateWithLifecycle()
    val activeScenario by viewModel.selectedSimulationScenario.collectAsStateWithLifecycle()
    val paramVal by viewModel.simulationParamValue.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("analytics_autopsy_screen")
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "Analytics & Autopsy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Autopsy", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_autopsy")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("What-If", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_what_if")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Spending", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_spending")
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Health", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_health")
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // --- SIGNATURE FINANCIAL AUTOPSY ---
                    item {
                        AutopsyHeaderCard(
                            monthTitle = autopsy.monthTitle,
                            income = autopsy.income,
                            expenses = autopsy.expenses,
                            saved = autopsy.saved,
                            savingsRate = autopsy.savingsRate
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        AutopsySectionCard(
                            title = "What Went Well",
                            items = autopsy.whatWentWell,
                            iconColor = IncomeGreen,
                            isPositive = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        AutopsySectionCard(
                            title = "Watch Out / Leaks",
                            items = autopsy.watchOut,
                            iconColor = ExpenseRed,
                            isPositive = false
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("biggest_leak_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.08f))
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
                                        text = "BIGGEST FINANCIAL LEAK",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ExpenseRed
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = autopsy.biggestFinancialLeakName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = FinancialEngine.formatNaira(autopsy.biggestFinancialLeakAmount),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Advisor Autopsy Insight",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = autopsy.advisorInsight,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

                1 -> {
                    // --- WHAT-IF FINANCIAL SIMULATOR ---
                    item {
                        Text(
                            text = "Interactive Financial Scenario Simulator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Simulate financial choices and see instantaneous projected impact on your Safe to Spend, Emergency runway, and Net worth.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Prebuilt Scenario Buttons
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ScenarioOptionButton(
                                title = "Buy ₦250,000 phone",
                                isSelected = activeScenario == "BUY_PHONE",
                                onClick = { viewModel.setSimulation("BUY_PHONE", 250000.0) }
                            )
                            ScenarioOptionButton(
                                title = "Save ₦30,000 extra each month",
                                isSelected = activeScenario == "EXTRA_SAVINGS",
                                onClick = { viewModel.setSimulation("EXTRA_SAVINGS", 30000.0) }
                            )
                            ScenarioOptionButton(
                                title = "Reduce Food & Dining spending by 20%",
                                isSelected = activeScenario == "REDUCE_FOOD",
                                onClick = { viewModel.setSimulation("REDUCE_FOOD", 20.0) }
                            )
                            ScenarioOptionButton(
                                title = "Income drops by 20%",
                                isSelected = activeScenario == "INCOME_DROP",
                                onClick = { viewModel.setSimulation("INCOME_DROP", 20.0) }
                            )
                            ScenarioOptionButton(
                                title = "Zero income for 2 months",
                                isSelected = activeScenario == "ZERO_INCOME",
                                onClick = { viewModel.setSimulation("ZERO_INCOME", 2.0) }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Simulation Result Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("simulation_result_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandNavy)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "PROJECTED OUTCOME",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.2.sp,
                                    color = BrandMint,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = simulationResult.scenarioTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SimMetricCol(
                                        label = "New Safe-to-Spend",
                                        value = FinancialEngine.formatNaira(simulationResult.newSafeToSpend),
                                        color = if (simulationResult.newSafeToSpend > 0) BrandMint else ExpenseRed
                                    )
                                    SimMetricCol(
                                        label = "Emergency Runway",
                                        value = "${String.format(Locale.US, "%.1f", simulationResult.emergencyRunwayMonths)} mos",
                                        color = Color.White
                                    )
                                    SimMetricCol(
                                        label = "1-Year Net Worth",
                                        value = FinancialEngine.formatNaira(simulationResult.projectedNetWorth1Year),
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = simulationResult.explanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

                2 -> {
                    // --- SPENDING BREAKDOWN & CASH FLOW ---
                    item {
                        SpendingDonutChart(items = categorySpends)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        CashFlowBarChart(
                            income = cashFlow.income,
                            expenses = cashFlow.expenses,
                            saved = cashFlow.saved
                        )
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

                3 -> {
                    // --- FINANCIAL HEALTH PILLARS ---
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("health_score_overview_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Overall Financial Health",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = healthReport.statusLabel,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = BrandEmerald,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = "${healthReport.totalScore}/100",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BrandEmerald
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                healthReport.components.forEach { comp ->
                                    HealthComponentRow(comp = comp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AutopsyHeaderCard(
    monthTitle: String,
    income: Double,
    expenses: Double,
    saved: Double,
    savingsRate: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("autopsy_header_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BrandNavy)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = monthTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AutopsyMetric(label = "Income", value = FinancialEngine.formatNaira(income), color = IncomeGreen)
                AutopsyMetric(label = "Expenses", value = FinancialEngine.formatNaira(expenses), color = ExpenseRed)
                AutopsyMetric(label = "Saved", value = FinancialEngine.formatNaira(saved), color = SavingsBlue)
                AutopsyMetric(label = "Rate", value = "${String.format(Locale.US, "%.1f", savingsRate)}%", color = BrandMint)
            }
        }
    }
}

@Composable
private fun AutopsyMetric(label: String, value: String, color: Color) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun AutopsySectionCard(
    title: String,
    items: List<String>,
    iconColor: Color,
    isPositive: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            items.forEach { line ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Text(text = "• ", fontWeight = FontWeight.Bold, color = iconColor)
                    Text(text = line, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ScenarioOptionButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            if (isSelected) {
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SimMetricCol(label: String, value: String, color: Color) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun HealthComponentRow(comp: com.example.domain.HealthScoreComponent) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = comp.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = "${comp.score} / ${comp.maxScore}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = comp.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (comp.score.toFloat() / comp.maxScore).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = BrandEmerald,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
