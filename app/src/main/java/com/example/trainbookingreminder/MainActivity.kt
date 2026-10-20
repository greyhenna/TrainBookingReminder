package com.example.trainbookingreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.trainbookingreminder.ui.screens.MainScreen
import com.example.trainbookingreminder.ui.theme.TrainBookingReminderTheme
import com.example.trainbookingreminder.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            TrainBookingReminderTheme(themeMode = uiState.themeMode) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
