package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FinoraCardBg
import com.example.ui.theme.FinoraCyan
import com.example.ui.theme.FinoraDarkBg
import com.example.ui.theme.FinoraLime
import com.example.ui.theme.FinoraTextPrimary
import com.example.ui.theme.FinoraTextSecondary
import com.example.ui.theme.FinoraTextTertiary

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animAlpha.animateTo(1f, animationSpec = tween(900))
    }

    val glowGradient = Brush.radialGradient(
        colors = listOf(
            FinoraLime.copy(alpha = 0.22f),
            FinoraCyan.copy(alpha = 0.12f),
            Color.Transparent
        ),
        radius = 650f,
        center = Offset(500f, 600f)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FinoraDarkBg)
            .background(glowGradient)
            .padding(24.dp)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Finora Logo Emblem with glowing depth
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(FinoraCardBg),
                contentAlignment = Alignment.Center
            ) {
                // Diagonal ribbon bars representing Finora "F" / financial bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(bottom = 22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(FinoraCyan)
                    )
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(FinoraLime)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Trackie",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FinoraTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your money. Explained.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = FinoraTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Track  •  Understand  •  Plan  •  Improve",
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.2.sp,
                color = FinoraTextTertiary
            )

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("btn_splash_get_started"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinoraLime,
                    contentColor = Color(0xFF090E17)
                )
            ) {
                Text(
                    text = "Get Started",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onLogin,
                modifier = Modifier.testTag("btn_splash_login")
            ) {
                Text(
                    text = "I already have an account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FinoraTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
