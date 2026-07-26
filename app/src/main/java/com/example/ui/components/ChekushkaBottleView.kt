package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.preferences.UserPrefs
import com.example.model.ChekushkaState
import com.example.ui.theme.GeoOnRedContainer
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoRedAccent
import com.example.ui.theme.GeoRedContainer
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMain
import com.example.ui.theme.GeoTextMuted
import com.example.ui.theme.LiquidGradBottom
import com.example.ui.theme.LiquidGradTop

@Composable
fun ChekushkaBottleView(
    state: ChekushkaState,
    isDrinking: Boolean,
    onToggleCap: () -> Unit,
    onDrink: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fillFraction = (state.levelMl.toFloat() / UserPrefs.MAX_VOLUME_ML).coerceIn(0f, 1f)
    val animatedFill by animateFloatAsState(
        targetValue = fillFraction,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "liquidLevel"
    )

    // Wave animation for liquid surface
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Card(
        modifier = modifier
            .border(1.5.dp, GeoOutline, RoundedCornerShape(28.dp))
            .testTag("chekushka_card"),
        colors = CardDefaults.cardColors(containerColor = GeoSurfaceVariant),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header / Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = GeoPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalBar,
                            contentDescription = "Чекушка",
                            tint = GeoPrimary,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "НАЛИТО (${state.levelMl}мл)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary
                    )
                }

                // Cap Lock Button
                Surface(
                    shape = CircleShape,
                    color = if (state.isCapClosed) GeoRedContainer else GeoPrimaryContainer,
                    modifier = Modifier
                        .clickable { onToggleCap() }
                        .testTag("cap_toggle_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (state.isCapClosed) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Крышка",
                            tint = if (state.isCapClosed) GeoRedAccent else GeoPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state.isCapClosed) "ЗАКРЫТА" else "ОТКРЫТА",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isCapClosed) GeoRedAccent else GeoPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottle Container with Liquid Overlay
            Box(
                modifier = Modifier
                    .height(190.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Background Bottle Image
                Image(
                    painter = painterResource(id = R.drawable.img_chekushka),
                    contentDescription = "Чекушка Водки",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Cap Visual Overlay
                if (state.isCapClosed) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 6.dp)
                            .background(GeoRedAccent, RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ЗАКРУЧЕНО 🔒",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                // Interactive Liquid Canvas Overlay
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .width(110.dp)
                        .height(140.dp)
                        .align(Alignment.Center)
                ) {
                    val width = size.width
                    val height = size.height

                    // Define bottle contour path for liquid clipping
                    val bottlePath = Path().apply {
                        moveTo(width * 0.35f, 0f)
                        lineTo(width * 0.65f, 0f)
                        lineTo(width * 0.70f, height * 0.22f)
                        cubicTo(width * 0.95f, height * 0.30f, width * 0.95f, height * 0.85f, width * 0.95f, height)
                        lineTo(width * 0.05f, height)
                        cubicTo(width * 0.05f, height * 0.85f, width * 0.05f, height * 0.30f, width * 0.30f, height * 0.22f)
                        close()
                    }

                    clipPath(bottlePath) {
                        val liquidTop = height * (1f - animatedFill)

                        // Warm Coral Liquid Shader Gradient (Geometric Balance palette)
                        val liquidBrush = Brush.verticalGradient(
                            colors = listOf(
                                LiquidGradTop.copy(alpha = 0.85f),
                                LiquidGradBottom.copy(alpha = 0.95f)
                            ),
                            startY = liquidTop,
                            endY = height
                        )

                        // Draw wave at top of liquid
                        val wavePath = Path().apply {
                            moveTo(0f, height)
                            lineTo(0f, liquidTop)
                            var x = 0f
                            while (x <= width) {
                                val y = liquidTop + Math.sin((x * 0.08 + waveOffset * 0.05)).toFloat() * 4f
                                lineTo(x, y)
                                x += 5f
                            }
                            lineTo(width, height)
                            close()
                        }

                        drawPath(wavePath, brush = liquidBrush)

                        // Liquid Glint / Bubbles
                        if (animatedFill > 0.05f) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.5f),
                                radius = 4f,
                                center = androidx.compose.ui.geometry.Offset(width * 0.3f, liquidTop + 15f)
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.4f),
                                radius = 6f,
                                center = androidx.compose.ui.geometry.Offset(width * 0.65f, liquidTop + 35f)
                            )
                        }
                    }
                }

                // Volume Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, GeoOutline)
                ) {
                    Text(
                        text = "${state.levelMl} / ${UserPrefs.MAX_VOLUME_ML} мл",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextMain,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Drink Button (Red FAB Style from Geometric Balance design)
            Button(
                onClick = { onDrink() },
                enabled = !isDrinking,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("drink_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GeoRedAccent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = "Выпить",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isDrinking) "ПЬЕМ..." else "ВЫПИТЬ ЧЕКУШКУ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Total Drunk Stat Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Всего выпито: ",
                    fontSize = 11.sp,
                    color = GeoTextMuted
                )
                Text(
                    text = "${state.totalDrunkMl} мл",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoRedAccent
                )
            }

            // Taunt message notification if active
            state.tauntMessage?.let { msg ->
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GeoRedContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = GeoOnRedContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}
