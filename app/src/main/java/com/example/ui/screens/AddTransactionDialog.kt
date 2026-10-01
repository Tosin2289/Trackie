package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.model.AccountEntity
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.TransactionEntity
import com.example.domain.FinancialEngine
import com.example.ui.theme.BrandEmerald
import com.example.ui.viewmodel.TrackieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    viewModel: TrackieViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val isNlParsing by viewModel.isNlParsing.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableStateOf(0) } // 0: AI Natural Language, 1: Manual

    // Form fields
    var naturalLanguageInput by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("EXPENSE") } // EXPENSE, INCOME, TRANSFER, SAVINGS, DEBT_GIVEN
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()?.name ?: "Food & Dining") }
    var subCategory by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var familyRecipient by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Bank Transfer") }
    var selectedAccount by remember { mutableStateOf(accounts.firstOrNull()?.name ?: "GTBank Main") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("add_transaction_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Record Transaction",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_add_tx")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Selector: AI vs Manual
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Quick Add")
                        }
                    },
                    modifier = Modifier.testTag("tab_ai_quick_add")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Manual Form")
                        }
                    },
                    modifier = Modifier.testTag("tab_manual_add")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTabIndex == 0) {
                // Natural Language UI
                Text(
                    text = "Type naturally like a bank alert or note:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = naturalLanguageInput,
                    onValueChange = { naturalLanguageInput = it },
                    placeholder = { Text("e.g. Spent ₦4,500 on lunch at Chicken Republic, or Lent David ₦20,000") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_natural_language"),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Suggestion chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        label = "₦3,500 lunch",
                        onClick = { naturalLanguageInput = "Spent ₦3,500 on lunch with team" }
                    )
                    SuggestionChip(
                        label = "₦15,000 power",
                        onClick = { naturalLanguageInput = "Paid ₦15,000 for electricity token" }
                    )
                    SuggestionChip(
                        label = "₦20k to Mum",
                        onClick = { naturalLanguageInput = "Sent ₦20,000 to Mum for upkeep" }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.parseNaturalLanguage(naturalLanguageInput) { parsed ->
                            amountText = parsed.amount.toInt().toString()
                            selectedType = parsed.type
                            selectedCategory = parsed.categoryName
                            subCategory = parsed.subCategory
                            merchant = parsed.merchant
                            notes = parsed.notes
                            selectedTabIndex = 1 // Switch to review & save in manual tab!
                        }
                    },
                    enabled = naturalLanguageInput.isNotBlank() && !isNlParsing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_parse_natural_language"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald)
                ) {
                    if (isNlParsing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Interpreting with Gemini...")
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Interpret & Review")
                    }
                }
            } else {
                // Manual Form UI
                // 1. Amount input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₦)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tx_amount"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TypePill(label = "Expense", isSelected = selectedType == "EXPENSE", onClick = { selectedType = "EXPENSE" })
                    TypePill(label = "Income", isSelected = selectedType == "INCOME", onClick = { selectedType = "INCOME" })
                    TypePill(label = "Transfer", isSelected = selectedType == "TRANSFER", onClick = { selectedType = "TRANSFER" })
                    TypePill(label = "Lent", isSelected = selectedType == "DEBT_GIVEN", onClick = { selectedType = "DEBT_GIVEN" })
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Category & Subcategory
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { selectedCategory = it },
                    label = { Text("Category") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tx_category"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text("Merchant / Person (e.g. Chicken Republic, Total, Mum)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tx_merchant"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Family Support Recipient field
                if (selectedCategory.contains("Family", ignoreCase = true) || merchant.equals("Mum", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = familyRecipient,
                        onValueChange = { familyRecipient = it },
                        label = { Text("Family Member (Mum, Sibling, Dad, Uncle)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_tx_family_recipient"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Source Account
                OutlinedTextField(
                    value = selectedAccount,
                    onValueChange = { selectedAccount = it },
                    label = { Text("Account (GTBank, OPay, Cash)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tx_account"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Payment Method
                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = { paymentMethod = it },
                    label = { Text("Payment Method (Bank Transfer, POS, Cash, Debit Card, USSD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            val acc = accounts.find { it.name.contains(selectedAccount, ignoreCase = true) } ?: accounts.firstOrNull()
                            val accId = acc?.id ?: 1L
                            val accName = acc?.name ?: "GTBank Main"

                            val newTx = TransactionEntity(
                                amount = amt,
                                type = selectedType,
                                categoryName = selectedCategory.ifBlank { "Food & Dining" },
                                subCategory = subCategory,
                                accountId = accId,
                                accountName = accName,
                                merchant = merchant,
                                notes = notes,
                                familyRecipient = familyRecipient.ifBlank { null },
                                paymentMethod = paymentMethod,
                                timestamp = System.currentTimeMillis()
                            )
                            viewModel.addTransaction(newTx)
                            onDismiss()
                        }
                    },
                    enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_transaction"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald)
                ) {
                    Text("Save Transaction", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun TypePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SuggestionChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
