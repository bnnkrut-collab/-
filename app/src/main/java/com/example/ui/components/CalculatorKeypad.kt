package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalcBtnEqualBg
import com.example.ui.theme.CalcBtnNumBg
import com.example.ui.theme.CalcBtnNumBorder
import com.example.ui.theme.CalcBtnOpBg
import com.example.ui.theme.CalcBtnOpText
import com.example.ui.theme.CalcBtnUtilBg
import com.example.ui.theme.GeoTextMain

@Composable
fun CalculatorKeypad(
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onToggleSign: () -> Unit,
    onParenthesis: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: C, ⌫, ( ), ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton("C", CalcBtnUtilBg, GeoTextMain, null, Modifier.weight(1f)) { onClear() }
            CalcButton("⌫", CalcBtnUtilBg, GeoTextMain, null, Modifier.weight(1f)) { onDelete() }
            CalcButton("( )", CalcBtnUtilBg, GeoTextMain, null, Modifier.weight(1f)) { onParenthesis() }
            CalcButton("÷", CalcBtnUtilBg, GeoTextMain, null, Modifier.weight(1f)) { onOperator("÷") }
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton("7", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("7") }
            CalcButton("8", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("8") }
            CalcButton("9", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("9") }
            CalcButton("×", CalcBtnOpBg, CalcBtnOpText, null, Modifier.weight(1f)) { onOperator("×") }
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton("4", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("4") }
            CalcButton("5", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("5") }
            CalcButton("6", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("6") }
            CalcButton("−", CalcBtnOpBg, CalcBtnOpText, null, Modifier.weight(1f)) { onOperator("-") }
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton("1", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("1") }
            CalcButton("2", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("2") }
            CalcButton("3", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("3") }
            CalcButton("+", CalcBtnOpBg, CalcBtnOpText, null, Modifier.weight(1f)) { onOperator("+") }
        }

        // Row 5: ±, 0, ., РОВНО
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton("±", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onToggleSign() }
            CalcButton("0", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit("0") }
            CalcButton(".", CalcBtnNumBg, GeoTextMain, CalcBtnNumBorder, Modifier.weight(1f)) { onDigit(".") }
            CalcButton("РОВНО", CalcBtnEqualBg, Color.White, null, Modifier.weight(1.2f), fontSize = 15.sp) { onEquals() }
        }
    }
}

@Composable
private fun CalcButton(
    text: String,
    bgColor: Color,
    textColor: Color,
    borderColor: Color? = null,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    var surfaceModifier = modifier
        .height(54.dp)
        .clip(shape)

    if (borderColor != null) {
        surfaceModifier = surfaceModifier.border(1.dp, borderColor, shape)
    }

    Surface(
        modifier = surfaceModifier
            .clickable { onClick() }
            .testTag("calc_btn_$text"),
        color = bgColor,
        shape = shape
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
