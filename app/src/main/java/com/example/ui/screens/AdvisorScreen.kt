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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.navigation.Screen
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
import com.example.ui.viewmodel.AdvisorChatMessage
import com.example.ui.viewmodel.TrackieViewModel

@Composable
fun AdvisorScreen(
    viewModel: TrackieViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.advisorMessages.collectAsStateWithLifecycle()
    val isThinking by viewModel.isAdvisorThinking.collectAsStateWithLifecycle()

    var userQueryText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(horizontal = 16.dp)
            .testTag("advisor_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header: ARIA Brand Avatar & Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(FinoraPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = FinoraPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ARIA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = FinoraTextPrimary
                    )
                    Text(
                        text = "Always here for you",
                        style = MaterialTheme.typography.labelSmall,
                        color = FinoraTextSecondary
                    )
                }
            }

            Button(
                onClick = { viewModel.navigateTo(Screen.CanIAfford) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FinoraCardBgElevated),
                modifier = Modifier
                    .border(1.dp, FinoraLime.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .height(34.dp)
                    .testTag("btn_advisor_afford")
            ) {
                Icon(imageVector = Icons.Default.QuestionMark, contentDescription = null, tint = FinoraLime, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Afford?",
                    style = MaterialTheme.typography.labelSmall,
                    color = FinoraLime,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Conversational Insight Cards
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, FinoraCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Good morning, Tosin. Here's what I think you should pay attention to this week:",
                            style = MaterialTheme.typography.bodySmall,
                            color = FinoraTextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                InsightPillRow(
                    icon = Icons.Default.PieChart,
                    color = FinoraOrange,
                    label = "Spending pattern",
                    text = "Your food spending is 27% higher than your 3-month average."
                )
            }

            item {
                InsightPillRow(
                    icon = Icons.Default.Flag,
                    color = FinoraLime,
                    label = "Goal",
                    text = "You're 64% towards your emergency fund goal."
                )
            }

            item {
                InsightPillRow(
                    icon = Icons.Default.Lightbulb,
                    color = FinoraCyan,
                    label = "Opportunity",
                    text = "If you reduce transport spending by 20%, you could save ~₦6,500/month."
                )
            }

            // Quick Actions: Analyze, Plan, Advise, Simulate
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionChip("Analyze", Icons.Default.PieChart) { viewModel.askAdvisor("Analyze my spending pattern and identify leaks.") }
                    ActionChip("Plan", Icons.Default.Flag) { viewModel.askAdvisor("Help me plan toward saving ₦500,000.") }
                    ActionChip("Advise", Icons.Default.AutoAwesome) { viewModel.askAdvisor("What is my top financial advice right now?") }
                    ActionChip("Simulate", Icons.Default.QuestionMark) { viewModel.navigateTo(Screen.WhatIfSimulator) }
                }
            }

            // Chat Messages
            items(messages, key = { it.id }) { msg ->
                FinoraAdvisorBubble(message = msg)
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = FinoraLime, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ARIA is analyzing your ledger...",
                            style = MaterialTheme.typography.bodySmall,
                            color = FinoraTextSecondary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Suggested Prompt Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                PromptChip("Can I afford a ₦250k phone?") {
                    viewModel.askAdvisor("Can I afford a ₦250,000 phone right now?")
                }
            }
            item {
                PromptChip("Why did I overspend?") {
                    viewModel.askAdvisor("Why did I overspend on food this month?")
                }
            }
            item {
                PromptChip("Analyze my month") {
                    viewModel.askAdvisor("Review my overall financial performance this month.")
                }
            }
            item {
                PromptChip("Why is Safe-to-Spend ₦73.5k?") {
                    viewModel.askAdvisor("Why is my safe to spend ₦73,500?")
                }
            }
        }

        // Bottom Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userQueryText,
                onValueChange = { userQueryText = it },
                placeholder = { Text("Ask me anything about your money...", color = FinoraTextTertiary, fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_advisor_query"),
                shape = RoundedCornerShape(20.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val query = userQueryText
                    userQueryText = ""
                    viewModel.askAdvisor(query)
                },
                enabled = userQueryText.isNotBlank() && !isThinking,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (userQueryText.isNotBlank()) FinoraLime else FinoraCardBgElevated)
                    .testTag("btn_send_advisor_query")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (userQueryText.isNotBlank()) Color(0xFF090E17) else FinoraTextTertiary
                )
            }
        }
    }
}

@Composable
private fun InsightPillRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    label: String,
    text: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
                Text(text, style = MaterialTheme.typography.bodySmall, color = FinoraTextPrimary)
            }
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(FinoraCardBg)
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = FinoraLime, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
        }
    }
}

@Composable
private fun PromptChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(FinoraCardBgElevated)
            .border(1.dp, FinoraCardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = FinoraTextSecondary)
    }
}

@Composable
private fun FinoraAdvisorBubble(message: AdvisorChatMessage) {
    val isUser = message.sender == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) FinoraLime else FinoraCardBg)
                .border(1.dp, if (isUser) Color.Transparent else FinoraCardBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
                .fillMaxWidth(if (isUser) 0.82f else 0.94f)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Color(0xFF090E17) else FinoraTextPrimary,
                lineHeight = 20.sp,
                fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}
