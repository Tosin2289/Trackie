package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.model.AccountEntity
import com.example.data.local.model.BillEntity
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.DebtEntity
import com.example.data.local.model.GoalEntity
import com.example.data.local.model.TransactionEntity
import com.example.data.local.model.UserProfileEntity
import com.example.data.remote.AuthResult
import com.example.data.remote.SupabaseClient
import com.example.data.remote.SupabaseUser
import com.example.data.remote.AffordabilityAnalysis
import com.example.data.remote.GeminiAdvisorService
import com.example.data.remote.ParsedTransactionResult
import com.example.data.repository.TrackieRepository
import com.example.domain.CashFlowSummary
import com.example.domain.CategorySpendItem
import com.example.domain.FamilySupportItem
import com.example.domain.FinancialAutopsyReport
import com.example.domain.FinancialEngine
import com.example.domain.FinancialHealthReport
import com.example.domain.RecurringExpenseSummary
import com.example.domain.SafeToSpendBreakdown
import com.example.domain.WhatIfSimulationResult
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdvisorChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "advisor"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

class TrackieViewModel(
    private val repository: TrackieRepository
) : ViewModel() {

    // --- Navigation State ---
    private val _currentScreen = MutableStateFlow(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _backStack = MutableStateFlow(listOf(Screen.Dashboard))
    val backStack: StateFlow<List<Screen>> = _backStack.asStateFlow()

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            _backStack.value = _backStack.value + screen
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        val stack = _backStack.value
        return if (stack.size > 1) {
            val newStack = stack.dropLast(1)
            _backStack.value = newStack
            _currentScreen.value = newStack.last()
            true
        } else {
            false
        }
    }

    // --- Raw Database Flows ---
    val accounts: StateFlow<List<AccountEntity>> = repository.accounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.goals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bills: StateFlow<List<BillEntity>> = repository.bills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debts: StateFlow<List<DebtEntity>> = repository.debts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- UI Controls State ---
    val heroDisplayMode = MutableStateFlow("SAFE_TO_SPEND") // "SAFE_TO_SPEND", "TOTAL_MONEY", "NET_WORTH"
    val showSafeSpendSheet = MutableStateFlow(false)
    val showAddTransactionSheet = MutableStateFlow(false)
    val showCanIAffordSheet = MutableStateFlow(false)

    // Transaction Filtering
    val transactionSearchQuery = MutableStateFlow("")
    val transactionTypeFilter = MutableStateFlow("ALL") // "ALL", "EXPENSE", "INCOME", "SAVINGS", "TRANSFER"
    val transactionCategoryFilter = MutableStateFlow("ALL")

    // --- Deterministic Domain Calculations ---
    val safeToSpendBreakdown: StateFlow<SafeToSpendBreakdown> = combine(
        accounts, bills, goals, debts, userProfile
    ) { accs, blls, gls, dbts, prof ->
        FinancialEngine.calculateSafeToSpend(accs, blls, gls, dbts, prof)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SafeToSpendBreakdown(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    )

    val totalMoney: StateFlow<Double> = accounts.combine(userProfile) { accs, _ ->
        FinancialEngine.calculateTotalMoney(accs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netWorth: StateFlow<Double> = combine(accounts, debts) { accs, dbts ->
        FinancialEngine.calculateNetWorth(accs, dbts)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cashFlowSummary: StateFlow<CashFlowSummary> = combine(
        transactions, userProfile
    ) { txs, prof ->
        FinancialEngine.calculateCashFlow(txs, prof)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CashFlowSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    )

    val categorySpends: StateFlow<List<CategorySpendItem>> = combine(
        transactions, categories
    ) { txs, cats ->
        FinancialEngine.calculateSpendingByCategory(txs, cats)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val familySupportItems: StateFlow<List<FamilySupportItem>> = transactions.combine(userProfile) { txs, _ ->
        FinancialEngine.calculateFamilySupport(txs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recurringSummary: StateFlow<RecurringExpenseSummary> = bills.combine(userProfile) { blls, _ ->
        FinancialEngine.calculateRecurringSummary(blls)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecurringExpenseSummary(0.0, 0.0, 0))

    val financialHealth: StateFlow<FinancialHealthReport> = combine(
        cashFlowSummary, safeToSpendBreakdown, goals, debts
    ) { cf, safe, gls, dbts ->
        FinancialEngine.calculateFinancialHealth(cf, safe, gls, dbts)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinancialEngine.calculateFinancialHealth(
            CashFlowSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            SafeToSpendBreakdown(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            emptyList(),
            emptyList()
        )
    )

    val financialAutopsy: StateFlow<FinancialAutopsyReport> = combine(
        cashFlowSummary, categorySpends, goals
    ) { cf, cats, gls ->
        FinancialEngine.generateFinancialAutopsy(cf, cats, gls)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinancialEngine.generateFinancialAutopsy(
            CashFlowSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            emptyList(),
            emptyList()
        )
    )

    // --- Simulator State ---
    val selectedSimulationScenario = MutableStateFlow("BUY_PHONE")
    val simulationParamValue = MutableStateFlow(250000.0)
    val simulationResult: StateFlow<WhatIfSimulationResult> = combine(
        selectedSimulationScenario, simulationParamValue, safeToSpendBreakdown, cashFlowSummary, netWorth
    ) { scenario, param, safe, cf, nw ->
        FinancialEngine.simulateScenario(scenario, param, safe.safeToSpend, cf.expenses, nw)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinancialEngine.simulateScenario("BUY_PHONE", 250000.0, 73500.0, 247600.0, 457500.0)
    )

    fun setSimulation(scenario: String, param: Double) {
        selectedSimulationScenario.value = scenario
        simulationParamValue.value = param
    }

    // --- Can-I-Afford-This State ---
    val affordItemName = MutableStateFlow("New Smartphone")
    val affordItemCost = MutableStateFlow(250000.0)
    val affordResult = MutableStateFlow<AffordabilityAnalysis?>(null)

    fun evaluateAffordability() {
        val analysis = GeminiAdvisorService.evaluateAffordability(
            itemName = affordItemName.value,
            itemCost = affordItemCost.value,
            safeToSpend = safeToSpendBreakdown.value,
            monthlyIncome = cashFlowSummary.value.income
        )
        affordResult.value = analysis
    }

    // --- AI Advisor Chat State ---
    private val _advisorMessages = MutableStateFlow(
        listOf(
            AdvisorChatMessage(
                sender = "advisor",
                content = "Hello Ayoola, I'm Trackie, your personal financial advisor. I have access to your live finances: **₦482,500 Total Money**, **₦73,500 Safe to Spend**, and a **72/100 Financial Health score**.\n\nHow can I help you optimize your money today?"
            )
        )
    )
    val advisorMessages: StateFlow<List<AdvisorChatMessage>> = _advisorMessages.asStateFlow()
    val isAdvisorThinking = MutableStateFlow(false)

    fun askAdvisor(query: String) {
        if (query.isBlank()) return
        val userMsg = AdvisorChatMessage(sender = "user", content = query)
        _advisorMessages.value = _advisorMessages.value + userMsg
        isAdvisorThinking.value = true

        viewModelScope.launch {
            val safe = safeToSpendBreakdown.value
            val cf = cashFlowSummary.value
            val nw = netWorth.value
            val total = totalMoney.value

            val contextJson = """
                {
                  "total_money": $total,
                  "net_worth": $nw,
                  "safe_to_spend": ${safe.safeToSpend},
                  "breakdown": {
                    "liquid_money": ${safe.currentAvailableMoney},
                    "upcoming_bills": ${safe.upcomingBills},
                    "planned_savings": ${safe.plannedSavings},
                    "emergency_reserves": ${safe.requiredReserves}
                  },
                  "monthly_income": ${cf.income},
                  "monthly_expenses": ${cf.expenses},
                  "monthly_saved": ${cf.saved},
                  "savings_rate_pct": ${cf.savingsRatePercentage},
                  "health_score": 72,
                  "top_leak": "Food delivery ₦19,400"
                }
            """.trimIndent()

            val reply = GeminiAdvisorService.askAdvisor(query, contextJson)
            _advisorMessages.value = _advisorMessages.value + AdvisorChatMessage(
                sender = "advisor",
                content = reply
            )
            isAdvisorThinking.value = false
        }
    }

    // --- Natural Language & Transaction Operations ---
    val nlParseInput = MutableStateFlow("")
    val isNlParsing = MutableStateFlow(false)
    val parsedNlResult = MutableStateFlow<ParsedTransactionResult?>(null)

    fun parseNaturalLanguage(text: String, onParsed: (ParsedTransactionResult) -> Unit) {
        if (text.isBlank()) return
        isNlParsing.value = true
        viewModelScope.launch {
            val parsed = GeminiAdvisorService.parseNaturalLanguageTransaction(text)
            parsedNlResult.value = parsed
            isNlParsing.value = false
            onParsed(parsed)
        }
    }

    fun addTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.addTransaction(tx)
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
        }
    }

    fun toggleBillPaid(bill: BillEntity) {
        viewModelScope.launch {
            repository.toggleBillPaid(bill.id, !bill.isPaid)
        }
    }

    fun contributeToGoal(goalId: Long, amount: Double) {
        viewModelScope.launch {
            repository.contributeToGoal(goalId, amount)
        }
    }

    fun addAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.addAccount(account)
        }
    }

    fun addGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.addGoal(goal)
        }
    }

    fun addBill(bill: BillEntity) {
        viewModelScope.launch {
            repository.addBill(bill)
        }
    }

    fun clearAllUserData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    // --- Supabase Authentication State ---
    val currentUser = MutableStateFlow<SupabaseUser?>(null)
    val isAuthLoading = MutableStateFlow(false)
    val authErrorMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage = MutableStateFlow<String?>(null)
    val showAuthScreen = MutableStateFlow(false)

    fun clearAuthMessages() {
        authErrorMessage.value = null
        authSuccessMessage.value = null
    }

    fun initializeSession(context: android.content.Context) {
        val session = SupabaseClient.loadSession(context)
        if (session != null) {
            currentUser.value = session.user
            showAuthScreen.value = false
        } else {
            currentUser.value = null
            showAuthScreen.value = true
        }
    }

    fun signUp(
        context: android.content.Context,
        fullName: String,
        email: String,
        pass: String,
        primaryBank: String,
        startingBalance: Double,
        monthlyIncome: Double,
        reserveTarget: Double,
        stayLoggedIn: Boolean,
        onSuccess: () -> Unit
    ) {
        isAuthLoading.value = true
        clearAuthMessages()
        viewModelScope.launch {
            when (val res = SupabaseClient.signUp(email, pass)) {
                is AuthResult.Success -> {
                    currentUser.value = res.user
                    authSuccessMessage.value = res.message
                    isAuthLoading.value = false

                    val profile = UserProfileEntity(
                        id = 1,
                        userName = fullName.ifBlank { "User" },
                        email = res.user.email,
                        supabaseUserId = res.user.id,
                        monthlyIncomeEstimate = monthlyIncome,
                        monthlyReserveTarget = reserveTarget
                    )
                    repository.updateProfile(profile)

                    if (startingBalance > 0 || primaryBank.isNotBlank()) {
                        val bankAccount = AccountEntity(
                            name = primaryBank.ifBlank { "Primary Bank" },
                            type = "BANK",
                            balance = startingBalance,
                            institution = primaryBank.ifBlank { "Bank" },
                            isAsset = true
                        )
                        repository.addAccount(bankAccount)
                    }

                    SupabaseClient.saveSession(
                        context,
                        com.example.data.remote.SupabaseSession(
                            accessToken = "supabase_token_${System.currentTimeMillis()}",
                            tokenType = "Bearer",
                            user = res.user
                        ),
                        stayLoggedIn
                    )
                    onSuccess()
                }
                is AuthResult.NeedsConfirmation -> {
                    authSuccessMessage.value = res.message
                    isAuthLoading.value = false
                }
                is AuthResult.Error -> {
                    authErrorMessage.value = res.message
                    isAuthLoading.value = false
                }
            }
        }
    }

    fun signIn(
        context: android.content.Context,
        email: String,
        pass: String,
        stayLoggedIn: Boolean,
        onSuccess: () -> Unit
    ) {
        isAuthLoading.value = true
        clearAuthMessages()
        viewModelScope.launch {
            when (val res = SupabaseClient.signIn(email, pass)) {
                is AuthResult.Success -> {
                    currentUser.value = res.user
                    authSuccessMessage.value = res.message
                    isAuthLoading.value = false
                    userProfile.value?.let { prof ->
                        repository.updateProfile(prof.copy(email = res.user.email, supabaseUserId = res.user.id))
                    } ?: run {
                        repository.updateProfile(
                            UserProfileEntity(
                                id = 1,
                                userName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                                email = res.user.email,
                                supabaseUserId = res.user.id
                            )
                        )
                    }
                    SupabaseClient.saveSession(
                        context,
                        com.example.data.remote.SupabaseSession(
                            accessToken = "supabase_token_${System.currentTimeMillis()}",
                            tokenType = "Bearer",
                            user = res.user
                        ),
                        stayLoggedIn
                    )
                    onSuccess()
                }
                is AuthResult.NeedsConfirmation -> {
                    authSuccessMessage.value = res.message
                    isAuthLoading.value = false
                }
                is AuthResult.Error -> {
                    authErrorMessage.value = res.message
                    isAuthLoading.value = false
                }
            }
        }
    }

    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        if (email.isBlank()) {
            onResult(false, "Please enter your email address.")
            return
        }
        isAuthLoading.value = true
        clearAuthMessages()
        viewModelScope.launch {
            when (val res = SupabaseClient.resetPasswordForEmail(email)) {
                is AuthResult.Success -> {
                    isAuthLoading.value = false
                    authSuccessMessage.value = res.message
                    onResult(true, res.message)
                }
                is AuthResult.Error -> {
                    isAuthLoading.value = false
                    authErrorMessage.value = res.message
                    onResult(false, res.message)
                }
                is AuthResult.NeedsConfirmation -> {
                    isAuthLoading.value = false
                    authSuccessMessage.value = res.message
                    onResult(true, res.message)
                }
            }
        }
    }

    fun continueAsDemo(context: android.content.Context, email: String, onSuccess: () -> Unit) {
        val user = SupabaseUser(
            id = "utqvepqvblwhjnepxdvk-demo",
            email = email,
            isConfirmed = true
        )
        currentUser.value = user
        clearAuthMessages()
        viewModelScope.launch {
            userProfile.value?.let { prof ->
                repository.updateProfile(prof.copy(email = email, supabaseUserId = user.id))
            } ?: run {
                repository.updateProfile(
                    UserProfileEntity(
                        id = 1,
                        userName = "Demo User",
                        email = email,
                        supabaseUserId = user.id
                    )
                )
            }
        }
        SupabaseClient.saveSession(
            context,
            com.example.data.remote.SupabaseSession(
                accessToken = "demo_token",
                tokenType = "Bearer",
                user = user
            ),
            stayLoggedIn = true
        )
        onSuccess()
    }

    fun signOut(context: android.content.Context? = null) {
        viewModelScope.launch {
            SupabaseClient.signOut(null)
            if (context != null) {
                SupabaseClient.clearSession(context)
            }
            currentUser.value = null
            showAuthScreen.value = true
        }
    }
}
