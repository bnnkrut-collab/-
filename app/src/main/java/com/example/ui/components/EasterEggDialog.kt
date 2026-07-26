package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.GeoOnRedContainer
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoRedAccent
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextMuted

@Composable
fun EasterEggDialog(
    expression: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, GeoOutline, RoundedCornerShape(28.dp))
                .testTag("easter_egg_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = GeoSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚠️ ВНИМАНИЕ! СЕКРЕТНЫЙ РЕЖИМ ($expression)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GeoRedAccent,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Warning image
                Image(
                    painter = painterResource(id = R.drawable.img_obesity_warning),
                    contentDescription = "Предупреждающий знак OBESITY",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Слишком простые вычисления (1+1 и 2+2) замечены! Внимание: легкомысленное отношение к математике ведет к перебору чекушек!",
                    style = MaterialTheme.typography.bodySmall,
                    color = GeoTextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_easter_egg_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GeoRedAccent,
                        contentColor = GeoOnRedContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "ПОНЯЛ, СЧИТАЮ ДАЛЬШЕ!",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
