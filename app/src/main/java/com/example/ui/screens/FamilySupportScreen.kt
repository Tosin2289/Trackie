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
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Person
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
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraCardBorder
import com.example.ui.theme.FinoraCyan
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraPurple
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.theme.FinoraTextTertiary
import com.example.ui.viewmodel.TrackieViewModel

data class FamilyMemberSupport(
    val recipient: String,
    val relationship: String,
    val thisMonth: Double,
    val ytd: Double,
    val color: Color
)

@Composable
fun FamilySupportScreen(
    viewModel: TrackieViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalThisMonth = 35000.0
    val totalYtd = 295000.0

    val members = listOf(
        FamilyMemberSupport("Mum", "Mother (Monthly upkeep)", 20000.0, 180000.0, FinoraLime),
        FamilyMemberSupport("Sibling (Tolu)", "Sister (Campus allowance)", 10000.0, 85000.0, FinoraCyan),
        FamilyMemberSupport("Extended Family", "Medical assistance & emergencies", 5000.0, 30000.0, FinoraPurple)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("family_support_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_family_back")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FinoraTextPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Family Support",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
            }
            IconButton(onClick = { viewModel.showAddTransactionSheet.value = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Record Support", tint = FinoraLime)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Summary Card
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("THIS MONTH", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraTextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(FinancialEngine.formatNaira(totalThisMonth), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = FinoraLime)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("YEAR TO DATE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraTextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(FinancialEngine.formatNaira(totalYtd), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = FinoraCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Support Breakdown by Recipient", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(members) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, FinoraCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(item.color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = item.color, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.recipient, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
                                Text(item.relationship, style = MaterialTheme.typography.bodySmall, color = FinoraTextSecondary)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(FinancialEngine.formatNaira(item.thisMonth), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
                            Text("YTD: ${FinancialEngine.formatNaira(item.ytd)}", style = MaterialTheme.typography.labelSmall, color = FinoraTextTertiary)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
