package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun AuthScreen(
    viewModel: TrackieViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Modes: 0 = Sign In, 1 = Create Account, 2 = Forgot Password
    var authMode by remember { mutableIntStateOf(0) }

    // Common Credentials
    var email by remember { mutableStateOf("ayoolatosin00@gmail.com") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var stayLoggedIn by remember { mutableStateOf(true) }

    // Extra Profile & Onboarding fields for Create Account
    var fullName by remember { mutableStateOf("Ayoola Tosin") }
    var primaryBank by remember { mutableStateOf("GTBank") }
    var startingBalanceInput by remember { mutableStateOf("50000") }
    var monthlyIncomeInput by remember { mutableStateOf("350000") }
    var savingsTargetInput by remember { mutableStateOf("70000") }

    // Forgot Password specific state
    var resetEmail by remember { mutableStateOf("") }

    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authErrorMessage.collectAsStateWithLifecycle()
    val authSuccess by viewModel.authSuccessMessage.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Premium Trackie Brand Logo Monogram
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(FinoraLime.copy(alpha = 0.15f))
                .border(2.dp, FinoraLime, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(FinoraLime),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "T",
                    color = Color(0xFF090E17),
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Trackie",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = FinoraTextPrimary
        )

        Text(
            text = "Track • Understand • Plan • Improve",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = FinoraTextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main Auth Card Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FinoraCardBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                if (authMode == 2) {
                    // Forgot Password Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                authMode = 0
                                viewModel.clearAuthMessages()
                            },
                            modifier = Modifier.testTag("btn_back_to_login")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Sign In",
                                tint = FinoraLime
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reset Password",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FinoraTextPrimary
                        )
                    }
                    Text(
                        text = "Enter your registered email address to receive password recovery instructions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FinoraTextSecondary,
                        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                    )
                } else {
                    // Segmented Tabs: Sign In vs Create Account
                    TabRow(
                        selectedTabIndex = authMode,
                        containerColor = Color(0xFF0D1424),
                        contentColor = FinoraLime,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[authMode]),
                                color = FinoraLime
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = authMode == 0,
                            onClick = {
                                authMode = 0
                                viewModel.clearAuthMessages()
                            },
                            text = {
                                Text(
                                    "Sign In",
                                    fontWeight = if (authMode == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (authMode == 0) FinoraLime else FinoraTextSecondary
                                )
                            },
                            modifier = Modifier.testTag("tab_auth_signin")
                        )
                        Tab(
                            selected = authMode == 1,
                            onClick = {
                                authMode = 1
                                viewModel.clearAuthMessages()
                            },
                            text = {
                                Text(
                                    "Create Account",
                                    fontWeight = if (authMode == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (authMode == 1) FinoraLime else FinoraTextSecondary
                                )
                            },
                            modifier = Modifier.testTag("tab_auth_signup")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Global Error Banner
                authError?.let { err ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = ExpenseRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodySmall,
                                color = ExpenseRed
                            )
                        }
                    }
                }

                // Global Success Banner
                authSuccess?.let { successMsg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = IncomeGreen.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = IncomeGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = successMsg,
                                style = MaterialTheme.typography.bodySmall,
                                color = IncomeGreen
                            )
                        }
                    }
                }

                AnimatedContent(
                    targetState = authMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "auth_form_content"
                ) { mode ->
                    when (mode) {
                        0 -> {
                            // SIGN IN MODE
                            Column {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email Address") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = FinoraLime)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_auth_email"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Password") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = FinoraLime)
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle password visibility",
                                                tint = FinoraTextSecondary
                                            )
                                        }
                                    },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_auth_password"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Stay Logged In & Forgot Password Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clickable { stayLoggedIn = !stayLoggedIn }
                                            .testTag("toggle_stay_logged_in")
                                    ) {
                                        Checkbox(
                                            checked = stayLoggedIn,
                                            onCheckedChange = { stayLoggedIn = it },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = FinoraLime,
                                                checkmarkColor = Color(0xFF090E17)
                                            )
                                        )
                                        Text(
                                            text = "Stay logged in",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FinoraTextPrimary
                                        )
                                    }

                                    TextButton(
                                        onClick = {
                                            authMode = 2
                                            resetEmail = email
                                            viewModel.clearAuthMessages()
                                        },
                                        modifier = Modifier.testTag("btn_forgot_password")
                                    ) {
                                        Text(
                                            text = "Forgot password?",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FinoraCyan,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        viewModel.signIn(
                                            context = context,
                                            email = email,
                                            pass = password,
                                            stayLoggedIn = stayLoggedIn,
                                            onSuccess = onAuthSuccess
                                        )
                                    },
                                    enabled = email.isNotBlank() && password.length >= 6 && !isAuthLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("btn_auth_submit"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FinoraLime,
                                        contentColor = Color(0xFF090E17),
                                        disabledContainerColor = FinoraLime.copy(alpha = 0.3f),
                                        disabledContentColor = Color(0xFF090E17).copy(alpha = 0.5f)
                                    )
                                ) {
                                    if (isAuthLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF090E17))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Signing In...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Text("Sign In to Trackie", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                    }
                                }
                            }
                        }

                        1 -> {
                            // CREATE ACCOUNT MODE (Collects more user setup details)
                            Column {
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    label = { Text("Full Name / Display Name") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = FinoraLime)
                                    },
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_auth_fullname"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email Address") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = FinoraLime)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_auth_signup_email"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Create Password (min 6 chars)") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = FinoraLime)
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle password visibility",
                                                tint = FinoraTextSecondary
                                            )
                                        }
                                    },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_auth_signup_password"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    label = { Text("Confirm Password") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = FinoraLime)
                                    },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_auth_confirm_password"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Financial Setup Details Header
                                Text(
                                    text = "Your Financial Profile Setup",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = FinoraLime
                                )
                                Text(
                                    text = "Trackie uses these to personalize your Safe-to-Spend & financial health.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FinoraTextTertiary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = primaryBank,
                                    onValueChange = { primaryBank = it },
                                    label = { Text("Primary Bank / Wallet (e.g. GTBank, OPay, Cash)") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = FinoraCyan)
                                    },
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_auth_primary_bank"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = startingBalanceInput,
                                        onValueChange = { startingBalanceInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                        label = { Text("Starting Balance (₦)") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null, tint = FinoraLime)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        colors = authTextFieldColors(),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_auth_starting_balance"),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = monthlyIncomeInput,
                                        onValueChange = { monthlyIncomeInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                        label = { Text("Monthly Income (₦)") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = IncomeGreen)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        colors = authTextFieldColors(),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_auth_monthly_income"),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = savingsTargetInput,
                                    onValueChange = { savingsTargetInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                    label = { Text("Monthly Savings / Reserve Target (₦)") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = FinoraCyan)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_auth_savings_target"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { stayLoggedIn = !stayLoggedIn }
                                        .testTag("toggle_signup_stay_logged_in")
                                ) {
                                    Checkbox(
                                        checked = stayLoggedIn,
                                        onCheckedChange = { stayLoggedIn = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = FinoraLime,
                                            checkmarkColor = Color(0xFF090E17)
                                        )
                                    )
                                    Text(
                                        text = "Keep me always logged in on this device",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FinoraTextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                val isPasswordsMatch = password == confirmPassword || confirmPassword.isBlank()
                                val canSubmitSignUp = fullName.isNotBlank() &&
                                    email.isNotBlank() &&
                                    password.length >= 6 &&
                                    (confirmPassword.isBlank() || confirmPassword == password) &&
                                    !isAuthLoading

                                Button(
                                    onClick = {
                                        val startingBal = startingBalanceInput.toDoubleOrNull() ?: 0.0
                                        val income = monthlyIncomeInput.toDoubleOrNull() ?: 0.0
                                        val reserves = savingsTargetInput.toDoubleOrNull() ?: 0.0

                                        viewModel.signUp(
                                            context = context,
                                            fullName = fullName,
                                            email = email,
                                            pass = password,
                                            primaryBank = primaryBank,
                                            startingBalance = startingBal,
                                            monthlyIncome = income,
                                            reserveTarget = reserves,
                                            stayLoggedIn = stayLoggedIn,
                                            onSuccess = onAuthSuccess
                                        )
                                    },
                                    enabled = canSubmitSignUp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("btn_auth_signup_submit"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FinoraLime,
                                        contentColor = Color(0xFF090E17),
                                        disabledContainerColor = FinoraLime.copy(alpha = 0.3f),
                                        disabledContentColor = Color(0xFF090E17).copy(alpha = 0.5f)
                                    )
                                ) {
                                    if (isAuthLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF090E17))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Creating Account...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Text("Create Account & Start Tracking", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                    }
                                }
                            }
                        }

                        2 -> {
                            // FORGOT PASSWORD MODE
                            Column {
                                OutlinedTextField(
                                    value = resetEmail,
                                    onValueChange = { resetEmail = it },
                                    label = { Text("Registered Email Address") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = FinoraLime)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_reset_password_email"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        viewModel.resetPassword(resetEmail) { _, _ -> }
                                    },
                                    enabled = resetEmail.isNotBlank() && !isAuthLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("btn_submit_forgot_password"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FinoraLime,
                                        contentColor = Color(0xFF090E17),
                                        disabledContainerColor = FinoraLime.copy(alpha = 0.3f),
                                        disabledContentColor = Color(0xFF090E17).copy(alpha = 0.5f)
                                    )
                                ) {
                                    if (isAuthLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF090E17))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Sending Reset Email...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Text("Send Password Reset Link", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                TextButton(
                                    onClick = {
                                        authMode = 0
                                        viewModel.clearAuthMessages()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_cancel_reset_password")
                                ) {
                                    Text(
                                        text = "Remember your password? Sign In",
                                        color = FinoraTextSecondary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instant demo access button (useful for testing and instant preview)
        TextButton(
            onClick = {
                viewModel.continueAsDemo(context, "ayoolatosin00@gmail.com", onAuthSuccess)
            },
            modifier = Modifier.testTag("btn_auth_instant_demo")
        ) {
            Text(
                text = "Instant Guest Access (Demo Mode)",
                style = MaterialTheme.typography.bodySmall,
                color = FinoraTextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your financial data is encrypted and saved privately in your local device database.",
            style = MaterialTheme.typography.labelSmall,
            color = FinoraTextTertiary,
            lineHeight = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = FinoraLime,
    unfocusedBorderColor = FinoraCardBorder,
    focusedLabelColor = FinoraLime,
    unfocusedLabelColor = FinoraTextSecondary,
    focusedTextColor = FinoraTextPrimary,
    unfocusedTextColor = FinoraTextPrimary,
    cursorColor = FinoraLime,
    focusedContainerColor = Color(0xFF090E17),
    unfocusedContainerColor = Color(0xFF090E17)
)
