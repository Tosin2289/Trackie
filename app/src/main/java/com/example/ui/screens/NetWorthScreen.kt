package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.FinancialEngine
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraCardBorder
import com.example.ui.theme.FinoraCyan
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.theme.FinoraTextTertiary
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.TrackieViewModel

@Composable
fun NetWorthScreen(
    viewModel: TrackieViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Total, 1: Assets, 2: Liabilities

    val netWorth = 2840000.0
    val totalAssets = 2930000.0
    val totalLiabilities = 90000.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("net_worth_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_net_worth_back")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FinoraTextPrimary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Net Worth",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FinoraTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Net Worth Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, FinoraCardBorder, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ESTIMATED NET WORTH",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = FinancialEngine.formatNaira(netWorth),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FinoraTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Assets: ${FinancialEngine.formatNaira(totalAssets)} • Liabilities: ${FinancialEngine.formatNaira(totalLiabilities)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FinoraTextTertiary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Line Chart Curve (History)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val points = listOf(
                        Offset(0f, h * 0.8f),
                        Offset(w * 0.25f, h * 0.7f),
                        Offset(w * 0.5f, h * 0.55f),
                        Offset(w * 0.75f, h * 0.4f),
                        Offset(w, h * 0.15f)
                    )

                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            val pPrev = points[i - 1]
                            val pCurr = points[i]
                            val midX = (pPrev.x + pCurr.x) / 2
                            cubicTo(midX, pPrev.y, midX, pCurr.y, pCurr.x, pCurr.y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = FinoraLime,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Total", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Assets", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Liabilities", fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedTab != 2) {
                item {
                    Text("Assets", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FinoraLime)
                }
                item { NetWorthItem("Physical Cash", FinancialEngine.formatNaira(30000.0), FinoraCyan) }
                item { NetWorthItem("Bank Checking & Wallets", FinancialEngine.formatNaira(252500.0), FinoraCyan) }
                item { NetWorthItem("High-Yield Savings (Piggyvest)", FinancialEngine.formatNaira(150000.0), FinoraCyan) }
                item { NetWorthItem("Investments & Stocks (Cowrywise)", FinancialEngine.formatNaira(50000.0), FinoraCyan) }
                item { NetWorthItem("Property & Land Ownership", FinancialEngine.formatNaira(2500000.0), FinoraCyan) }
            }

            if (selectedTab != 1) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Liabilities", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ExpenseRed)
                }
                item { NetWorthItem("Gadget Store Credit", "-${FinancialEngine.formatNaira(25000.0)}", ExpenseRed) }
                item { NetWorthItem("Short-Term Personal Loan", "-${FinancialEngine.formatNaira(65000.0)}", ExpenseRed) }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun NetWorthItem(label: String, amount: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = FinoraTextPrimary)
            Text(amount, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
