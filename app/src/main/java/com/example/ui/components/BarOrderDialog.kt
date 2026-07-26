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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.BarOrderInfo
import com.example.ui.theme.GeoOnPrimary
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMain
import com.example.ui.theme.GeoTextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BarOrderDialog(
    orderInfo: BarOrderInfo,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(orderInfo.timestamp))

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, GeoOutline, RoundedCornerShape(28.dp))
                .testTag("bar_order_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = GeoSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = GeoPrimaryContainer,
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalBar,
                        contentDescription = "Барный заказ",
                        tint = GeoPrimary,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🍸 АВТОМАТИЧЕСКИЙ ЗАКАЗ В БАРЕ!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Вы достигли требуемой суммы вычислений! Бармен уже готовит ваш напиток.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GeoTextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Receipt Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = GeoSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth()
                    ) {
                        ReceiptRow("Чек №:", orderInfo.orderCode)
                        ReceiptRow("Напиток:", orderInfo.drinkName)
                        ReceiptRow("Объем:", "${orderInfo.volumeMl} мл")
                        ReceiptRow("Сумма вычислений:", "${orderInfo.totalSum.toInt()} ₽")
                        ReceiptRow("Время заказа:", formattedDate)
                        ReceiptRow("Статус:", "ГОТОВИТСЯ В БАРЕ 🍾")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_bar_order_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GeoPrimary,
                        contentColor = GeoOnPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Отлично",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ПОНЯТНО, ЖДУ ЗАКАЗ!",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = GeoTextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GeoTextMain)
    }
}
