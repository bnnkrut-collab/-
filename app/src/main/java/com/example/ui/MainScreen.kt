package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BarHistoryDialog
import com.example.ui.components.BarOrderDialog
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.ChekushkaBottleView
import com.example.ui.components.EasterEggDialog
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoRedAccent
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextMain
import com.example.ui.theme.GeoTextMuted
import com.example.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: CalculatorViewModel
) {
    val calcState by viewModel.calcState.collectAsStateWithLifecycle()
    val chekushkaState by viewModel.chekushkaState.collectAsStateWithLifecycle()
    val isDrinking by viewModel.isDrinkingAnimation.collectAsStateWithLifecycle()

    val showBarOrderDialog by viewModel.showBarOrderDialog.collectAsStateWithLifecycle()
    val showEasterEgg by viewModel.showEasterEgg.collectAsStateWithLifecycle()
    val showHistoryDialog by viewModel.showHistoryDialog.collectAsStateWithLifecycle()

    val barOrdersHistory by viewModel.barOrders.collectAsStateWithLifecycle(initialValue = emptyList())

    val configuration = LocalConfiguration.current
    val isExpanded = configuration.screenWidthDp >= 600

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GeoPrimary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Калькулятор",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Чекунец Калькулятор",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = GeoTextMain
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleHistoryDialog(true) },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("app_bar_history_icon")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GeoPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    if (chekushkaState.totalBarOrders > 0) {
                                        Badge(
                                            containerColor = GeoRedAccent,
                                            contentColor = Color.White
                                        ) {
                                            Text(text = "${chekushkaState.totalBarOrders}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Liquor,
                                    contentDescription = "Заказы в баре",
                                    tint = GeoPrimary,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GeoBackground,
                    titleContentColor = GeoTextMain
                )
            )
        },
        bottomBar = {
            // Footer: Status Info Bar from Geometric Balance Theme
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, GeoOutline, RoundedCornerShape(0.dp)),
                color = GeoSurfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(GeoRedAccent, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "БАР: ОЖИДАНИЕ СУММЫ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextMuted
                        )
                    }

                    Text(
                        text = "ЗВУК И ВЫПИВАНИЕ АКТИВНЫ",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = GeoTextMuted
                    )
                }
            }
        },
        containerColor = GeoBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GeoBackground)
        ) {
            if (isExpanded) {
                // Wide Screen / Tablet Layout (Side-by-side)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ChekushkaBottleView(
                        state = chekushkaState,
                        isDrinking = isDrinking,
                        onToggleCap = { viewModel.toggleCap() },
                        onDrink = { viewModel.drinkChekushka() },
                        modifier = Modifier
                            .width(300.dp)
                            .fillMaxHeight()
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        CalculatorDisplay(
                            calcState = calcState,
                            chekushkaState = chekushkaState,
                            onOpenHistory = { viewModel.toggleHistoryDialog(true) },
                            onEquals = { viewModel.onEquals() }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CalculatorKeypad(
                            onDigit = { viewModel.onDigit(it) },
                            onOperator = { viewModel.onOperator(it) },
                            onClear = { viewModel.onClear() },
                            onDelete = { viewModel.onDelete() },
                            onToggleSign = { viewModel.onToggleSign() },
                            onParenthesis = { viewModel.onParenthesis() },
                            onEquals = { viewModel.onEquals() },
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                }
            } else {
                // Portrait / Mobile Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .widthIn(max = 600.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ChekushkaBottleView(
                        state = chekushkaState,
                        isDrinking = isDrinking,
                        onToggleCap = { viewModel.toggleCap() },
                        onDrink = { viewModel.drinkChekushka() },
                        modifier = Modifier.fillMaxWidth()
                    )

                    CalculatorDisplay(
                        calcState = calcState,
                        chekushkaState = chekushkaState,
                        onOpenHistory = { viewModel.toggleHistoryDialog(true) },
                        onEquals = { viewModel.onEquals() }
                    )

                    CalculatorKeypad(
                        onDigit = { viewModel.onDigit(it) },
                        onOperator = { viewModel.onOperator(it) },
                        onClear = { viewModel.onClear() },
                        onDelete = { viewModel.onDelete() },
                        onToggleSign = { viewModel.onToggleSign() },
                        onParenthesis = { viewModel.onParenthesis() },
                        onEquals = { viewModel.onEquals() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Dialogs & Overlays
            showBarOrderDialog?.let { orderInfo ->
                BarOrderDialog(
                    orderInfo = orderInfo,
                    onDismiss = { viewModel.dismissBarOrderDialog() }
                )
            }

            showEasterEgg?.let { expr ->
                EasterEggDialog(
                    expression = expr,
                    onDismiss = { viewModel.dismissEasterEgg() }
                )
            }

            if (showHistoryDialog) {
                BarHistoryDialog(
                    orders = barOrdersHistory,
                    onDismiss = { viewModel.toggleHistoryDialog(false) }
                )
            }
        }
    }
}
