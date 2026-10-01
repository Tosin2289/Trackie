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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.viewmodel.TrackieViewModel

@Composable
fun FinancialAutopsyScreen(
    viewModel: TrackieViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("financial_autopsy_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_autopsy_back")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FinoraTextPrimary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "September Financial Autopsy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
                Text(
                    text = "1 – 30 Sep 2026",
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
            // Top 4 Metrics 2x2 Grid
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, FinoraCardBorder, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AutopsyMetricBox("Income", FinancialEngine.formatNaira(320000.0), FinoraCyan)
                            AutopsyMetricBox("Expenses", FinancialEngine.formatNaira(247600.0), FinoraTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AutopsyMetricBox("Saved", FinancialEngine.formatNaira(72400.0), FinoraLime)
                            AutopsyMetricBox("Savings rate", "22.6%", FinoraLime)
                        }
                    }
                }
            }

            // WHAT WENT WELL
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, FinoraCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "WHAT WENT WELL",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight.Bold,
                            color = IncomeGreen
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Savings increased 18%", style = MaterialTheme.typography.bodyMedium, color = FinoraTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Transport decreased 12%", style = MaterialTheme.typography.bodyMedium, color = FinoraTextPrimary)
                        }
                    }
                }
            }

            // WATCH OUT
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, FinoraCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "WATCH",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight.Bold,
                            color = FinoraOrange
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = FinoraOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Food increased 27%", style = MaterialTheme.typography.bodyMedium, color = FinoraTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = FinoraOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Entertainment increased 34%", style = MaterialTheme.typography.bodyMedium, color = FinoraTextPrimary)
                        }
                    }
                }
            }

            // BIGGEST FINANCIAL LEAK
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ExpenseRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.08f))
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
                                text = "BIGGEST FINANCIAL LEAK",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Food Delivery",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FinoraTextPrimary
                            )
                        }
                        Text(
                            text = FinancialEngine.formatNaira(19400.0),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ExpenseRed
                        )
                    }
                }
            }

            // GOAL PROGRESS & AI SUMMARY
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, FinoraCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("GOAL PROGRESS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraTextSecondary)
                            Text("Emergency Fund: 64%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = FinoraLime)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = FinoraPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ARIA Summary", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraPurple)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "“If you maintain your current 22.6% savings rate, you will reach your ₦1,000,000 emergency fund approximately two months earlier than planned.”",
                            style = MaterialTheme.typography.bodySmall,
                            color = FinoraTextPrimary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun AutopsyMetricBox(label: String, value: String, valueColor: Color) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = FinoraTextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
