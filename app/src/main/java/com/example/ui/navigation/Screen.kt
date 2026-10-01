package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val title: String,
    val icon: ImageVector,
    val isBottomNav: Boolean = false
) {
    // Core Bottom Nav Destinations
    Dashboard("Home", Icons.Default.Home, true),
    Transactions("Transactions", Icons.AutoMirrored.Filled.ReceiptLong, true),
    Analytics("Analytics", Icons.Default.Analytics, true),
    Advisor("Advisor", Icons.Default.SmartToy, true),

    // Deep Fintech Features (from design spec & reference images)
    Accounts("Accounts", Icons.Default.AccountBalance),
    Budgets("Budgets", Icons.Default.PieChart),
    Goals("Goals", Icons.Default.Flag),
    FinancialHealth("Health", Icons.Default.Speed),
    CanIAfford("Can I Afford?", Icons.Default.QuestionMark),
    FinancialAutopsy("Autopsy", Icons.Default.Assessment),
    NetWorth("Net Worth", Icons.Default.AccountBalance),
    UpcomingBills("Bills", Icons.Default.Receipt),
    DebtManagement("Debts", Icons.Default.Handshake),
    FamilySupport("Family Support", Icons.Default.FamilyRestroom),
    FinancialTimeline("Timeline", Icons.Default.CalendarMonth),
    WhatIfSimulator("What-If", Icons.Default.Timeline),
    Settings("Settings", Icons.Default.Settings),
    Splash("Brand Splash", Icons.Default.AutoAwesome),
    Onboarding("Onboarding", Icons.Default.AutoAwesome)
}
