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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.model.BillEntity
import com.example.domain.FinancialEngine
import com.example.ui.components.CommitmentTimelineItem
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraCardBorder
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.viewmodel.TrackieViewModel

@Composable
fun UpcomingBillsScreen(
    viewModel: TrackieViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bills by viewModel.bills.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalUpcoming = bills.filter { !it.isPaid }.sumOf { it.amount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("upcoming_bills_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_bills_back")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FinoraTextPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Upcoming Bills",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FinoraTextPrimary
                )
            }
            IconButton(onClick = { showAddDialog = true }, modifier = Modifier.testTag("btn_bills_add")) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Bill", tint = FinoraLime)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Total Upcoming Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, FinoraCardBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("TOTAL DUE THIS CYCLE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(FinancialEngine.formatNaira(totalUpcoming), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = FinoraTextPrimary)
                }
                Text("${bills.count { !it.isPaid }} pending", style = MaterialTheme.typography.bodySmall, color = FinoraLime)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bills) { bill ->
                CommitmentTimelineItem(
                    bill = bill,
                    onTogglePaid = { viewModel.toggleBillPaid(bill) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var amountText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Recurring Bill") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Bill Name (e.g. Netflix, Rent)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (₦)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && amt > 0) {
                            viewModel.addBill(
                                BillEntity(
                                    title = title,
                                    amount = amt,
                                    dueDayOfMonth = 15,
                                    nextDueDateMillis = System.currentTimeMillis() + (5L * 24 * 3600 * 1000),
                                    category = "Bills"
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FinoraLime)
                ) {
                    Text("Save", color = Color(0xFF090E17), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
