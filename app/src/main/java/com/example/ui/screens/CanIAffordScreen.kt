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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun CanIAffordScreen(
    viewModel: TrackieViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var itemName by remember { mutableStateOf("New Smartphone") }
    var itemCostText by remember { mutableStateOf("250000") }
    var hasEvaluated by remember { mutableStateOf(true) }

    val safeToSpend by viewModel.safeToSpendBreakdown.collectAsStateWithLifecycle()
    val cost = itemCostText.toDoubleOrNull() ?: 250000.0

    val availableCash = 620000.0
    val upcomingCommitments = 180000.0
    val emergencyReserve = 150000.0
    val remainingDiscretionary = availableCash - upcomingCommitments - emergencyReserve // 290,000

    val canAfford = remainingDiscretionary >= cost

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("can_i_afford_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_afford_back")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FinoraTextPrimary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Can I afford this?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FinoraTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, FinoraCardBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "What are you planning to buy?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FinoraTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = itemCostText,
                    onValueChange = { itemCostText = it },
                    label = { Text("Cost (₦)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { hasEvaluated = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FinoraLime)
                ) {
                    Text(
                        text = "Evaluate Financial Impact",
                        color = Color(0xFF090E17),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (hasEvaluated) {
            Spacer(modifier = Modifier.height(16.dp))

            // AI Verdict Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, FinoraCyan.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = FinoraCardBgElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(FinoraCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = FinoraCyan, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Here's what I found:",
                            style = MaterialTheme.typography.labelSmall,
                            color = FinoraCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You can technically afford it, but it will affect your financial plan.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = FinoraTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Position Breakdown
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, FinoraCardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Position", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    AffordRow("Available cash", FinancialEngine.formatNaira(availableCash), IncomeGreen)
                    AffordRow("Upcoming commitments", "-${FinancialEngine.formatNaira(upcomingCommitments)}", ExpenseRed)
                    AffordRow("Emergency reserve target", "-${FinancialEngine.formatNaira(emergencyReserve)}", ExpenseRed)
                    AffordRow("Remaining discretionary", FinancialEngine.formatNaira(remainingDiscretionary), FinoraLime)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Warning consequence card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, FinoraOrange.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = FinoraOrange.copy(alpha = 0.08f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = FinoraOrange, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "A ${FinancialEngine.formatNaira(cost)} purchase would leave only ~₦40,000 in discretionary funds and delay your emergency fund goal by ~3 months.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FinoraTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Alternative: Save First
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, FinoraLime.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = FinoraCardBgElevated)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = FinoraLime, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Alternative: Save First", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraLime)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Save ₦50,000/month and you can buy it in ~5 months without affecting your emergency reserve.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FinoraTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun AffordRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = FinoraTextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
