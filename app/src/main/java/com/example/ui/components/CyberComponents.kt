package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * CyberCard: A glassmorphic card container with semi-transparent dark background,
 * subtle cyan/purple borders, and depth.
 */
@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = LaserCyan.copy(alpha = 0.25f),
    contentColor: Color = SoftWhite,
    glow: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val glowWidth: Dp = if (glow) 1.2.dp else 0.8.dp
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x0F202025), // Ultra transparent dark card overlay
                        Color(0x06101015)
                    )
                )
            )
            .border(
                width = glowWidth,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.35f),
                        Color(0x0DFFFFFF) // Subtle white border gloss
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(18.dp),
        contentAlignment = Alignment.TopStart
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column {
                content()
            }
        }
    }
}

/**
 * CyberButton: A high-tech neon action button with a sleek cyberpunk edge.
 */
@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color = ElectricBlue,
    textColor: Color = SoftWhite,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (enabled) {
                    Brush.horizontalGradient(
                        colors = listOf(ElectricBlue, NeonPurple)
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(SpaceDeck, SpaceCard)
                    )
                }
            )
            .border(
                width = 1.dp,
                color = if (enabled) LaserCyan else MutedSlate.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            ),
            color = if (enabled) textColor else MutedSlate,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

/**
 * CyberProgressBar: A futuristic tech progress bar with multiple colors and a glowing slider track.
 */
@Composable
fun CyberProgressBar(
    progress: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier,
    barColor: Color = LaserCyan,
    trackColor: Color = Color(0x1BFFFFFF) // white/10 track
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "Progress"
    )
    
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(barColor, barColor.copy(alpha = 0.7f))
                        )
                    )
            )
        }
    }
}

/**
 * SpaceDivider: A simple neon line segment.
 */
@Composable
fun SpaceDivider(
    modifier: Modifier = Modifier,
    color: Color = NeonPurple.copy(alpha = 0.25f)
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        color,
                        Color.Transparent
                    )
                )
            )
    )
}

/**
 * CyberHeader: Title block with tech details.
 */
@Composable
fun CyberHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    accentColor: Color = LaserCyan
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 24.dp)
                    .background(accentColor)
                    .clip(RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = SoftWhite,
                letterSpacing = 1.sp
            )
        }
        Text(
            text = subtitle.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = MutedSlate,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
        )
    }
}
