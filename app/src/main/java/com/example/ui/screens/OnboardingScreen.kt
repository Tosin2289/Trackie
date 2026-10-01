package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraCardBgElevated
import com.example.ui.theme.FinoraCardBorder
import com.example.ui.theme.FinoraCyan
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraPurple
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.theme.FinoraTextTertiary
import com.example.ui.theme.IncomeGreen

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val previewType: String
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        OnboardingStep(
            title = "Know where your money goes.",
            subtitle = "Automatic categorization, real-time balance tracking, and smart leak detection.",
            icon = Icons.Default.PieChart,
            iconColor = FinoraCyan,
            previewType = "SPENDING"
        ),
        OnboardingStep(
            title = "Know what you can safely spend.",
            subtitle = "Spend with confidence without risking upcoming bills or tapping emergency reserves.",
            icon = Icons.Default.Shield,
            iconColor = FinoraLime,
            previewType = "SAFE_SPEND"
        ),
        OnboardingStep(
            title = "Build toward what matters.",
            subtitle = "Target-driven savings goals with automated recalculations and milestone forecasts.",
            icon = Icons.Default.Flag,
            iconColor = FinoraLime,
            previewType = "GOALS"
        ),
        OnboardingStep(
            title = "Meet your AI Financial Advisor.",
            subtitle = "ARIA analyzes your patterns and evaluates purchases with 'Can I Afford This?'",
            icon = Icons.Default.AutoAwesome,
            iconColor = FinoraPurple,
            previewType = "ADVISOR"
        )
    )

    var currentStepIdx by remember { mutableStateOf(0) }
    val step = steps[currentStepIdx]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .padding(24.dp)
            .testTag("onboarding_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onFinish,
                modifier = Modifier.testTag("btn_onboarding_skip")
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FinoraTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Visual Illustration Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, FinoraCardBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = FinoraCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(step.iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = step.iconColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    when (step.previewType) {
                        "SPENDING" -> {
                            SpendingPreviewCard()
                        }
                        "SAFE_SPEND" -> {
                            SafeSpendPreviewCard()
                        }
                        "GOALS" -> {
                            GoalsPreviewCard()
                        }
                        else -> {
                            AdvisorPreviewCard()
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Titles
        Text(
            text = step.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = FinoraTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = step.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = FinoraTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Dot indicators
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            steps.indices.forEach { idx ->
                val isSel = idx == currentStepIdx
                Box(
                    modifier = Modifier
                        .width(if (isSel) 24.dp else 8.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(if (isSel) FinoraLime else FinoraCardBorder)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Next / Get Started button
        Button(
            onClick = {
                if (currentStepIdx < steps.lastIndex) {
                    currentStepIdx++
                } else {
                    onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .testTag("btn_onboarding_next"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FinoraLime,
                contentColor = Color(0xFF090E17)
            )
        ) {
            Text(
                text = if (currentStepIdx == steps.lastIndex) "Get Started" else "Continue",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SpendingPreviewCard() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Monthly Inflow", style = MaterialTheme.typography.labelSmall, color = FinoraTextSecondary)
            Text("₦320,000", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FinoraCyan)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Monthly Outflow", style = MaterialTheme.typography.labelSmall, color = FinoraTextSecondary)
            Text("₦247,600", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
        }
    }
}

@Composable
private fun SafeSpendPreviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBgElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SAFE TO SPEND", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraLime)
            Text("₦73,500", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = FinoraTextPrimary)
            Text("Available discretionary cushion", style = MaterialTheme.typography.bodySmall, color = FinoraTextTertiary)
        }
    }
}

@Composable
private fun GoalsPreviewCard() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Emergency Fund", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = FinoraTextPrimary)
            Text("38%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = FinoraLime)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { 0.38f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = FinoraLime,
            trackColor = FinoraDarkBg
        )
    }
}

@Composable
private fun AdvisorPreviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = FinoraCardBgElevated)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("ARIA • AI Advisor", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FinoraPurple)
            Text("“You can save ~₦9,640/month by reducing food delivery orders by 20%.”", style = MaterialTheme.typography.bodySmall, color = FinoraTextPrimary)
        }
    }
}
