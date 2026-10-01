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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraCardBgElevated
import com.example.ui.theme.FinoraCardBorder
import com.example.ui.theme.FinoraCyan
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraOrange
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.theme.FinoraTextTertiary
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.TrackieViewModel
import java.util.Locale

@Composable
fun WhatIfSimulatorScreen(
    viewModel: TrackieViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeScenario by viewModel.selectedSimulationScenario.collectAsStateWithLifecycle()
    val paramVal by viewModel.simulationParamValue.collectAsStateWithLifecycle()
    val simResult by viewModel.simulationResult.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("what_if_simulator_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_what_if_back")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FinoraTextPrimary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "What If? Simulator",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
                Text(
                    text = "Simulate financial decisions before taking them",
                    style = MaterialTheme.typography.bodySmall,
                    color = FinoraTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Scenario Selection Buttons
            item {
                Text("Select Decision Scenario", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SimPill("Save ₦30,000 extra every month", activeScenario == "EXTRA_SAVINGS") {
                        viewModel.setSimulation("EXTRA_SAVINGS", 30000.0)
                    }
                    SimPill("Reduce Food & Dining spending by 20%", activeScenario == "REDUCE_FOOD") {
                        viewModel.setSimulation("REDUCE_FOOD", 20.0)
                    }
                    SimPill("Buy ₦250,000 phone", activeScenario == "BUY_PHONE") {
                        viewModel.setSimulation("BUY_PHONE", 250000.0)
                    }
                    SimPill("Income drops by 20%", activeScenario == "INCOME_DROP") {
                        viewModel.setSimulation("INCOME_DROP", 20.0)
                    }
                    SimPill("Zero income for 2 months", activeScenario == "ZERO_INCOME") {
                        viewModel.setSimulation("ZERO_INCOME", 2.0)
                    }
                }
            }

            // Interactive Slider for parameter adjustment
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, FinoraCardBorder, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Scenario Parameter", style = MaterialTheme.typography.bodyMedium, color = FinoraTextSecondary)
                            Text(
                                text = when (activeScenario) {
                                    "EXTRA_SAVINGS" -> FinancialEngine.formatNaira(paramVal) + "/mo"
                                    "BUY_PHONE" -> FinancialEngine.formatNaira(paramVal)
                                    "REDUCE_FOOD", "INCOME_DROP" -> "${paramVal.toInt()}%"
                                    else -> "${paramVal.toInt()} months"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = FinoraLime
                            )
                        }

                        Slider(
                            value = paramVal.toFloat(),
                            onValueChange = { viewModel.simulationParamValue.value = it.toDouble() },
                            valueRange = when (activeScenario) {
                                "EXTRA_SAVINGS" -> 10000f..100000f
                                "BUY_PHONE" -> 100000f..800000f
                                "REDUCE_FOOD", "INCOME_DROP" -> 5f..50f
                                else -> 1f..6f
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = FinoraLime,
                                activeTrackColor = FinoraLime,
                                inactiveTrackColor = FinoraDarkBg
                            )
                        )
                    }
                }
            }

            // Projected Impact Outcomes
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, FinoraCardBorder, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBgElevated)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "PROJECTED OUTCOME",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            color = FinoraLime
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = simResult.scenarioTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FinoraTextPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SimCol("New Safe-to-Spend", FinancialEngine.formatNaira(simResult.newSafeToSpend), if (simResult.newSafeToSpend > 0) FinoraLime else ExpenseRed)
                            SimCol("Emergency Runway", "${String.format(Locale.US, "%.1f", simResult.emergencyRunwayMonths)} mos", FinoraTextPrimary)
                            SimCol("1-Year Net Worth", FinancialEngine.formatNaira(simResult.projectedNetWorth1Year), FinoraCyan)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(FinoraDarkBg)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = simResult.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = FinoraTextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Future Horizon Breakdown (3, 6, 12 months)
            if (activeScenario == "EXTRA_SAVINGS") {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, FinoraCardBorder, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Accumulated Extra Wealth", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                HorizonBox("3 Months", FinancialEngine.formatNaira(paramVal * 3))
                                HorizonBox("6 Months", FinancialEngine.formatNaira(paramVal * 6))
                                HorizonBox("12 Months", FinancialEngine.formatNaira(paramVal * 12))
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SimPill(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, if (isSelected) FinoraLime else FinoraCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) FinoraLime.copy(alpha = 0.12f) else FinoraCardBg
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
                color = if (isSelected) FinoraLime else FinoraTextPrimary
            )
            if (isSelected) {
                Text("Active", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraLime)
            }
        }
    }
}

@Composable
private fun SimCol(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = FinoraTextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun HorizonBox(period: String, amount: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(period, style = MaterialTheme.typography.labelSmall, color = FinoraTextTertiary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(amount, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FinoraLime)
    }
}
