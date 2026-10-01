package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Finora Signature Dark-Mode Fintech Palette
val FinoraLime = Color(0xFFD2F824) // Signature bright neon lime
val FinoraLimeMuted = Color(0xFFA8CB16)
val FinoraLimeContainer = Color(0xFF223005)

val FinoraDarkBg = Color(0xFF090E17) // Very dark navy / charcoal background
val FinoraCardBg = Color(0xFF111827) // Surface card
val FinoraCardBgElevated = Color(0xFF162034) // Elevated card
val FinoraCardBorder = Color(0xFF1E2B45) // Subtle card border
val FinoraCardBorderSubtle = Color(0xFF16233B)

val FinoraCyan = Color(0xFF22D3EE) // Secondary positive & transfer
val FinoraPurple = Color(0xFF818CF8) // Analytics & AI (ARIA)
val FinoraOrange = Color(0xFFFB923C) // Warnings & Leaks
val FinoraRed = Color(0xFFF43F5E) // Critical alerts & Expenses

val FinoraTextPrimary = Color(0xFFF8FAFC)
val FinoraTextSecondary = Color(0xFF94A3B8)
val FinoraTextTertiary = Color(0xFF64748B)

// Semantic Financial Colors
val IncomeGreen = Color(0xFF22C55E)
val ExpenseRed = Color(0xFFF43F5E)
val WarningAmber = Color(0xFFFB923C)
val SavingsBlue = Color(0xFF38BDF8)
val InvestmentPurple = Color(0xFF818CF8)
val SafeSpendLime = Color(0xFFD2F824)

// Backward-compatibility aliases
val BrandEmerald = FinoraLime
val BrandMint = FinoraLime
val BrandNavy = FinoraCardBg
val BrandSurfaceNavy = FinoraCardBgElevated
val SafeSpendMint = FinoraLime

// Theme tokens
val FinoraColorScheme = androidx.compose.material3.darkColorScheme(
    primary = FinoraLime,
    onPrimary = Color(0xFF0B1202),
    primaryContainer = FinoraLimeContainer,
    onPrimaryContainer = FinoraLime,
    secondary = FinoraCyan,
    onSecondary = Color(0xFF021B21),
    secondaryContainer = Color(0xFF082F38),
    onSecondaryContainer = FinoraCyan,
    tertiary = FinoraPurple,
    onTertiary = Color(0xFF101438),
    tertiaryContainer = Color(0xFF232A60),
    onTertiaryContainer = Color(0xFFC7D2FE),
    background = FinoraDarkBg,
    onBackground = FinoraTextPrimary,
    surface = FinoraCardBg,
    onSurface = FinoraTextPrimary,
    surfaceVariant = FinoraCardBgElevated,
    onSurfaceVariant = FinoraTextSecondary,
    outline = FinoraCardBorder,
    error = FinoraRed,
    onError = Color.White
)
