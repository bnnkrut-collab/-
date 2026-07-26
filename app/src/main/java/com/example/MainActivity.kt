package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.MainScreen
import com.example.ui.theme.ChekunetsTheme
import com.example.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {

    private val calculatorViewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChekunetsTheme {
                MainScreen(viewModel = calculatorViewModel)
            }
        }
    }
}
