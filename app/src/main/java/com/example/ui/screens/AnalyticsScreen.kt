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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.components.CashFlowBarChart
import com.example.ui.components.SpendingDonutChart
import com.example.ui.navigation.Screen
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraCardBorder
import com.example.ui.theme.FinoraCyan
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraPurple
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.viewmodel.TrackieViewModel

@Composable
fun AnalyticsScreen(
    viewModel: TrackieViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTimePeriod by remember { mutableStateOf("30D") }
    val periods = listOf("7D", "30D", "3M", "6M", "1Y")

    val cashFlow by viewModel.cashFlowSummary.collectAsStateWithLifecycle()
    val categorySpends by viewModel.categorySpends.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title & Time Range Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Analytics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FinoraTextPrimary
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(FinoraCardBg)
                    .border(1.dp, FinoraCardBorder, RoundedCornerShape(10.dp))
                    .padding(3.dp)
            ) {
                periods.forEach { p ->
                    val isSel = selectedTimePeriod == p
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) FinoraLime else Color.Transparent)
                            .clickable { selectedTimePeriod = p }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dedicated Tools Shortcuts: Financial Autopsy & What-If Simulator
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, FinoraCardBorder, RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(Screen.FinancialAutopsy) }
                            .testTag("btn_goto_autopsy"),
                        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(FinoraCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = FinoraCyan, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.padding(start = 10.dp))
                            Column {
                                Text("Autopsy", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
                                Text("Monthly review", style = MaterialTheme.typography.labelSmall, color = FinoraTextSecondary)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, FinoraCardBorder, RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(Screen.WhatIfSimulator) }
                            .testTag("btn_goto_simulator"),
                        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(FinoraLime.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = FinoraLime, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.padding(start = 10.dp))
                            Column {
                                Text("What-If?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
                                Text("Simulator", style = MaterialTheme.typography.labelSmall, color = FinoraTextSecondary)
                            }
                        }
                    }
                }
            }

            // Cash Flow Distribution
            item {
                CashFlowBarChart(
                    income = cashFlow.income,
                    expenses = cashFlow.expenses,
                    saved = cashFlow.saved
                )
            }

            // Spending by Category Donut Chart
            item {
                SpendingDonutChart(
                    items = categorySpends,
                    onCategoryClick = { /* drill-down */ }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
