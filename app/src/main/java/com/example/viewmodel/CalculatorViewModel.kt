package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEffectsManager
import com.example.data.database.AppDatabase
import com.example.data.entity.BarOrderEntity
import com.example.data.preferences.UserPrefs
import com.example.model.BarOrderInfo
import com.example.model.CalculatorState
import com.example.model.ChekushkaState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack
import kotlin.random.Random

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPrefs(application)
    private val barOrderDao = AppDatabase.getDatabase(application).barOrderDao()
    val soundManager = SoundEffectsManager(application)

    private val _calcState = MutableStateFlow(CalculatorState())
    val calcState: StateFlow<CalculatorState> = _calcState.asStateFlow()

    private val _chekushkaState = MutableStateFlow(
        ChekushkaState(
            levelMl = userPrefs.liquidLevelMl,
            isCapClosed = userPrefs.isCapClosed,
            totalDrunkMl = userPrefs.totalDrunkMl,
            accumulatedSum = userPrefs.accumulatedSum,
            barTargetSum = userPrefs.barTargetSum
        )
    )
    val chekushkaState: StateFlow<ChekushkaState> = _chekushkaState.asStateFlow()

    val barOrders = barOrderDao.getAllOrders()
    val barOrderCount = barOrderDao.getOrderCount()

    // Dialog & Overlay states
    private val _showBarOrderDialog = MutableStateFlow<BarOrderInfo?>(null)
    val showBarOrderDialog: StateFlow<BarOrderInfo?> = _showBarOrderDialog.asStateFlow()

    private val _showEasterEgg = MutableStateFlow<String?>(null) // "1+1" or "2+2"
    val showEasterEgg: StateFlow<String?> = _showEasterEgg.asStateFlow()

    private val _showHistoryDialog = MutableStateFlow(false)
    val showHistoryDialog: StateFlow<Boolean> = _showHistoryDialog.asStateFlow()

    private val _isDrinkingAnimation = MutableStateFlow(false)
    val isDrinkingAnimation: StateFlow<Boolean> = _isDrinkingAnimation.asStateFlow()

    private val decimalFormat = DecimalFormat("#,##0.######", DecimalFormatSymbols(Locale.US))

    init {
        // Collect order count to keep state synced
        viewModelScope.launch {
            barOrderCount.collect { count ->
                _chekushkaState.value = _chekushkaState.value.copy(totalBarOrders = count)
            }
        }
    }

    // --- Calculator Actions ---

    fun onDigit(digit: String) {
        soundManager.playButtonClick()
        val current = _calcState.value.displayExpression
        _calcState.value = _calcState.value.copy(
            displayExpression = current + digit,
            error = null
        )
    }

    fun onOperator(op: String) {
        soundManager.playButtonClick()
        val current = _calcState.value.displayExpression
        if (current.isEmpty()) {
            if (op == "-") {
                _calcState.value = _calcState.value.copy(displayExpression = "-")
            }
            return
        }
        val lastChar = current.last()
        val expression = if (lastChar in listOf('+', '-', '×', '÷', '%', '*')) {
            current.dropLast(1) + op
        } else {
            current + op
        }
        _calcState.value = _calcState.value.copy(displayExpression = expression, error = null)
    }

    fun onClear() {
        soundManager.playButtonClick()
        _calcState.value = CalculatorState()
    }

    fun onDelete() {
        soundManager.playButtonClick()
        val current = _calcState.value.displayExpression
        if (current.isNotEmpty()) {
            _calcState.value = _calcState.value.copy(
                displayExpression = current.dropLast(1),
                error = null
            )
        }
    }

    fun onToggleSign() {
        soundManager.playButtonClick()
        val current = _calcState.value.displayExpression
        if (current.isEmpty()) return
        if (current.startsWith("-")) {
            _calcState.value = _calcState.value.copy(displayExpression = current.drop(1))
        } else {
            _calcState.value = _calcState.value.copy(displayExpression = "-$current")
        }
    }

    fun onParenthesis() {
        soundManager.playButtonClick()
        val current = _calcState.value.displayExpression
        val openCount = current.count { it == '(' }
        val closeCount = current.count { it == ')' }
        val nextParen = if (openCount > closeCount && current.lastOrNull()?.isDigit() == true) ")" else "("
        _calcState.value = _calcState.value.copy(displayExpression = current + nextParen)
    }

    fun onEquals() {
        soundManager.playButtonClick()
        val rawExpression = _calcState.value.displayExpression.trim()
        if (rawExpression.isEmpty()) return

        val cleanExpr = rawExpression.replace(" ", "")

        // Check Easter Egg first (1+1 or 2+2)
        if (cleanExpr == "1+1" || cleanExpr == "2+2") {
            _showEasterEgg.value = cleanExpr
        }

        try {
            val evalResult = evaluateExpression(rawExpression)
            val formattedResult = decimalFormat.format(evalResult)

            _calcState.value = _calcState.value.copy(
                currentResult = formattedResult,
                lastExpression = rawExpression,
                displayExpression = formattedResult,
                error = null
            )

            // Fill Chekushka & accumulate sum towards bar order
            fillChekushkaAndCheckBarOrder(evalResult)

        } catch (e: Exception) {
            _calcState.value = _calcState.value.copy(error = "Ошибка вычисления")
        }
    }

    // --- Chekushka & Bar Actions ---

    private fun fillChekushkaAndCheckBarOrder(calculatedValue: Double) {
        val absoluteVal = Math.abs(calculatedValue)
        // Add liquid: at least 25ml per calculation, or based on sum
        val addedMl = if (absoluteVal in 1.0..1000.0) {
            (25 + (absoluteVal / 20).toInt()).coerceIn(25, 100)
        } else {
            25
        }

        val currentLevel = _chekushkaState.value.levelMl
        val newLevel = (currentLevel + addedMl).coerceAtMost(UserPrefs.MAX_VOLUME_ML)
        val addedSum = if (absoluteVal > 0) absoluteVal else 100.0
        val newAccumulated = _chekushkaState.value.accumulatedSum + addedSum

        userPrefs.liquidLevelMl = newLevel
        userPrefs.accumulatedSum = newAccumulated

        val target = _chekushkaState.value.barTargetSum

        _chekushkaState.value = _chekushkaState.value.copy(
            levelMl = newLevel,
            accumulatedSum = newAccumulated
        )

        // Trigger Auto Bar Order if bottle is 100% full (250ml) OR sum threshold reached!
        if (newLevel >= UserPrefs.MAX_VOLUME_ML || newAccumulated >= target) {
            triggerAutoBarOrder(newAccumulated)
        }
    }

    fun toggleCap() {
        val nextClosed = !_chekushkaState.value.isCapClosed
        userPrefs.isCapClosed = nextClosed

        _chekushkaState.value = _chekushkaState.value.copy(
            isCapClosed = nextClosed,
            tauntMessage = if (nextClosed) "Чекушка закручена! Хе-хе-хе!" else "Чекушка открыта! Можно наливать и пить!"
        )

        if (nextClosed) {
            // Play mocking laugh sound when capping!
            soundManager.playMockingLaugh()
        }

        viewModelScope.launch {
            delay(3000)
            _chekushkaState.value = _chekushkaState.value.copy(tauntMessage = null)
        }
    }

    fun drinkChekushka() {
        val state = _chekushkaState.value
        if (state.isCapClosed) {
            soundManager.playMockingLaugh()
            _chekushkaState.value = state.copy(
                tauntMessage = "Сначала открутите крышку, чтобы выпить!"
            )
            viewModelScope.launch {
                delay(3000)
                _chekushkaState.value = _chekushkaState.value.copy(tauntMessage = null)
            }
            return
        }

        if (state.levelMl <= 0) {
            soundManager.playEmptyBottleSound()
            _chekushkaState.value = state.copy(
                tauntMessage = "Чекушка пуста! Посчитайте еще, чтобы налить!"
            )
            viewModelScope.launch {
                delay(3000)
                _chekushkaState.value = _chekushkaState.value.copy(tauntMessage = null)
            }
            return
        }

        // Drink action
        viewModelScope.launch {
            _isDrinkingAnimation.value = true
            soundManager.playDrinkSound()

            val initialMl = state.levelMl
            val steps = 10
            for (i in steps downTo 0) {
                delay(120)
                val currentMl = (initialMl * i) / steps
                _chekushkaState.value = _chekushkaState.value.copy(levelMl = currentMl)
            }

            // Finished drinking -> play empty bottle sound!
            soundManager.playEmptyBottleSound()
            _isDrinkingAnimation.value = false

            val totalDrunk = userPrefs.totalDrunkMl + initialMl
            userPrefs.totalDrunkMl = totalDrunk
            userPrefs.liquidLevelMl = 0

            _chekushkaState.value = _chekushkaState.value.copy(
                levelMl = 0,
                totalDrunkMl = totalDrunk,
                tauntMessage = "Ахх! Чекушка выпита до дна! (+${initialMl}мл)"
            )

            delay(3500)
            _chekushkaState.value = _chekushkaState.value.copy(tauntMessage = null)
        }
    }

    private fun triggerAutoBarOrder(totalSum: Double) {
        val orderCode = "CHK-${Random.nextInt(1000, 9999)}"
        val drinkNames = listOf(
            "Чекушка 'Душевная №3' (250мл)",
            "Фирменный Напиток Калькулятора",
            "Ледяная Стопка Бармена",
            "Коктейль '2+2=4'"
        )
        val selectedDrink = drinkNames.random()
        val volumeOrdered = 250

        val orderEntity = BarOrderEntity(
            orderCode = orderCode,
            drinkName = selectedDrink,
            volumeMl = volumeOrdered,
            totalCalculationSum = totalSum
        )

        viewModelScope.launch {
            barOrderDao.insertOrder(orderEntity)
            soundManager.playBarOrderChime()

            val orderInfo = BarOrderInfo(
                orderCode = orderCode,
                drinkName = selectedDrink,
                volumeMl = volumeOrdered,
                totalSum = totalSum,
                timestamp = System.currentTimeMillis()
            )

            // Reset accumulated sum
            userPrefs.accumulatedSum = 0.0
            _chekushkaState.value = _chekushkaState.value.copy(accumulatedSum = 0.0)

            _showBarOrderDialog.value = orderInfo
        }
    }

    fun dismissBarOrderDialog() {
        _showBarOrderDialog.value = null
    }

    fun dismissEasterEgg() {
        _showEasterEgg.value = null
    }

    fun toggleHistoryDialog(show: Boolean) {
        _showHistoryDialog.value = show
    }

    fun updateBarTargetSum(newTarget: Double) {
        userPrefs.barTargetSum = newTarget
        _chekushkaState.value = _chekushkaState.value.copy(barTargetSum = newTarget)
    }

    // --- Expression Evaluator ---

    private fun evaluateExpression(expr: String): Double {
        val sanitized = expr.replace("×", "*").replace("÷", "/")
        return parseAndEval(sanitized)
    }

    private fun parseAndEval(expression: String): Double {
        val tokens = tokenize(expression)
        val rpn = shuntingYard(tokens)
        return evalRpn(rpn)
    }

    private fun tokenize(expr: String): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isWhitespace() -> i++
                c in "0123456789." -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    result.add(sb.toString())
                }
                c in "+-*/%()" -> {
                    result.add(c.toString())
                    i++
                }
                else -> i++
            }
        }
        return result
    }

    private fun shuntingYard(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operators = Stack<String>()

        fun precedence(op: String): Int = when (op) {
            "+", "-" -> 1
            "*", "/", "%" -> 2
            else -> 0
        }

        var prevToken: String? = null

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token == "(" -> operators.push(token)
                token == ")" -> {
                    while (operators.isNotEmpty() && operators.peek() != "(") {
                        output.add(operators.pop())
                    }
                    if (operators.isNotEmpty() && operators.peek() == "(") {
                        operators.pop()
                    }
                }
                else -> { // Operator
                    // Unary minus handling
                    if (token == "-" && (prevToken == null || prevToken == "(" || prevToken in "+-*/%")) {
                        output.add("0") // Convert -x to 0 - x
                    }
                    while (operators.isNotEmpty() && precedence(operators.peek()) >= precedence(token)) {
                        output.add(operators.pop())
                    }
                    operators.push(token)
                }
            }
            prevToken = token
        }

        while (operators.isNotEmpty()) {
            output.add(operators.pop())
        }

        return output
    }

    private fun evalRpn(tokens: List<String>): Double {
        val stack = Stack<Double>()
        for (token in tokens) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.push(num)
            } else {
                val b = if (stack.isNotEmpty()) stack.pop() else 0.0
                val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                val result = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> if (b != 0.0) a / b else throw ArithmeticException("Division by zero")
                    "%" -> a % b
                    else -> 0.0
                }
                stack.push(result)
            }
        }
        return if (stack.isNotEmpty()) stack.pop() else 0.0
    }
}
