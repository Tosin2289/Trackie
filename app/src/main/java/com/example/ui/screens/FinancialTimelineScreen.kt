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
import com.example.ui.theme.FinoraCardBorder
import com.example.ui.theme.FinoraCyan
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.theme.FinoraTextTertiary
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.TrackieViewModel

data class TimelineEvent(
    val dateLabel: String,
    val title: String,
    val category: String,
    val amount: Double,
    val isIncome: Boolean,
    val isUpcoming: Boolean
)

@Composable
fun FinancialTimelineScreen(
    viewModel: TrackieViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val events = listOf(
        TimelineEvent("Today, 30 Sep", "Freelance Milestone Payment", "Income", 150000.0, true, false),
        TimelineEvent("Today, 30 Sep", "Chicken Republic Team Lunch", "Food & Dining", -8000.0, false, false),
        TimelineEvent("Tomorrow, Oct 1", "Apartment Rent Contribution", "Housing", -50000.0, false, true),
        TimelineEvent("Oct 3", "Internet (Spectranet Fiber)", "Utilities", -15000.0, false, true),
        TimelineEvent("Oct 5", "Emergency Fund Auto-Save", "Savings", -30000.0, false, true),
        TimelineEvent("Oct 7", "Subscriptions (Netflix & Spotify)", "Entertainment", -8500.0, false, true),
        TimelineEvent("Oct 15", "Scheduled Client Retainer", "Income", 220000.0, true, true)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("financial_timeline_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_timeline_back")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FinoraTextPrimary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Financial Timeline",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
                Text(
                    text = "Chronological past & upcoming cash flow events",
                    style = MaterialTheme.typography.bodySmall,
                    color = FinoraTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(events) { ev ->
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
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (ev.isIncome) FinoraCyan else if (ev.isUpcoming) FinoraLime else ExpenseRed)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = ev.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FinoraTextPrimary
                                )
                                Text(
                                    text = "${ev.dateLabel} • ${ev.category}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FinoraTextSecondary
                                )
                            }
                        }

                        val sign = if (ev.isIncome) "+" else "-"
                        val color = if (ev.isIncome) FinoraCyan else ExpenseRed
                        Text(
                            text = "$sign${FinancialEngine.formatNaira(ev.amount)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
