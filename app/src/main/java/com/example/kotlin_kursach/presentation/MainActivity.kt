package com.example.kotlin_kursach.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kotlin_kursach.presentation.navigation.AppNavigation
import com.example.kotlin_kursach.ui.theme.Kotlin_KursachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kotlin_KursachTheme {
                AppNavigation()
            }
        }
    }
}
