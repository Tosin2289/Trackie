package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.TrackieViewModel

data class BudgetCategoryItem(
    val name: String,
    val spent: Double,
    val limit: Double,
    val iconColor: Color
)

@Composable
fun BudgetsScreen(
    viewModel: TrackieViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Categories, 1: Overview

    val totalBudget = 400000.0
    val totalSpent = 247600.0
    val remaining = totalBudget - totalSpent
    val progress = (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f)

    val categoryBudgets = listOf(
        BudgetCategoryItem("Food & Dining", 48200.0, 60000.0, FinoraRed),
        BudgetCategoryItem("Transport & Fuel", 32500.0, 40000.0, FinoraOrange),
        BudgetCategoryItem("Bills & Utilities", 35000.0, 50000.0, FinoraPurple),
        BudgetCategoryItem("Data & Airtime", 18700.0, 25000.0, FinoraCyan),
        BudgetCategoryItem("Shopping", 25900.0, 30000.0, Color(0xFFEC4899))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("budgets_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_budgets_back")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FinoraTextPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Budgets",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
            }
            IconButton(onClick = { viewModel.showAddTransactionSheet.value = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Budget", tint = FinoraLime)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Monthly Budget Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, FinoraCardBorder, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MONTHLY BUDGET",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            color = FinoraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = FinancialEngine.formatNaira(totalBudget),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = FinoraTextPrimary
                        )
                    }
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FinoraLime
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = FinoraLime,
                    trackColor = FinoraDarkBg
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Spent", style = MaterialTheme.typography.labelSmall, color = FinoraTextTertiary)
                        Text(FinancialEngine.formatNaira(totalSpent), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Remaining", style = MaterialTheme.typography.labelSmall, color = FinoraTextTertiary)
                        Text(FinancialEngine.formatNaira(remaining), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FinoraLime)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs: Categories vs Overview
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Categories", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) FinoraLime else FinoraTextSecondary) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Overview", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) FinoraLime else FinoraTextSecondary) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Category Budget Progress List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categoryBudgets) { item ->
                val catProgress = (item.spent / item.limit).toFloat().coerceIn(0f, 1f)
                val isWarning = catProgress >= 0.85f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, FinoraCardBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(item.iconColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FinoraTextPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isWarning) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = FinoraOrange,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = "${FinancialEngine.formatNaira(item.spent)} / ${FinancialEngine.formatNaira(item.limit)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWarning) FinoraOrange else FinoraTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { catProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isWarning) FinoraOrange else item.iconColor,
                            trackColor = FinoraDarkBg
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
