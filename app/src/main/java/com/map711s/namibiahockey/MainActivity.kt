package com.map711s.namibiahockey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.map711s.namibiahockey.navigation.NamibiaHockeyNavHost
import com.map711s.namibiahockey.theme.NamibiaHockeyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NamibiaHockeyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NamibiaHockeyApp()
                }
            }
        }
    }
}

@Composable
fun NamibiaHockeyApp() {
    val navController = rememberNavController()

    NamibiaHockeyNavHost(navController = navController)
}