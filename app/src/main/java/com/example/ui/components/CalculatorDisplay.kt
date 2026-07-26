package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorState
import com.example.model.ChekushkaState
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMain
import com.example.ui.theme.GeoTextMuted

@Composable
fun CalculatorDisplay(
    calcState: CalculatorState,
    chekushkaState: ChekushkaState,
    onOpenHistory: () -> Unit,
    onEquals: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GeoOutline, RoundedCornerShape(28.dp))
            .testTag("calculator_display_card"),
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            // Top Badge & History Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = GeoPrimaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalDrink,
                            contentDescription = "Барный заказ",
                            tint = GeoPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "ЗАКАЗ В БАРЕ: ${chekushkaState.accumulatedSum.toInt()} / ${chekushkaState.barTargetSum.toInt()} ₽",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoOnPrimaryContainer
                        )
                    }
                }

                IconButton(
                    onClick = onOpenHistory,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("bar_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "История заказов в баре",
                        tint = GeoTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            val barProgress = (chekushkaState.accumulatedSum / chekushkaState.barTargetSum).toFloat().coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { barProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GeoPrimary,
                trackColor = GeoSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Row with "РОВНО" button on the left (where user indicated in image) & Expression/Result on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button "РОВНО" placed on the left side
                Button(
                    onClick = onEquals,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GeoPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("display_equals_button")
                ) {
                    Text(
                        text = "РОВНО =",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Expression and Current Result
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (calcState.displayExpression.isEmpty()) "0" else calcState.displayExpression,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = GeoTextMuted,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = calcState.currentResult,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Light,
                        color = GeoTextMain,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Error Banner if calculation error
            calcState.error?.let { err ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
