package com.example.randomstringgeneratorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.randomstringgeneratorapp.ui.screen.RandomStringScreen
import com.example.randomstringgeneratorapp.ui.theme.RandomStringGeneratorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Black.toArgb()
            )
        )
        setContent {
            RandomStringGeneratorAppTheme {
                RandomStringScreen()
            }
        }
    }
}