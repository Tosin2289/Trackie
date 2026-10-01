package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.TrackieDatabase
import com.example.data.repository.TrackieRepository
import com.example.ui.navigation.Screen
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.AddTransactionDialog
import com.example.ui.screens.AdvisorScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.CanIAffordScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DebtManagementScreen
import com.example.ui.screens.FamilySupportScreen
import com.example.ui.screens.FinancialAutopsyScreen
import com.example.ui.screens.FinancialHealthScreen
import com.example.ui.screens.FinancialTimelineScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.NetWorthScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.UpcomingBillsScreen
import com.example.ui.screens.WhatIfSimulatorScreen
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TrackieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val coroutineScope = rememberCoroutineScope()
                val database = remember { TrackieDatabase.getDatabase(applicationContext, coroutineScope) }
                val repository = remember { TrackieRepository(database.trackieDao()) }
                val viewModel = remember {
                    TrackieViewModel(repository).apply {
                        initializeSession(applicationContext)
                    }
                }

                TrackieApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackieApp(viewModel: TrackieViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val backStack by viewModel.backStack.collectAsStateWithLifecycle()
    val showAddTx by viewModel.showAddTransactionSheet.collectAsStateWithLifecycle()
    val showAuth by viewModel.showAuthScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    if (showAuth || currentUser == null) {
        BackHandler(enabled = currentUser != null) {
            viewModel.showAuthScreen.value = false
        }
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = { viewModel.showAuthScreen.value = false }
        )
        return
    }

    // Android back navigation handler
    BackHandler(enabled = backStack.size > 1) {
        viewModel.navigateBack()
    }

    val showChrome = currentScreen != Screen.Splash && currentScreen != Screen.Onboarding

    Scaffold(
        topBar = {
            if (showChrome) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(FinoraLime),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "T",
                                    color = Color(0xFF090E17),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                            Text(
                                text = "  Trackie",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = FinoraTextPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.showAuthScreen.value = true },
                            modifier = Modifier.testTag("topbar_btn_auth")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Account & Profile",
                                tint = FinoraLime
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Accounts) },
                            modifier = Modifier.testTag("topbar_btn_accounts")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Accounts and Net Worth",
                                tint = if (currentScreen == Screen.Accounts) FinoraLime else FinoraTextSecondary
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Settings) },
                            modifier = Modifier.testTag("topbar_btn_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = if (currentScreen == Screen.Settings) FinoraLime else FinoraTextSecondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = FinoraDarkBg
                    )
                )
            }
        },
        bottomBar = {
            if (showChrome && currentScreen.isBottomNav) {
                NavigationBar(
                    containerColor = FinoraCardBg,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val bottomNavItems = listOf(
                        Screen.Dashboard,
                        Screen.Transactions,
                        Screen.Analytics,
                        Screen.Advisor
                    )

                    bottomNavItems.forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(screen) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FinoraLime,
                                selectedTextColor = FinoraLime,
                                indicatorColor = FinoraLime.copy(alpha = 0.15f),
                                unselectedIconColor = FinoraTextSecondary,
                                unselectedTextColor = FinoraTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showChrome && currentScreen.isBottomNav) {
                FloatingActionButton(
                    onClick = { viewModel.showAddTransactionSheet.value = true },
                    containerColor = FinoraLime,
                    contentColor = Color(0xFF090E17),
                    modifier = Modifier.testTag("fab_add_transaction")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Transaction"
                    )
                }
            }
        },
        containerColor = FinoraDarkBg,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
                Screen.Transactions -> TransactionsScreen(viewModel = viewModel)
                Screen.Analytics -> AnalyticsScreen(viewModel = viewModel)
                Screen.Advisor -> AdvisorScreen(viewModel = viewModel)
                Screen.Accounts -> AccountsScreen(viewModel = viewModel)
                Screen.Budgets -> BudgetsScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.Goals -> GoalsScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.FinancialHealth -> FinancialHealthScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.CanIAfford -> CanIAffordScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.FinancialAutopsy -> FinancialAutopsyScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.NetWorth -> NetWorthScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.UpcomingBills -> UpcomingBillsScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.DebtManagement -> DebtManagementScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.FamilySupport -> FamilySupportScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.FinancialTimeline -> FinancialTimelineScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.WhatIfSimulator -> WhatIfSimulatorScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.Settings -> SettingsScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                Screen.Splash -> SplashScreen(
                    onGetStarted = { viewModel.navigateTo(Screen.Onboarding) },
                    onLogin = { viewModel.showAuthScreen.value = true }
                )
                Screen.Onboarding -> OnboardingScreen(
                    onFinish = { viewModel.navigateTo(Screen.Dashboard) }
                )
            }
        }
    }

    // Global Add Transaction Dialog
    if (showAddTx) {
        AddTransactionDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showAddTransactionSheet.value = false }
        )
    }
}
